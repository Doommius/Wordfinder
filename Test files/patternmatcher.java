import cp.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class patternmatcher {

    private static ArrayList results = new ArrayList<Result>();

    /*

 */
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        //test folder is around 800 Mbyte of lorem ipsum and other random .txt files
        File StartingDir = new File("C:/Users/mark-/Documents/test/1");
        String lookingforword = "ipsum";
        System.out.println("Pattern single thread");
        List<Result> list = run(lookingforword, StartingDir.toPath());


//        list.forEach(i -> System.out.println("Word "+lookingfor+" found in file " + i.path()+ " at line "+i.line()));

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime + " ms");
    }

    /*
    Starts the Crawler, and counts how long time the problem takes to run
        @param path is the path the problem starts crawling from.
     */
    public static List<Result> run(String lookingforword, Path path) {
//        ArrayList results = new ArrayList<Result>();
//        Path path = Paths.get("C:/Users/user/OneDrive/randomuni");
        Pattern lookingforpattern = Pattern.compile("\\s" + lookingforword + "\\s");
        directoryCrawler(path, lookingforpattern);


        //wating for queue to empty

//When queue is empty there should be a few tasks still in the pool that came from the queue when shutdown is called. they will be allowed to finish.
        System.out.println("Found " + results.size() + " Results");
        List list = null;
        int j = 0;
//        results.forEach(i -> list.add(i));


        return list;
    }

    /*
    @param dir, Recursive folder crawling
    if it finds a @param filetype sends the file to filehandler.
     */
    public static void directoryCrawler(Path dir, Pattern lookingforpattern) {


        try (
                DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)
        ) {
            for (Path path : dirStream) {
//                System.out.println(path);
//                    System.out.println(path);
                if (Files.isDirectory(path)) {

                    directoryCrawler(path, lookingforpattern);

                } else if (path.toString().endsWith("txt")) {
//                        System.out.println(path.toString());
                    filehandler(path, lookingforpattern);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Handles files, Spilts the file into lines and feeds the lines to the word checkers tread
    @param path is a .txt file from a scanned directory
     */
    private static void filehandler(Path path, Pattern lookingforpattern) {
//            System.out.println("running file halder");
//        System.out.println("checking file " + path);

        try {
            BufferedReader reader = Files.newBufferedReader(path);
            int linestothread = 1000;
            int linenumber = 0;
            int n;
//            String[] lines = new String[linestothread];
            String line;


            while ((line = reader.readLine()) != null) {
                String[] lines = new String[linestothread];
                if (line.trim().length() > 0) {
                    lines[0] = line;
                }
                n = 1;
                while (n < linestothread && ((line = reader.readLine()) != null)) {
                    if (line.trim().length() > 1) {
                        //System.out.println("adding line to file "+path+" *"+line+"*");
                        lines[n] = line;
                        n++;
                    }
                }
                linenumber += 1;
//                    System.out.println(linenumber);
                final int finalline = linenumber;
                final String[] currentLines = lines;
                wordchecker(currentLines, path, finalline, lookingforpattern);
            }
        } catch (IOException e) {
        }
    }

    /*
    Checks the lines for the word @param lookingfor
     */
    private static void wordchecker(String[] lines, Path path, int linenumbers, Pattern lookingforpattern) {
        Matcher match = lookingforpattern.matcher("");
        for (String line : lines) {
            if (line != null) {
                // System.out.println("1 " + line);
                match.reset(line);

                while (match.find()) {
//                    System.out.println(match.group(0));
                    synchronized (results) {
                           //System.out.println(results.size());
//                            System.out.println("Result at " + path + " on line " + linenumbers);
                        final int linenuber = linenumbers;
                        results.add(new Result() {
                            @Override
                            public Path path() {
                                return path;
                            }

                            @Override
                            public int line() {
                                return linenuber;
                            }
                        });
                    }


            }

        }
            linenumbers++;
    }
}
}


