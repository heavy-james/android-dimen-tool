import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by zhy on 15/5/3.
 */
public class GenerateValueFiles {

    int baseW;
    int baseH;
    List<int[]> targetDimens;

    private String dirStr = "./res";

    private final static String WTemplate = "<dimen name=\"x{0}\">{1}px</dimen>\n";
    private final static String HTemplate = "<dimen name=\"y{0}\">{1}px</dimen>\n";

    /**
     * {0}-HEIGHT
     */
    private final static String VALUE_TEMPLATE = "values-{0}x{1}";

    public GenerateValueFiles(int baseW, int baseH, List<int[]> addDimens) {

        this.baseW = baseW;
        this.baseH = baseH;
        this.targetDimens = addDimens;

        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdir();

        }
        System.out.println(dir.getAbsoluteFile());
    }


    public void generate() {
        for (int[] dimen : targetDimens) {
            generateXmlFile(dimen[0], dimen[1]);
        }
    }

    private void generateXmlFile(int w, int h) {

        StringBuffer sbForWidth = new StringBuffer();
        sbForWidth.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForWidth.append("<resources>");
        float cellWidth = w * 1.0f / baseW;

        System.out.println("width : " + w + "," + baseW + "," + cellWidth);
        for (int i = 1; i < baseW; i++) {
            sbForWidth.append(WTemplate.replace("{0}", i + "").replace("{1}",
                    change(cellWidth * i) + ""));
        }
        sbForWidth.append(WTemplate.replace("{0}", baseW + "").replace("{1}",
                w + ""));
        sbForWidth.append("</resources>");

        StringBuffer sbForHeight = new StringBuffer();
        sbForHeight.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForHeight.append("<resources>");
        float cellh = h * 1.0f / baseH;
        System.out.println("height : " + h + "," + baseH + "," + cellh);
        for (int i = 1; i < baseH; i++) {
            sbForHeight.append(HTemplate.replace("{0}", i + "").replace("{1}",
                    change(cellh * i) + ""));
        }
        sbForHeight.append(HTemplate.replace("{0}", baseH + "").replace("{1}",
                h + ""));
        sbForHeight.append("</resources>");

        File fileDir = new File(dirStr + File.separator
                + VALUE_TEMPLATE.replace("{0}", w + "")//
                .replace("{1}", h + ""));
        fileDir.mkdir();

        File layxFile = new File(fileDir.getAbsolutePath(), "lay_x.xml");
        File layyFile = new File(fileDir.getAbsolutePath(), "lay_y.xml");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
            pw.print(sbForWidth.toString());
            pw.close();
            pw = new PrintWriter(new FileOutputStream(layyFile));
            pw.print(sbForHeight.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static float change(float a) {
        int temp = (int) (a * 100);
        return temp / 100f;
    }


}