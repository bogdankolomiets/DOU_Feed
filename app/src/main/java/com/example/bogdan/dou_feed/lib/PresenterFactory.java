package com.example.bogdan.dou_feed.lib;

import android.support.annotation.NonNull;

/**
 * @author Bogdan Kolomiets
 * @version 1
 * @date 30.06.16
 */
public interface PresenterFactory<T extends Presenter> {

    @NonNull T createPresenter();
}