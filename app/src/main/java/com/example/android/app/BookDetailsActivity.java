package com.example.android.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.ProlificBookstoreApp;
import com.example.android.R;
import com.example.android.api.ApiClientModule;
import com.example.android.model.Book;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class BookDetailsActivity extends ActionBarActivity {

    private static final String INTENT_BOOK = "INTENT_BOOK";
    public static final String INTENT_BOOK_URL = "INTENT_BOOK_URL";
    private static final String TAG = "BookDetailsActivity";

    @InjectView(R.id.bookTitle) TextView bookTitle;
    @InjectView(R.id.bookAuthors) TextView bookAuthors;
    @InjectView(R.id.bookPublisher) TextView bookPublisher;
    @InjectView(R.id.bookTags) TextView bookTags;
    @InjectView(R.id.bookLastCheckedOut) TextView bookLastCheckedOut;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @Inject ApiClientModule apiClient;

    Book book;
    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        ButterKnife.inject(this);

        ProlificBookstoreApp.get(this).inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null)
            setBook(savedInstanceState.<Book>getParcelable(INTENT_BOOK));
        else {
            String bookUrl = getIntent().getStringExtra(INTENT_BOOK_URL);
            apiClient.getService().getBook(bookUrl.substring(1))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Book>() {
                        @Override public void onCompleted() {
                        }

                        @Override public void onError(Throwable e) {
                            Toast.makeText(BookDetailsActivity.this,
                                    "Failed to retrieve book details", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "GET book details failed", e);
                            finish();
                        }

                        @Override public void onNext(Book book) {
                            setBook(book);
                        }
                    });
        }
    }

    private void setBook(Book book) {
        this.book = book;
        bookTitle.setText(book.title());
        bookAuthors.setText(book.author());

        if (book.publisher() != null)
            bookPublisher.setText("Publisher: " + book.publisher());
        else
            bookPublisher.setVisibility(View.GONE);
        if (book.categories() != null)
            bookTags.setText("Tags: " + book.categories());
        else
            bookTags.setVisibility(View.GONE);
        if (book.lastCheckedOutBy() != null)
            bookLastCheckedOut.setText("Last Checked Out: " + book.lastCheckedOutBy() + " @ " +
                    book.lastCheckedOutHumanDate());
        else
            bookLastCheckedOut.setVisibility(View.GONE);

        setShareIntent();
    }

    private void setShareIntent() {
        if (mShareActionProvider != null && book != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, book.title());
            intent.putExtra(Intent.EXTRA_TEXT, book.author());
            mShareActionProvider.setShareIntent(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_details, menu);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(
                menu.findItem(R.id.share_button));
        setShareIntent();
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(INTENT_BOOK, book);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
            finish();
        else if (id == R.id.edit_button) {
            startActivity(new Intent(this, EditBookActivity.class)
                    .putExtra(EditBookActivity.INTENT_BOOK_URL, book.url()));
        } else if (id == R.id.delete_button) {
            apiClient.getService().deleteBook(book.url().substring(1))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override public void onCompleted() {

                        }

                        @Override public void onError(Throwable e) {
                            Toast.makeText(BookDetailsActivity.this, "Failed to delete book.",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override public void onNext(String s) {
                            Toast.makeText(BookDetailsActivity.this, "Book deleted!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btnCheckout)
    public void onClickCheckout() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("What's your name?")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        checkoutBook(input.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void checkoutBook(String name) {
        final AlertDialog dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);

        apiClient.getService().checkoutBook(book.url().substring(1), name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Book>() {
                    @Override public void onCompleted() {
                        dialog.dismiss();
                    }

                    @Override public void onError(Throwable e) {
                        dialog.dismiss();
                    }

                    @Override public void onNext(Book book) {
                        bookLastCheckedOut.setText("Last Checked Out: " + book.lastCheckedOutBy()
                                + " @ " + book.lastCheckedOutHumanDate());
                        bookLastCheckedOut.setVisibility(View.VISIBLE);
                        Toast.makeText(BookDetailsActivity.this, "Book successfully checked out!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
