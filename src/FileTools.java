import java.util.*;
import java.io.*;

public class FileTools {
//    TODO: use classpath
    public static void scramble(String feeds) throws IOException {
        scramble(new File(feeds));
    }

    public static void scramble(File file) throws IOException {
        Scanner reader = new Scanner(file);
        List<String> inp = new ArrayList<String>();
        while (reader.hasNext())
            inp.add(reader.nextLine());

        Collections.shuffle(inp);

        PrintWriter printWriter = new PrintWriter(file);
        for(String line: inp)
            printWriter.println(line);
        printWriter.close();
    }

    public static void alphabetize(String feeds) throws IOException {
        alphabetize(new File(feeds));
    }

    public static void alphabetize(File file) throws IOException {
        Scanner reader = new Scanner(file);
        List<String> inp = new ArrayList<String>();
        while (reader.hasNext())
            inp.add(reader.nextLine());

        Collections.sort(inp);

        PrintWriter printWriter = new PrintWriter(file);
        for(String line: inp)
            printWriter.println(line);
        printWriter.close();
    }
}