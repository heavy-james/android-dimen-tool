package command;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceReplacer2 {

    int originWidth;
    int originHeight;
    int targetWidth;
    int targetHeight;

    double scaleX = 1;
    double scaleY = 1;

    public SourceReplacer2(int originWidth, int originHeight, int targetWidth, int targetHeight) {
        this.originWidth = originWidth;
        this.originHeight = originHeight;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        scaleX = (double) targetWidth / (double) originWidth;
        scaleY = (double) targetHeight / (double) originHeight;
    }

    private void exitWithMessage(int code, String message) {
        System.out.println(message);
        System.exit(code);
    }

    /**
     * 修改文件内容
     *
     * @param file
     * @return
     */
    public boolean replace(File file) {
        System.out.println("replace : " + file.getName());

        File newFile = new File(file.getAbsoluteFile().getName() + "-bak");
        BufferedOutputStream fileOutputStream = null;
        RandomAccessFile raf = null;
        try {
            if (!newFile.createNewFile()) {
                if (!newFile.delete()) {
                    exitWithMessage(0, "can not delete old backup file for : " + file.getAbsoluteFile().getName() + ", abort.");
                } else if (newFile.createNewFile()) {
                    exitWithMessage(0, "can not create backup file for : " + file.getAbsoluteFile().getName() + ", abort.");
                }
            }
            raf = new RandomAccessFile(file, "rw");
            fileOutputStream = new BufferedOutputStream(new FileOutputStream(newFile));
            String line = null;
            while ((line = raf.readLine()) != null) {
                line = repalceDimenRef(line);
                line = replaceDpRef(line);
                line += "\n";
                fileOutputStream.write(line.getBytes());
            }

            fileOutputStream.flush();

            if (!file.delete() || !newFile.renameTo(file)) {
                exitWithMessage(0, "rename backup file failed. abort.");
            }
        } catch (IOException e) {
            exitWithMessage(0, "can not replace file : " + file.getAbsoluteFile().getName() + ", abort.");
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * @param line
     * @return true if line changed, false otherwise
     */
    private String repalceDimenRef(String line) {
        String patternString = "(\\s*\\S*@dimen/)(dp|sp)(\\d*\\S*\\s*)";

        // 创建 Pattern x对象
        Pattern pattern = Pattern.compile(patternString);

        // 现在创建 matcher 对象
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            if (line.contains("width") || line.contains("Width") || line.contains("Left") || line.contains("Right") || line.contains("Start") || line.contains("End")) {
                line = matcher.group(1) + "x" + matcher.group(3);
            } else {
                line = matcher.group(1) + "y" + matcher.group(3);
            }
            System.out.println("replace : " + line);
        }
        return line;
    }

    /**
     * @param line
     * @return true if line changed, false otherwise
     */
    private String replaceDpRef(String line) {
        String patternString = "([\\D]*)(\\d*)(dp|sp|px)([\\S|\\s]*)";

        // 创建 Pattern x对象
        Pattern pattern = Pattern.compile(patternString);

        // 现在创建 matcher 对象
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {

            System.out.println("group 0 : " + matcher.group(0));
            System.out.println("group 1 : " + matcher.group(1));
            System.out.println("group 2 : " + matcher.group(2));
            System.out.println("group 3 : " + matcher.group(3));
            System.out.println("group 4 : " + matcher.group(4));


            if (line.contains("width") || line.contains("Width") || line.contains("Left") || line.contains("Right") || line.contains("Start") || line.contains("End")) {
                line = matcher.group(1) + "@dimen/x" + matcher.group(2) + matcher.group(4);
            } else {
                line = matcher.group(1) + "@dimen/y" + matcher.group(2) + matcher.group(4);
            }
            System.out.println("replace : " + line);
        }
        return line;
    }


}
