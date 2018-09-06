import command.SourceCreator;
import command.SourceReplacer2;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainDp2Lay {

    static final Object mCountLock = new Object();
    static String path = ".";
    static List<String> positiveFilter;
    static List<String> negativeFilter;
    static String valuesPath = null;
    static int originWidth;
    static int originHeight;
    static int targetWidth;
    static int targetHeight;
    static int jobs = 1;

    static volatile int jobCount = 0;

    static volatile boolean shouldFinish = false;

    static ExecutorService executorService;

    static Map<String, Integer> predineValues;

    public static void main(String[] args) {


        String command = "java -jar TextReplace.jar -p ./test -f .xml -s .java -j 2 -v res/values.xml";

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
            } else if ("-j".equals(arg)) {
                jobs = Integer.valueOf(args[++i]);
            } else if ("-v".equals(arg)) {
                valuesPath = args[++i];
            } else {
                i++;
            }
        }

        System.out.println("path : " + new File(path).getAbsolutePath());
        System.out.println("values path : " + valuesPath);
        System.out.println("jobs : " + jobs);
        System.out.println("positiveFilter : " + positiveFilter);
        System.out.println("negativeFilter : " + negativeFilter);


        initPreDefineValues();

        if (predineValues != null) {
            for (String key : predineValues.keySet()) {
                System.out.print("predefine value name : " + key + "; value : " + predineValues.get(key) + "\n");
            }
        }

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
                synchronized (mCountLock) {
                    jobCount++;
                }
                SourceReplacer2 sourceReplacer = new SourceReplacer2(originWidth, originHeight, targetWidth, targetHeight);
                sourceReplacer.setPredefineValues(predineValues);
                sourceReplacer.replace(executeFile);
                synchronized (mCountLock) {
                    jobCount--;
                }
                if (shouldFinish && jobCount == 0) {
                    executorService.shutdown();
                }
            });
        }
        shouldFinish = true;
    }

    private static void initPreDefineValues() {

        if(valuesPath == null){
            return;
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        //通锟斤拷factory锟斤拷取SAXParser实锟斤拷
        try {
            SAXParser parser = factory.newSAXParser();
            //锟斤拷锟斤拷SAXParserHandler锟斤拷锟斤拷
            ValuesXmlParser handler = new ValuesXmlParser();
            parser.parse(valuesPath, handler);
            predineValues = handler.getValuesMap();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
