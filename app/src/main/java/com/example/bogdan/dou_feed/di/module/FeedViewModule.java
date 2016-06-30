package com.example.bogdan.dou_feed.di.module;

import android.support.annotation.NonNull;

import com.example.bogdan.dou_feed.di.ActivityScope;
import com.example.bogdan.dou_feed.lib.Presenter;
import com.example.bogdan.dou_feed.lib.PresenterCache;
import com.example.bogdan.dou_feed.lib.PresenterFactory;
import com.example.bogdan.dou_feed.model.DouModel;
import com.example.bogdan.dou_feed.ui.feed.presenter.FeedPresenter;
import com.example.bogdan.dou_feed.ui.feed.presenter.FeedPresenterImpl;
import com.example.bogdan.dou_feed.ui.feed.view.FeedView;

import dagger.Module;
import dagger.Provides;

/**
 * @author Bogdan Kolomiets
 * @version 1
 * @date 21.06.16
 */
@Module
public class FeedViewModule {
    private FeedView mView;

    public FeedViewModule(FeedView view) {
        mView = view;
    }

    @ActivityScope
    @Provides
    FeedPresenter provideFeedPresenter(DouModel model, PresenterCache cache) {
        return cache.getPresenter(FeedView.class.getName(), new PresenterFactory<FeedPresenterImpl>() {
            @NonNull
            @Override
            public FeedPresenterImpl createPresenter() {
                return new FeedPresenterImpl(model, mView);
            }
        });
    }

}
