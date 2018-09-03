import command.SourceCreator;
import command.SourceReplacer2;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainDp2Lay {

    static final Object mCountLock = new Object();
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


        String command = "java -jar TextReplace.jar -p ./test -f .xml -s .java -j 2";

        //args = command.split(" ");

        if (args == null || args.length == 0) {
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
            } if ("-j".equals(arg)) {
                jobs = Integer.valueOf(args[++i]);
            } else {
                i++;
            }
        }

        System.out.println("path : " + new File(path).getAbsolutePath());
        System.out.println("jobs : " + jobs);
        System.out.println("positiveFilter : " + positiveFilter);
        System.out.println("negativeFilter : " + negativeFilter);


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
                SourceReplacer2 sourceReplacer = new SourceReplacer2(originWidth, originHeight, targetWidth, targetHeight);
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
