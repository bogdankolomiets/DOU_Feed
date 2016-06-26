package com.example.bogdan.dou_feed.view;

import com.example.bogdan.dou_feed.model.entity.TableEntity;

/**
 * @author Bogdan Kolomiets
 * @version 1
 * @date 22.06.16
 */
public interface ArticleView extends View {
    String RUBRIC_KEY = "RUBRIC_KEY";

    String URL_KEY = "URL_KEY";

    void showContent(String content);

    void showImage(String imageUrl);

    void showHeading(String heading);

    void showCode(String code);

    void showHead(String author, String date, String title);

    void showLink(String text);

    void showBlockquote(String text);

    void showTable(TableEntity table);

}
