package cp;

import cp.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DirectoryCrawler {
    private static ExecutorService executor = Executors.newFixedThreadPool(5);
    private static String filetype = "txt";
    private static String lookingfor = "semper";
    private static ArrayList results = new ArrayList<Result>();

    /*

     */
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        //test folder is around 800 Mbyte of lorem ipsum and other random .txt files
        File StartingDir = new File("C:/Users/mark/OneDrive/sdu/testfolder");

        List<Result> list = run(StartingDir.toPath());

        list.forEach(i -> System.out.println(i.toString()));
        System.out.println("Parsing complete");

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime + " ms");
    }

    /*
    Starts the Crawler, and counts how long time the problem takes to run
        @param path is the path the problem starts crawling from.
     */
    public static List<Result> run(Path path) {
//        ArrayList results = new ArrayList<Result>();
//        Path path = Paths.get("C:/Users/user/OneDrive/randomuni");

        executor.submit(
                () -> directoryCrawler(path));


        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List list = null;
        int j = 0;
        results.forEach(i -> list.add(i));


        return null;
    }

    /*
    @param dir, Recursive folder crawling
    if it finds a @param filetype sends the file to filehandler.
     */
    public static void directoryCrawler(Path dir) {


        try (
                DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)
        ) {
            for (Path path : dirStream) {
//                    System.out.println(path);
                if (Files.isDirectory(path)) {
                    directoryCrawler(path);
                } else {
                    if (path.toString().endsWith(filetype)) {

//                        System.out.println(path.toString());
                        filehandler(path);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Handles files, Spilts the file into lines and feeds the lines to the word checkers tread
     */
    private static void filehandler(Path path) {
//            System.out.println("running file halder");
//        System.out.println("checking file " + path);
        try {
            BufferedReader reader = Files.newBufferedReader(path);
//            System.out.println("opening file");
            String line;
            int linenumber = 0;
            while ((line = reader.readLine()) != null) {
                linenumber += 1;
//                    System.out.println(linenumber);
                final int finalline = linenumber;
                final String currentLine = line;
                executor.submit(
                        () -> wordchecker(currentLine, path, finalline)
                );

//                    executor.shutdown();
//                    executor.awaitTermination( 1, TimeUnit.MINUTES );

            }

        } catch (IOException e) {

        }


    }

    /*
    Checks the lines for the word @param lookingfor
     */
    private static void wordchecker(String line, Path path, int linenumber) {
//            System.out.println("running work checker");
//            System.out.println(line);
        {
            String[] words = line.split("\\s+");
            for (String word : words) {
                if (lookingfor.equals(word)) synchronized (results) {
//                    System.out.println("Found result at " + path + " On line " + linenumber);
                    results.add(new Result() {
                        @Override
                        public Path path() {
                            return path;
                        }

                        @Override
                        public int line() {
                            return linenumber;
                        }
                    });

//                        executor.shutdownNow();


                }
            }


        }
    }
}


