package com.example.android;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

public class ProlificBookstoreApp extends Application {
    private ObjectGraph objectGraph;

    @Override public void onCreate() {
        super.onCreate();

        buildObjectGraphAndInject();
    }

    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(new BookstoreModule());
        objectGraph.inject(this);
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }

    public static ProlificBookstoreApp get(Context context) {
        return (ProlificBookstoreApp) context.getApplicationContext();
    }
}
