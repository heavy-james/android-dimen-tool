
package command;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceReplacer2 {

    int originWidth;
    int originHeight;
    int targetWidth;
    int targetHeight;

    Map<String, Integer> predefineValues;

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

    public void setPredefineValues(Map<String, Integer> predefineValues) {
        this.predefineValues = predefineValues;
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

        File newFile = new File(file.getAbsolutePath() + "-bak");
        OutputStreamWriter fileOutputStream = null;
        BufferedReader bufferedReader = null;
        try {
            newFile.delete();
            newFile.createNewFile();
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            fileOutputStream = new OutputStreamWriter(new FileOutputStream(newFile), "utf-8");
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {

                if (line.contains("android:text=") || line.contains("android:contentDescription=") || line.contains("<!--")) {
                    //do nothing, just need else
                    System.out.println("ignore line : " + line);
                } else {

                    String temp = replaceDimenRef(line);

                    if (temp == null) {
                        temp = replaceDpRef(line);
                    }

                    if (temp == null) {
                        temp = replacePredefineValues(line);
                    }

                    if (temp != null) {
                        line = temp;
                    }
                }
                line += "\n";
                fileOutputStream.append(line);
            }

            fileOutputStream.flush();

            if (!file.delete() || !newFile.renameTo(file)) {
                exitWithMessage(0, "rename backup file failed. abort.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
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
     * @return line if line changed, null otherwise
     */
    private String replaceDimenRef(String line) {
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
            return line;
        }
        return null;
    }

    /**
     * @param line
     * @return line if line changed, null otherwise
     */
    private String replaceDpRef(String line) {
        String patternString = "([\\D]*)(\\S+)(dp|sp|px)([\\S|\\s]*)";

        // 创建 Pattern x对象
        Pattern pattern = Pattern.compile(patternString);

        // 现在创建 matcher 对象
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            try{
                double dimen = Double.valueOf(matcher.group(2));
                if (dimen > 1) {
                    if (line.contains("width") || line.contains("Width") || line.contains("Left") || line.contains("Right") || line.contains("Start") || line.contains("End")) {
                        line = matcher.group(1) + "@dimen/x" + matcher.group(2) + matcher.group(4);
                    } else {
                        line = matcher.group(1) + "@dimen/y" + matcher.group(2) + matcher.group(4);
                    }
                }
                System.out.println("replace : " + line);
                return line;
            }catch (NumberFormatException e){
                System.out.println("can not replace dimen for line : " + line);
            }
        }
        return null;
    }

    /**
     * @param line
     * @return line if line changed, null otherwise
     */
    private String replacePredefineValues(String line) {

        if (predefineValues != null) {

            String patternString = "(\\s*\\S*@dimen/)(\\S+)(\"\\s*)";

            // 创建 Pattern x对象
            Pattern pattern = Pattern.compile(patternString);

            Matcher matcher = pattern.matcher(line);

            if (matcher.find() && predefineValues.containsKey(matcher.group(2))) {
                if (line.contains("width") || line.contains("Width") || line.contains("Left") || line.contains("Right") || line.contains("Start") || line.contains("End")) {
                    line = line.replace(matcher.group(2), "x" + String.valueOf(predefineValues.get(matcher.group(2))));
                } else {
                    line = line.replace(matcher.group(2), "y" + String.valueOf(predefineValues.get(matcher.group(2))));

                }
                return line;
            }

        }
        return null;
    }


}
