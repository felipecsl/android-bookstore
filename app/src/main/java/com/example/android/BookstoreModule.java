package com.example.android;

import com.example.android.api.ApiClientModule;
import com.example.android.app.AddBookActivity;
import com.example.android.app.BookDetailsActivity;
import com.example.android.app.EditBookActivity;
import com.example.android.app.MainActivity;

import dagger.Module;

@Module(
        includes = {ApiClientModule.class},
        injects = {
                ProlificBookstoreApp.class,
                MainActivity.class,
                BookDetailsActivity.class,
                AddBookActivity.class,
                EditBookActivity.class
        }
)
public final class BookstoreModule {
}
