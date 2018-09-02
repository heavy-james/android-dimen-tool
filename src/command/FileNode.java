package command;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileNode {

    int childIndex;

    String path;

    File self;

    FileNode mCurrentNode;

    List<FileNode> subNodes;

    public FileNode(String path) {

        this.path = path;
        self = new File(path);
    }

    public boolean isDirectory() {
        return self.exists() && self.isDirectory();
    }

    public boolean isFile() {
        return self.exists() && self.isFile();
    }

    public boolean hasChildren() {
        if (isFile()) {
            return false;
        }
        if (subNodes == null) {
            String[] subFiles = self.list();
            if (subFiles != null) {
                subNodes = new ArrayList<>();
                for (String file : subFiles) {
                    subNodes.add(new FileNode(self.getPath() + File.separator + file));
                }
            }

        }
        if (subNodes == null) {
            return false;
        }
        return childIndex < subNodes.size();
    }

    private FileNode nextChild() {
        if (childIndex == subNodes.size()) {
            return null;
        }
        return subNodes.get(childIndex++);

    }

    public File getFile(){
        if(isFile()){
            return self;
        }
        if(mCurrentNode == null && hasChildren()){
            mCurrentNode = nextChild();
        }
        if(mCurrentNode != null){
            if(mCurrentNode.isFile()){
                File file = mCurrentNode.getFile();
                mCurrentNode = nextChild();
                return file;
            }else if(mCurrentNode.isDirectory()){
                File file = mCurrentNode.getFile();
                if(file == null){
                    mCurrentNode = nextChild();
                    return getFile();
                }else {
                    return file;
                }
            }
        }
        return null;
    }

}
