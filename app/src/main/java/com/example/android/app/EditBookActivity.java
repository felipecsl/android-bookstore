package com.example.android.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
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

public class EditBookActivity extends ActionBarActivity {

    private static final String INTENT_BOOK = "INTENT_BOOK";
    public static final String INTENT_BOOK_URL = "INTENT_BOOK_URL";
    @InjectView(R.id.txtBookTitle) EditText bookTitle;
    @InjectView(R.id.txtBookAuthor) EditText bookAuthors;
    @InjectView(R.id.txtBookPublisher) EditText bookPublisher;
    @InjectView(R.id.txtBookCategories) EditText bookTags;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @Inject ApiClientModule apiClient;
    Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

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
                            Toast.makeText(EditBookActivity.this,
                                    "Failed to retrieve book details", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override public void onNext(Book book) {
                            setBook(book);
                        }
                    });
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void setBook(Book book) {
        this.book = book;
        bookTitle.setText(book.title());
        bookAuthors.setText(book.author());
        bookPublisher.setText(book.publisher());
        bookTags.setText(book.categories());
    }

    @OnClick(R.id.btnSubmit) public void onClickSubmit() {
        boolean failed = false;
        String title = bookTitle.getText().toString();
        String author = bookAuthors.getText().toString();
        String publisher = bookPublisher.getText().toString();
        String categories = bookTags.getText().toString();

        if (TextUtils.isEmpty(title)) {
            bookTitle.setError("The book title is required.");
            failed = true;
        }

        if (TextUtils.isEmpty(author)) {
            bookAuthors.setError("The book author is required.");
            failed = true;
        }

        if (TextUtils.isEmpty(publisher)) {
            bookPublisher.setError("The book publisher is required.");
            failed = true;
        }

        if (TextUtils.isEmpty(categories)) {
            bookTags.setError("The book categories is required.");
            failed = true;
        }

        if (failed) {
            new AlertDialog.Builder(EditBookActivity.this)
                    .setTitle("Error")
                    .setMessage("Please fill all the fields!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return;
        }

        final AlertDialog dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);

        apiClient.getService().updateBook(book.url().substring(1), title, author, categories, publisher)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Book>() {
                    @Override public void onCompleted() {
                        dialog.dismiss();
                    }

                    @Override public void onError(Throwable e) {
                        dialog.dismiss();

                        new AlertDialog.Builder(EditBookActivity.this)
                                .setTitle("Error")
                                .setMessage("Failed to update book!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                    @Override public void onNext(Book book) {
                        new AlertDialog.Builder(EditBookActivity.this)
                                .setTitle("Message")
                                .setMessage("Book successfully Updated!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                });
    }
}
