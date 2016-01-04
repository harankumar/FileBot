import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.*;

public class Feed {
    SyndFeed feed;
    List entries;
    int currentEntry = 0;
    public Feed(String url) {
        try {
            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(new URL(url)));
            entries = feed.getEntries();
        } catch (Exception e){
            System.err.println("Error! " + e.getMessage());
        }
    }

    public Article nextArticle() throws IOException {
        Article article = new Article(((SyndEntryImpl) entries.get(currentEntry)));
        currentEntry++;
        return article;
    }

    public boolean hasNext(){
        try {
            return entries.size() > currentEntry;
        } catch (Exception e){
            System.err.println("Error! " + e.getMessage());
            return false;
        }
    }

    public String toString(){
        return feed.getUri();
    }
}