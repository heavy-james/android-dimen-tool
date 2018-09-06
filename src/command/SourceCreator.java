package command;

import java.io.File;
import java.util.List;

public class SourceCreator {

    String path;
    static List<String> positiveFilter;
    static List<String> negativeFilter;

    FileNode fileNode;

    File nextFile;

    public SourceCreator(String path, List<String> positiveFilter, List<String> negativeFilter) {
        this.path = path;
        this.positiveFilter = positiveFilter;
        this.negativeFilter = negativeFilter;
        fileNode = new FileNode(path);
    }

    public synchronized boolean hasNextFile() {
        nextFile = fileNode.getFile();
        if(nextFile == null){
            return false;
        }
        if(isAccepted(nextFile)){
            return true;
        }else {
            return hasNextFile();
        }
    }

    private boolean isAccepted(File file){

        if(negativeFilter != null){
            for(String filter : negativeFilter){
                if(file.getPath().contains(filter)){
                    System.out.println("negative filter ignore : " + file.getPath());
                    return false;
                }
            }
        }

        if(positiveFilter != null){
            for(String filter : positiveFilter){
                if(file.getPath().contains(filter)){
                    return true;
                }
            }
        }
        System.out.println("positive filter ignore : " + file.getPath());
        return false;
    }

    public File nextFile() {
        File file = nextFile;
        nextFile = null;
        return file;
    }
}
