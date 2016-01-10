import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class FileBotRunner implements Runnable {
    Boolean running = false;
    String target, feeds, domHelper;
    int maxArticles, maxArticlesPerFeed, maxFeeds;
    Boolean scramble, alphabetize;
    CompletionListener completionListener;

    public interface CompletionListener {
        void onComplete();
    }

    public FileBotRunner(String target, String feeds, String domHelper, int maxArticles, int maxArticlesPerFeed, int maxFeeds, Boolean scramble, Boolean alphabetize, CompletionListener completionListener) {
        this.target = target;
        this.feeds = feeds;
        this.domHelper = domHelper;
        this.maxArticles = maxArticles;
        this.maxArticlesPerFeed = maxArticlesPerFeed;
        this.maxFeeds = maxFeeds;
        this.scramble = scramble;
        this.alphabetize = alphabetize;
        this.completionListener = completionListener;
    }

    public void run() {
        try {
            if (alphabetize)
                FileTools.alphabetize(feeds);
            if (scramble)
                FileTools.scramble(feeds);
        } catch (IOException e) {
            System.out.println("Unable to scramble or alphabetize due to IO Error.");
        }
        running = true;
        try {
            FeedReader feedReader = new FeedReader(feeds);
            ArticleParser articleParser = new ArticleParser(domHelper);

            int articlesFiled = 0, articlesFiledInFeed = 0, feedsFiled = 0;
            while (feedReader.hasNext() && running) {
                Feed feed = feedReader.nextFeed();
                if (feed == null)
                    continue;

                while (feed.hasNext() && running) {
                    Article article = feed.nextArticle();
                    if (!article.valid) {
                        continue;
                    }
                    article = articleParser.parse(article);
                    article.file(target);

                    articlesFiled++;
                    articlesFiledInFeed++;
                    if (articlesFiled > maxArticles || articlesFiledInFeed > maxArticlesPerFeed)
                        break;
                }

                feedsFiled++;
                if (feedsFiled > maxFeeds || articlesFiled > maxArticles)
                    break;
                articlesFiledInFeed = 0;
            }
        } catch (IOException i) {
            System.out.println("IO Exception, try again or report bug.");
        } catch (URISyntaxException u) {
            System.out.println("URI Exception, try again or report bug.");
        } finally {
            running = false;
            System.out.println("Operation completed.");
            completionListener.onComplete();
        }
    }

    public void stop() {
        System.err.println("Stopping...");
        running = false;
        completionListener.onComplete();
    }
}
