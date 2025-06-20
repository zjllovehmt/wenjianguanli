import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
    Icon folderIcon = UIManager.getIcon("FileView.directoryIcon");
    Icon fileIcon = UIManager.getIcon("FileView.fileIcon");
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode) value;
        Object obj = node.getUserObject();
        if (obj instanceof FileNode) {
            java.io.File f = ((FileNode) obj).file;
            setIcon(f.isDirectory() ? folderIcon : fileIcon);
        }
        return this;
    }
}