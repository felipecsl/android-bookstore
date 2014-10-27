package com.example.android.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.ProlificBookstoreApp;
import com.example.android.R;
import com.example.android.api.ApiClientModule;
import com.example.android.model.Book;
import com.example.android.widget.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    public static final String ITEMS = "ITEMS";
    @Inject ApiClientModule apiClient;
    @InjectView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.inject(this);
        ProlificBookstoreApp.get(this).inject(this);

        if (savedInstanceState != null) {
            ArrayList<Book> books = savedInstanceState.getParcelableArrayList(ITEMS);
            listView.setAdapter(new BookListAdapter(books));
            listView.setOnItemClickListener(MainActivity.this);
        } else {
            apiClient.getService().listBooks().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Book>>() {
                        @Override public void onCompleted() {
                        }

                        @Override public void onError(Throwable e) {
                            Toast.makeText(MainActivity.this, "Failed to retrieve list of books",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override public void onNext(List<Book> books) {
                            ArrayList<Book> items = new ArrayList<>();
                            items.addAll(books);
                            listView.setAdapter(new BookListAdapter(items));
                            listView.setOnItemClickListener(MainActivity.this);
                        }
                    });
        }
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Book book = (Book) listView.getItemAtPosition(position);
        Intent intent = new Intent(this, BookDetailsActivity.class)
                .putExtra(BookDetailsActivity.INTENT_BOOK_URL, book.url());
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ITEMS,
                ((BookListAdapter) listView.getAdapter()).getBooks());
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        restoreActionBar();
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_add) {
            startActivity(new Intent(this, AddBookActivity.class));
        } else if (itemId == R.id.action_delete_all) {
            apiClient.getService().deleteAllBooks();
            Toast.makeText(this, "All books have been deleted!", Toast.LENGTH_LONG).show();
            listView.setAdapter(new BookListAdapter(new ArrayList<Book>()));
        }
        return super.onOptionsItemSelected(item);
    }

    public class BookListAdapter extends BaseAdapter {
        private ArrayList<Book> books;

        BookListAdapter(ArrayList<Book> books) {
            this.books = books;
        }

        @Override public int getCount() {
            return books.size();
        }

        @Override public Object getItem(int position) {
            return books.get(position);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        public ArrayList<Book> getBooks() {
            return books;
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder v;

            if (convertView == null)
                v = (ViewHolder) getLayoutInflater().inflate(R.layout.book_item,
                        parent, false);
            else
                v = (ViewHolder) convertView;

            v.setBook(books.get(position));

            return v;
        }
    }
}
