import java.util.LinkedList;
import java.util.List;

public class Main {

    static List<int[]> targetDimens = new LinkedList<>();


    static {

        targetDimens.add(new int[]{320, 480});
        targetDimens.add(new int[]{480, 800});
        targetDimens.add(new int[]{480, 854});
        targetDimens.add(new int[]{540, 960});
        targetDimens.add(new int[]{600, 1024});
        targetDimens.add(new int[]{720, 1184});
        targetDimens.add(new int[]{720, 1280});
        targetDimens.add(new int[]{768, 1024});
        targetDimens.add(new int[]{800, 1280});
        targetDimens.add(new int[]{1080, 1812});
        targetDimens.add(new int[]{1080, 1920});
        targetDimens.add(new int[]{1440, 2560});
        targetDimens.add(new int[]{1280, 720});
        targetDimens.add(new int[]{1366, 696});
        targetDimens.add(new int[]{1366, 786});
        targetDimens.add(new int[]{1920, 1080});
    }


    public static void main(String[] args) {

        if (args == null) {
            return;
        }

        int baseW = 320;
        int baseH = 400;

        for (int i = 0; i < args.length; ) {
            String arg = args[i];
            if ("-b".equals(arg)) {
                baseW = Integer.valueOf(args[++i]);
                baseH = Integer.valueOf(args[++i]);
            } else if ("-t".equals(arg)) {
                int targetWidth = Integer.valueOf(args[++i]);
                int targetHeight = Integer.valueOf(args[++i]);
                int[] resource = new int[]{targetWidth, targetHeight};
                targetDimens.add(resource);
            } else{
                i++;
            }
        }

        new GenerateValueFiles(baseW, baseH, targetDimens).generate();
    }
}
