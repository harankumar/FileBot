import java.io.*;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

/**
 * Main Wrapper Class for FileBot
 * Serves as a command line interface
 * */
public class FileBot {

    /**
     * Runs FileBot.
     * Possible Commands:
     *   file: starts filing
     *   clean: removes blank .extemp files from specified directory
     * */
    public static void main(String[] args) throws IOException, URISyntaxException {
        if (args.length == 0) {
            System.err.println("You must provide an option and arguments in order to file.");
            System.exit(0);
        }
        switch(args[0]){
            case "file":
                file(args);
                break;
            case "clean":
                clean(args);
                break;
            default:
                throw new IllegalArgumentException("Unknown command \"" + args[0] + "\"");
        }
        System.out.println("Finished running successfully!");
    }

    /**
     * Starts Filing.
     * Options:
     *  -target Path
     *      FileBot will place generated files in this directory.
     *      Path must provide an absolute path.
     *      Default is C:/Extemp/files
     *  -feeds PathToFile
     *      If specified, FileBot will use this file for the list of feeds,
     *      otherwise it will use the default feeds.in file.
     *  -domHelper PathToFile
     *      If specified, FileBot will use this file for the list of dom helpers,
     *      otherwise, it will use the default domHelper.txt.
     *  -maxArticles MaxArticles
     *      Specifies maximum number of articles to file.
     *      Default is 100,000.
     *  -maxArticlesPerFeed MaxArticlesPerFeed
     *      Specifies maximum number of articles to file in each feed.
     *      Default is 100.
     *  -maxFeeds MaxFeeds
     *      Specifies maximum number of feeds to parse.
     *      Default is 1000.
     *  -scramble true/false
     *      Specifies whether to scramble the order in which feeds are processed.
     *  -loop true/false
     *      Specifies whether to continuously keep filing, or to stop once the operation is completed
     * */
    public static void file(String[] args) throws IOException, URISyntaxException {
        assert (args[0].equals("file"));

        // Default values
        String target = "C:/Extemp/files";
        String feeds = "lib/feeds.in";
        String domHelper = "lib/domHelper.txt";
        int maxArticles = 100000;
        int maxArticlesPerFeed = 100;
        int maxFeeds = 1000;
        boolean alphabetize = false;
        boolean scramble = false;
        boolean loop = false;

        // Custom Options
        for (int i = 1; i < args.length; i += 2) {
            String option = args[i];
            String argument = args[i + 1];

            switch (option) {
                case "-target":
                    target = cleanPath(argument);
                    break;
                case "-feeds":
                    feeds = cleanPath(argument);
                    break;
                case "-domHelper":
                    domHelper = argument;
                    break;
                case "-maxArticles":
                    maxArticles = Integer.parseInt(argument);
                    break;
                case "-maxArticlesPerFeed":
                    maxArticlesPerFeed = Integer.parseInt(argument);
                    break;
                case "-maxFeeds":
                    maxFeeds = Integer.parseInt(argument);
                    break;
                case "-scramble":
                    scramble = argument.toLowerCase().charAt(0) == 't';
                    break;
                case "-alphabetize":
                    alphabetize = argument.toLowerCase().charAt(0) == 't';
                case "-loop":
                    loop = argument.toLowerCase().charAt(0) == 't';
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Option \"" + option + "\"");
            }
        }

        do {
            runFiler(target, feeds, domHelper, maxArticles, maxArticlesPerFeed, maxFeeds, scramble, alphabetize);
        } while(loop);
    }

    /**
     * This method actually runs the filer.
     * See FileBot.file for more information.
     * */
    public static void runFiler(String target, String feeds, String domHelper, int maxArticles, int maxArticlesPerFeed, int maxFeeds, boolean scramble, boolean alphabetize) throws IOException, URISyntaxException {
        FileBotRunner fb = new FileBotRunner(target, feeds, domHelper, maxArticles, maxArticlesPerFeed, maxFeeds, scramble, alphabetize, new FileBotRunner.CompletionListener() {
            @Override
            public void onComplete() {
            }
        });
        fb.run();
    }

    /**
     * Cleans a Windows filepath into a suitable Java path
     * */
    private static String cleanPath(String path){
        path = path.replace("\\", "/");
        return path;
    }

    public static void clean(String[] args) throws IOException {
        assert(args[0].equals("clean"));
        String dirPath = cleanPath(args[1]);

        File dir = new File(dirPath);
        assert(dir.isDirectory());

        File[] list = dir.listFiles();

        for (File file: list){
            if (file == null)
                continue;
            Article article = new Article(file, true);
        }
    }
}