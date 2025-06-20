import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileListPanel extends JPanel {
    JList<File> fileList;
    DefaultListModel<File> listModel;
    JLabel pathLabel;
    File currentDir;
    FileOperationHandler handler;

    public FileListPanel(FileOperationHandler handler) {
        this.handler = handler;
        setLayout(new BorderLayout());
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setCellRenderer(new FileListCellRenderer());
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(fileList);

        pathLabel = new JLabel(" ");
        pathLabel.setBorder(BorderFactory.createEmptyBorder(2,8,2,8));
        add(pathLabel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    File f = fileList.getSelectedValue();
                    if (f != null && f.isDirectory()) {
                        handler.onDirSelected(f);
                    }
                }
            }
        });

        fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                File f = fileList.getSelectedValue();
                handler.onFileSelected(f);
            }
        });

        fileList.setComponentPopupMenu(new FileListPopupMenu(handler, fileList));
    }

    public void showFiles(File dir) {
        this.currentDir = dir;
        listModel.clear();
        if (dir == null) return;
        pathLabel.setText("当前目录: " + dir.getAbsolutePath());
        File[] files = dir.listFiles();
        if (files == null) return;
        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
        for (File f : files) listModel.addElement(f);
    }

    public File getSelectedFile() {
        return fileList.getSelectedValue();
    }
}