import command.SourceCreator;
import command.SourceReplacer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static final Object mCountLock = new Object();
    static String path = ".";
    static List<String> positiveFilter;
    static List<String> negativeFilter;
    static int originWidth;
    static int originHeight;
    static int targetWidth;
    static int targetHeight;
    static int jobs = 1;

    static volatile int jobCount = 0;

    static volatile boolean shouldFinish = false;

    static ExecutorService executorService;

    public static void main(String[] args) {

        System.out.print("start!\n");

        String command = "java -jar TextReplace.jar -p ./test -f .xml -s .java -o 1280 600 -t 1920 1080";

        //args = command.split(" ");

        if (args == null || args.length == 0) {
            return;
        }

        positiveFilter = new ArrayList<>();
        negativeFilter = new ArrayList<>();

        for (int i = 0; i < args.length; ) {
            String arg = args[i];

            if ("-p".equals(arg)) {
                path = args[++i];
            } else if ("-f".equals(arg)) {
                positiveFilter.add(args[++i]);
            } else if ("-s".equals(arg)) {
                negativeFilter.add(args[++i]);
            } else if ("-o".equals(arg)) {
                originWidth = Integer.valueOf(args[++i]);
                originHeight = Integer.valueOf(args[++i]);
            } else if ("-t".equals(arg)) {
                targetWidth = Integer.valueOf(args[++i]);
                targetHeight = Integer.valueOf(args[++i]);
            } else if ("-j".equals(arg)) {
                jobs = Integer.valueOf(args[++i]);
            } else {
                i++;
            }
        }

        System.out.println("path : " + new File(path).getAbsolutePath());
        System.out.println("jobs : " + jobs);
        System.out.println("positiveFilter : " + positiveFilter);
        System.out.println("negativeFilter : " + negativeFilter);
        System.out.println("originWidth : " + originWidth);
        System.out.println("originHeight : " + originHeight);
        System.out.println("targetWidth : " + targetWidth);
        System.out.println("targetHeight : " + targetHeight);

        File file = new File(path);

        if (!file.exists()) {
            System.out.print("file not exist. finished.");
            return;
        }

        executorService = Executors.newFixedThreadPool(jobs);

        SourceCreator sourceCreator = new SourceCreator(path, positiveFilter, negativeFilter);

        while (sourceCreator.hasNextFile()) {
            File executeFile = sourceCreator.nextFile();
            executorService.execute(() -> {
                synchronized (mCountLock){
                    jobCount ++;
                }
                SourceReplacer sourceReplacer = new SourceReplacer(originWidth, originHeight, targetWidth, targetHeight);
                sourceReplacer.replace(executeFile);
                synchronized (mCountLock){
                    jobCount --;
                }
                if(shouldFinish && jobCount == 0){
                    executorService.shutdown();
                }
            });
        }
        shouldFinish = true;
    }

}
