package com.example.android.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import auto.parcel.AutoParcel;

@AutoParcel
@AutoGson
public abstract class Book implements Parcelable {
    public abstract String author();

    public abstract String categories();

    @Nullable public abstract String lastCheckedOut();

    @Nullable public abstract String lastCheckedOutBy();

    @Nullable public abstract String publisher();

    public abstract String title();

    public abstract String url();

    public String lastCheckedOutHumanDate() {
        if (lastCheckedOut() == null)
            return null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date result = df.parse(lastCheckedOut());
            return DateFormat.getDateTimeInstance().format(result);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Book create(String author, String categories, String lastCheckedOut,
                              String lastCheckedOutBy, String publisher, String title, String url) {
        return new AutoParcel_Book(author, categories, lastCheckedOut, lastCheckedOutBy, publisher,
                title, url);
    }
}
