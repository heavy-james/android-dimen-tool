package command;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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

                long originalLength = line.length();

                final long point = raf.getFilePointer();

                boolean lineChanged = false;

                String patternString = "(\\s*\\S*@dimen/)(dp|sp)(\\d*\\S*\\s*)";

                // 创建 Pattern x对象
                Pattern pattern = Pattern.compile(patternString);

                // 现在创建 matcher 对象
                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {
                    if(line.contains("width") || line.contains("Width") || line.contains("Left") || line.contains("Right") || line.contains("tart") || line.contains("End")){
                        line = matcher.group(1) + "x" + matcher.group(3);
                    }else {
                        line = matcher.group(1) + "y" + matcher.group(3);
                    }
                    lineChanged = true;
                    System.out.println("replace : " + line);
                }

                if (lineChanged) {
                    int lineLength = line.length();
                    StringBuilder stringBuilder = new StringBuilder(line);
                    for(int i = 0; i< originalLength - lineLength; i++){
                        stringBuilder.append(" ");
                    }
                    raf.seek(lastPoint);
                    raf.writeBytes(stringBuilder.toString());
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
