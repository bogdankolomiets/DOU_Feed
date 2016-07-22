package com.bogdan_kolomiets_1996.bogdan.dou_feed.api;

import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.CommentItem;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.page.Table;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.article.Article;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.article.ArticleHeader;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.feed.Content;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.feed.FeedItem;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.feed.Footer;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.feed.Header;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.page.Blockquote;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.page.Code;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.page.Heading;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.page.Image;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.page.ListElement;
import com.bogdan_kolomiets_1996.bogdan.dou_feed.model.entity.page.Text;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bogdan Kolomiets
 * @version 1
 * @date 21.06.16
 */
public class DouParser {

  public static List<FeedItem> parseFeed(Document document) {
    List<FeedItem> feed = new ArrayList<>();

    Elements feedItems = document.select(".b-lenta article");

    for (Element item : feedItems) {

      String url = item.select("h2 a").first().attr("href");

      String imageUrl = item.select("h2 a img").first().attr("src");
      String authorName = item.select(".author").first().html();
      String date = item.select(".date").first().text();
      Header header = new Header(imageUrl, authorName, date);

      String title = item.select("h2 a").first().text().replace("&nbsp;", " ");
      String description = item.select(".b-typo").first().text();
      description = deleteCommentCount(description);
      Content content = new Content(title, description);

      int watchCount;
      try {
        watchCount = Integer.parseInt(item.select(".pageviews").first().text());
      } catch (NullPointerException e) {
        watchCount = 0;
      }
      int commentCount;
      try {
        commentCount = Integer.parseInt(item.select(".b-typo a").first().html());
      } catch (NullPointerException e) {
        commentCount = 0;
      }
      String commentUrl;
      try {
        commentUrl = item.select(".b-typo a").first().attr("href");
      } catch (NullPointerException e) {
        commentUrl = null;
      }
      Footer footer = new Footer(watchCount, commentCount, commentUrl);

      FeedItem feedItemEntity = new FeedItem.Builder()
          .url(url)
          .header(header)
          .content(content)
          .footer(footer)
          .build();

      feed.add(feedItemEntity);
    }

    return feed;
  }

  public static Article parseArticle(Document document) {
    String authorName = document.select(".b-post-info .author .name a").first().html();
    String date = document.select(".b-post-info .date").first().text();
    String title = document.select("article.b-typo h1").first().text().replace("&nbsp;", " ");
    ArticleHeader header = new ArticleHeader(authorName, date, title);

    Article articlePage = new Article(header);

    Elements elements = document.select("article.b-typo div").first().children();
    for (Element element : elements) {
      switch (element.tagName()) {
        case "p":
          if (element.hasText()) {
            for (Element children : element.children()) {
              if (children.tagName().equals("src")) {
                articlePage.addElement(new Image(children.attr("src")));
              } else if (children.tagName().equals("a") &&
                  children.children().hasAttr("src")) {
                articlePage.addElement(new Image(children.children().attr("src")));
              }
            }
            articlePage.addElement(new Text(element.text()));
          }
          break;
        case "h1":
        case "h2":
        case "h3":
        case "h4":
        case "h5":
        case "h6":
          articlePage.addElement(new Heading(element.text()));
          break;
        case "pre":
          articlePage.addElement(new Code(element.text()));
          break;
        case "blockquote":
          articlePage.addElement(new Blockquote(element.text()));
          break;
        case "table":
          Table table = new Table();
          for (Element tableElements : element.children()) {
            if (tableElements.tagName().equals("thead")) {
              table.addTableRow();
              for (Element head : tableElements.children().first().children()) {
                table.addRowCell(head.text());
              }
            } else if (tableElements.tagName().equals("tbody")) {
              for (Element row : tableElements.children()) {
                table.addTableRow();
                for (Element column : row.children()) {
                  table.addRowCell(column.text());
                }
              }
            }
          }
          articlePage.addElement(table);
          break;
        case "ul":
          ListElement list = new ListElement();
          for (Element listChildren : element.children()) {
            if (listChildren.tagName().equals("li")) {
              list.add(listChildren.text());
            }
          }
          articlePage.addElement(list);
          break;
      }
      if (element.children().hasAttr("src") && !element.children().hasAttr("scrolling")) {
        articlePage.addElement(new Image(element.children().attr("src")));
      }
    }

    return articlePage;
  }

  public static List<CommentItem> parseComments(Document document) {
    List<CommentItem> commentsList = new ArrayList<>();

    Element commentBlock = document.getElementById("commentsList");
    for (Element commentItem : commentBlock.children()) {
      String imageUrl = commentItem.select("img.g-avatar").first().attr("src");
      String authorName;
      String date;
      try {
        authorName = commentItem.select("a").first().text();
      } catch (NullPointerException e) {
        authorName = "Unknown";
      }
      try {
        date = commentItem.select(".comment-link").first().text();
      } catch (NullPointerException e) {
        date = "";
      }
      Header header = new Header(imageUrl, authorName, date);

      String content;
      try {
        content = commentItem.select(".text.b-typo").first().text();
      } catch (NullPointerException e) {
        content = "";
      }

      CommentItem comment = new CommentItem(header, content);
      commentsList.add(comment);
    }

    return commentsList;
  }

  private static String deleteCommentCount(String text) {
    char[] charText = text.toCharArray();
    int end = charText.length - 1;

    for (int i = end; i >= 0; i--) {
      try {
        Integer.parseInt(String.valueOf(charText[i]));
        end--;
      } catch (NumberFormatException e) {
        break;
      }
    }

    return String.valueOf(charText).substring(0, end + 1);
  }
}
