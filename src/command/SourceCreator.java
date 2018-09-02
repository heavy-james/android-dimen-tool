package command;

import java.io.File;

public class SourceCreator {

    String path;
    String positiveFilter;
    String negativeFilter;

    FileNode fileNode;

    File nextFile;

    public SourceCreator(String path, String positiveFilter, String negativeFilter) {
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
        if(negativeFilter != null && file.getName().contains(negativeFilter)){
            System.out.println("negative filter ignore : " + file.getName());
            return false;
        }
        if(positiveFilter != null && !file.getName().contains(positiveFilter)){
            System.out.println("positive filter ignore : " + file.getName());
            return false;
        }
        return true;
    }

    public File nextFile() {
        File file = nextFile;
        nextFile = null;
        return file;
    }
}
