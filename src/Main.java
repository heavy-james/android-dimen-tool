import command.SourceCreator;
import command.SourceReplacer;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static String path = ".";
    static String positiveFilter;
    static String negativeFilter;
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

        String command = "java -jar TextReplace.jar -p /Users/heavy/workspace/topband/topband_running -f .xml -s .java -o 1280 600 -t 1920 1080 -j 5";

        //args = command.split(" ");

        if (args == null) {
            return;
        }

        for (int i = 0; i < args.length; ) {
            String arg = args[i];

            if ("-p".equals(arg)) {
                path = args[++i];
            } else if ("-f".equals(arg)) {
                positiveFilter = args[++i];
            } else if ("-s".equals(arg)) {
                negativeFilter = args[++i];
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

        // 按指定模式在字符串查找
        //String line = "android:layout_width=\"@dimen/x_100\"";
        //String pattern = "\\*(@dimen/x_)(\\d*)";

        System.out.print("path : " + new File(path).getAbsolutePath());
        System.out.print("\njobs : " + jobs);
        System.out.print("\npositiveFilter : " + positiveFilter);
        System.out.print("\nnegativeFilter : " + negativeFilter);
        System.out.print("\noriginWidth : " + originWidth);
        System.out.print("\noriginHeight : " + originHeight);
        System.out.print("\ntargetWidth : " + targetWidth);
        System.out.print("\ntargetHeight : " + targetHeight);
        System.out.print("\n");

        Executors.newFixedThreadPool(jobs);

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
                jobCount ++;
                SourceReplacer sourceReplacer = new SourceReplacer(originWidth, originHeight, targetWidth, targetHeight);
                sourceReplacer.replace(executeFile);
                jobCount --;
                if(shouldFinish && jobCount == 0){
                    executorService.shutdown();
                }

            });
        }
        shouldFinish = true;
    }

}
