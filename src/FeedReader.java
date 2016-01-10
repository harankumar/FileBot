import java.util.*;
import java.io.*;

/**
 * FeedReader objects take in a file and produce
 * Feed objects for each url in it. The feeds file
 * may contain comments, which are any lines
 * beginning with a "#" character.
 * The FeedReader class is essentially a wrapper to
 * the Scanner class, but may eventually be switched
 * to another form of File IO, perhaps BufferedReader.
 */
public class FeedReader {
    Scanner reader;
    public FeedReader(String pathToFeeds) throws IOException{
//        if (inJar) {
//        byte[] buffer = new byte[input.available()];
//        for (int i = 0; i != -1; i = input.read(buffer)) {
//            System.out.write(buffer, 0, i);
//        }
//        reader = new Scanner(new File(pathToFeeds));
        try {
            InputStream input = this.getClass().getClassLoader().getResourceAsStream(pathToFeeds);
            reader = new Scanner(input);
        } catch (NullPointerException e) {
//        } else
            reader = new Scanner(new File(pathToFeeds));
        }
    }

    public boolean hasNext(){
        return reader.hasNext();
    }

    public Feed nextFeed(){
        String line = reader.nextLine();
        if (line.charAt(0) == '#')
            return null;
        return new Feed(line);
    }
}
