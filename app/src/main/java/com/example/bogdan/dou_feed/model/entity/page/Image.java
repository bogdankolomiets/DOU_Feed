package com.example.bogdan.dou_feed.model.entity.page;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bogdan.dou_feed.R;
import com.squareup.picasso.Picasso;

/**
 * @author Bogdan Kolomiets
 * @version 1
 * @date 26.06.16
 */
public class Image extends PageElement {
    private String mUrl;

    public Image(String url) {
        mUrl = url;
    }

    @Override
    void display(LayoutInflater inflater, ViewGroup container) {
        ImageView imageView = (ImageView) inflater.inflate(R.layout.article_image, null);
        Picasso.with(inflater.getContext())
                .load(mUrl)
                .into(imageView);
        container.addView(imageView);
    }
}