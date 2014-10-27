package com.example.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.R;
import com.example.android.model.Book;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ViewHolder extends LinearLayout {
    @InjectView(R.id.bookTitle) TextView bookTitle;
    @InjectView(R.id.bookAuthors) TextView bookAuthors;

    public ViewHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setBook(Book book) {
        bookTitle.setText(book.title());
        bookAuthors.setText(book.author());
    }
}
