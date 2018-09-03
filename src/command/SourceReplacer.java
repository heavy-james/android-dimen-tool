package command;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceReplacer {

    int originWidth;
    int originHeight;
    int targetWidth;
    int targetHeight;

    double scaleX = 1;
    double scaleY = 1;

    public SourceReplacer(int originWidth, int originHeight, int targetWidth, int targetHeight) {
        this.originWidth = originWidth;
        this.originHeight = originHeight;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        scaleX = (double) targetWidth / (double) originWidth;
        scaleY = (double) targetHeight / (double) originHeight;
    }

    /**
     * 修改文件内容
     *
     * @param file
     * @return
     */
    public boolean replace(File file) {
        System.out.println("replace : " + file.getName());
        RandomAccessFile raf = null;
        long lastPoint = 0;
        try {
            raf = new RandomAccessFile(file, "rw");
            String line = null;
            while ((line = raf.readLine()) != null) {

                int originalLength = line.length();

                final long point = raf.getFilePointer();

                boolean lineChanged = false;

                String patternX = "((\\s*\\S*@dimen/x)(\\d*)(\\S*\\s*))";

                // 创建 Pattern x对象
                Pattern x = Pattern.compile(patternX);

                // 现在创建 matcher 对象
                Matcher xMatcher = x.matcher(line);

                if (xMatcher.find()) {
                    int dimen = Integer.valueOf(xMatcher.group(3));
                    int translatedDimen = (int) (dimen * scaleX + 0.5);
                    line = xMatcher.group(2) + String.valueOf(translatedDimen) + xMatcher.group(4);
                    System.out.println("replace x : " + line);
                    lineChanged = true;
                }

                // 创建 Pattern y对象
                String patternY = "((\\s*\\S*@dimen/y)(\\d*)(\\S*\\s*))";
                // 创建 Pattern 对象
                Pattern y = Pattern.compile(patternY);
                // 现在创建 matcher 对象
                Matcher yMatcher = y.matcher(line);
                if (yMatcher.find()) {
                    int dimen = Integer.valueOf(yMatcher.group(3));
                    int translatedDimen = (int) (dimen * scaleY + 0.5);
                    line = yMatcher.group(2) + String.valueOf(translatedDimen) + yMatcher.group(4);
                    System.out.println("replace y : " + line);
                    lineChanged = true;
                }


                if (lineChanged) {

                    int lineLength = line.length();
                    StringBuilder stringBuilder = new StringBuilder(line);
                    for(int i = 0; i< originalLength - lineLength; i++){
                        stringBuilder.append(" ");
                    }

                    raf.seek(lastPoint);
                    raf.writeBytes(line);

                    //String targetLine = "[" + line + "]";
                    //System.out.println("replace : " + originLine);
                    //System.out.println("replace : " + targetLine);
                }
                lastPoint = point;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


}
