package command;

import java.io.File;

public abstract class Command {

    public abstract void excute(File file, String line);

}
