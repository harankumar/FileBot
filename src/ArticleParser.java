import java.net.*;
import java.util.*;
import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class ArticleParser {
    Map<String, String> domHelper;
    public ArticleParser(String domHelper) throws IOException {
        this.domHelper = new HashMap<String, String>();

        Scanner reader;
        try {
            InputStream input = this.getClass().getClassLoader().getResourceAsStream(domHelper);
            reader = new Scanner(input);
        } catch (Exception e){
            System.out.println("Error");
            reader = new Scanner(new File(domHelper));
        }
        while (reader.hasNext()){
            String url = reader.nextLine();
            String selector = reader.nextLine();

            this.domHelper.put(url, selector);
        }
    }

    public Article parse(Article article) throws URISyntaxException {
        String uri = getDomainName(article.uri);
        String selector = domHelper.get(uri);

        Document doc = Jsoup.parse(article.body);
        Elements a;
        if (selector != null) {
            a = doc.select(selector);
        } else {
            selector = "*[itemprop=\"articleBody\"] p, *[itemprop=\"articleBody\"] ul, *[itemprop=\"articleBody\"] ol";
            a = doc.select(selector);
            if (a.size() == 0){
                selector = "article";
                a = doc.select(selector);
            }
        }

        article.body = a.toString();
        return article;
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
