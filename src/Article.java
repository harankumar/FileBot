import com.sun.syndication.feed.synd.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.safety.*;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 *
 */
public class Article {
//    public static Whitelist whitelist = Whitelist.basic().addTags("article", "p").removeTags("a");
    public static Whitelist whitelist = Whitelist.none();
    public String uri;
    String author;
    String title;
    String date;
    int views;
    public String body;
    public boolean valid;

    public Article(SyndEntry entry) {
        try {
            uri = entry.getUri();
            if (uri.contains("tag:news.google.com")){
                uri = uri.substring(uri.indexOf("http"));
            }
            author = entry.getAuthor();
            title = entry.getTitle();
            date = entry.getPublishedDate().toString();

            Document doc = Jsoup.connect(uri).get();
            body = doc.html();

            valid = validate(title);
        } catch (MalformedURLException e){
            System.err.println(e.getMessage());
            System.err.println(uri);
            valid = false;
        } catch (Exception e){
            System.err.println(e.getMessage());
            valid = false;
        }
    }

    public Article(File file, boolean autodelete) throws IOException {
        Scanner reader = new Scanner(file);
        boolean flag = false;
        try {
            title = reader.nextLine();
            System.out.println(title);
            assert (title.indexOf("TITLE: ") == 0);
            title = title.substring(7);

            author = reader.nextLine();
            System.out.println(author);
            assert (author.indexOf("AUTHOR: ") == 0);
            author = author.length() > 8 ? author.substring(8) : "";

            date = reader.nextLine();
            assert (date.indexOf("DATE: ") == 0);
            System.out.println(date);
            date = date.substring(6);

            uri = reader.nextLine();
            assert (uri.indexOf("URI: ") == 0);
            System.out.println(uri);
            System.exit(0);
            uri = uri.substring(5);

            assert (reader.nextLine().equals("BEGIN BODY"));
            body = "";
            while (reader.hasNext()) {
                body += reader.nextLine();
            }
            body = body.replaceAll("\\&nbsp", " ");
            body = body.replaceAll("\\&colon", ":");
            body = body.replaceAll("\\&comma", ",");
            body = body.replaceAll("\\&quot", "\"");
            body = body.replaceAll("\\&dollar", "$");
            body = body.replaceAll("  ", "\n");
            while(body.indexOf(" \n") != 0){
                body = body.replaceAll(" \n", "\n");
            }
            while(body.indexOf("\n\n") != 0){
                body = body.replaceAll("\n\n", "\n");
            }
        } catch (NoSuchElementException e){
            if (autodelete){
                flag = true;
            } else {
                System.err.println("Error: " + e.getMessage());
            }
        }
        if (body == null || body.length() <= 200 || flag || !validate(this.uri) || !validate(this.title)){
            reader = null;
            System.gc();
            delete(file);
        }
    }

    public static void repair(File file) throws IOException {
        Article article = new Article(file, false);
        article.clean();
        article.writeToFile(file.getAbsolutePath());
    }

    static void delete(File file){
        if (!file.exists())
            System.err.println("Can't delete nonexistent file!");
        else if (!file.canWrite())
            System.err.println("File is write protected!");
        else if (!file.delete()) {
            System.err.println("Unable to delete file! Will attempt to delete on exit.");
            file.deleteOnExit();
        } else
            System.out.println("File successfully deleted!");
    }

    boolean empty(){
        return body.length() == 0;
    }

    boolean validate(String string){
        String[] banned = {"Episode", "Photo", "Watch", "Video", "Audio", "Gallery", "Slideshow"};
        for (String bannedWord: banned)
            if (string.toLowerCase().contains(bannedWord.toLowerCase()) || uri.toLowerCase().contains(bannedWord.toLowerCase()))
                return false;
        return true;
    }

    void clean(){
        body = Jsoup.clean(body, uri, whitelist);
    }

    String cleanTitle(String title){
        title = title.replace(": ", "-");
        title = title.replace(":", "-");
        title = title.replace(" ", "_");
        String[] banned = {"<", ">", "\"", "/", "\\", "|", "?", "*", ";", ","};
        for (String c: banned)
            title = title.replace(c, "");
        return title;
    }

    public String getFilePath(String targetDirectory){
        return targetDirectory + "/" + cleanTitle(this.title) + ".extemp";
    }

    public boolean isFiled(String filePath){
        File x = new File(filePath);
        return x.length() != 0;
    }

    public void file(String targetDirectory) throws IOException {
        clean();

        if (body.length() <= 300) {
            System.err.println("Short File! Won't Save! " + title);
            return;
        }

        String filePath = getFilePath(targetDirectory);
        if (isFiled(filePath)){
            System.err.println("Already Filed! " + title);
            return;
        }

        System.out.println("Filing: " + title);
        System.out.println("\t" + uri);

        writeToFile(filePath);

    }

    public void writeToFile(String filePath) throws IOException {
        try {
            PrintWriter printWriter = new PrintWriter(filePath, "UTF-16");
            printWriter.println("TITLE: " + title);
            printWriter.println("AUTHOR: " + author);
            printWriter.println("DATE: " + date);
            printWriter.println("URI: " + uri);
            printWriter.println("ARTICLE BODY:\n" + body);
            printWriter.close();
        } catch (FileNotFoundException e){
            System.err.println("Error Occurred while saving. File Skipped.");
        }
    }
}