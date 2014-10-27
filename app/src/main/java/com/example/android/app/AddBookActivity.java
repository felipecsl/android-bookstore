package com.example.android.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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

public class AddBookActivity extends ActionBarActivity {

    @InjectView(R.id.txtBookTitle) EditText txtBookTitle;
    @InjectView(R.id.txtBookAuthor) EditText txtBookAuthor;
    @InjectView(R.id.txtBookPublisher) EditText txtBookPublisher;
    @InjectView(R.id.txtBookCategories) EditText txtBookCategories;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @Inject ApiClientModule apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        ButterKnife.inject(this);
        ProlificBookstoreApp.get(this).inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_done || itemId == android.R.id.home) {
            confirmUnsavedChangesBeforeLeaving();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        confirmUnsavedChangesBeforeLeaving();
    }

    private void confirmUnsavedChangesBeforeLeaving() {
        String title = txtBookTitle.getText().toString();
        String author = txtBookAuthor.getText().toString();
        String publisher = txtBookPublisher.getText().toString();
        String categories = txtBookCategories.getText().toString();

        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(author) || !TextUtils.isEmpty(publisher)
                || !TextUtils.isEmpty(categories)) {
            new AlertDialog.Builder(AddBookActivity.this)
                    .setTitle("Attention")
                    .setMessage("You have unsaved changes. Are you sure you want to abandon?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            finish();
        }
    }

    @OnClick(R.id.btnSubmit) public void onClickSubmit() {
        boolean failed = false;
        String title = txtBookTitle.getText().toString();
        String author = txtBookAuthor.getText().toString();
        String publisher = txtBookPublisher.getText().toString();
        String categories = txtBookCategories.getText().toString();

        if (TextUtils.isEmpty(title)) {
            txtBookTitle.setError("The book title is required.");
            failed = true;
        }

        if (TextUtils.isEmpty(author)) {
            txtBookAuthor.setError("The book author is required.");
            failed = true;
        }

        if (TextUtils.isEmpty(publisher)) {
            txtBookPublisher.setError("The book publisher is required.");
            failed = true;
        }

        if (TextUtils.isEmpty(categories)) {
            txtBookCategories.setError("The book categories is required.");
            failed = true;
        }

        if (failed) {
            new AlertDialog.Builder(AddBookActivity.this)
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

        apiClient.getService().addBook(author, categories, title, publisher, "Felipe Lima")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Book>() {
                    @Override public void onCompleted() {
                        dialog.dismiss();
                    }

                    @Override public void onError(Throwable e) {
                        dialog.dismiss();

                        new AlertDialog.Builder(AddBookActivity.this)
                                .setTitle("Error")
                                .setMessage("Failed to create book!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                    @Override public void onNext(Book book) {
                        new AlertDialog.Builder(AddBookActivity.this)
                                .setTitle("Message")
                                .setMessage("Book successfully created!")
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
