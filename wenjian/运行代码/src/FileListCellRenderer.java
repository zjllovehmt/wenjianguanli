import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileListCellRenderer extends DefaultListCellRenderer {
    Icon folderIcon = UIManager.getIcon("FileView.directoryIcon");
    Icon fileIcon = UIManager.getIcon("FileView.fileIcon");
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (value instanceof File) {
            File file = (File) value;
            label.setIcon(file.isDirectory() ? folderIcon : fileIcon);
            label.setText(file.getName());
        }
        return label;
    }
}