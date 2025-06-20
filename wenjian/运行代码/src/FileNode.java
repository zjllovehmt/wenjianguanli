import java.io.File;

public class FileNode {
    final File file;
    public FileNode(File file) { this.file = file; }
    public String toString() { return file.getName().isEmpty() ? file.getPath() : file.getName(); }
}