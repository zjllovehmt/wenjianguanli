import javax.swing.*;

public class FileListPopupMenu extends JPopupMenu {
    FileOperationHandler handler;
    JList<java.io.File> fileList;

    public FileListPopupMenu(FileOperationHandler handler, JList<java.io.File> fileList) {
        this.handler = handler;
        this.fileList = fileList;

        JMenuItem preview = new JMenuItem("预览文本");
        JMenuItem rename = new JMenuItem("重命名");
        JMenuItem copy = new JMenuItem("复制");
        JMenuItem paste = new JMenuItem("粘贴");
        JMenuItem count = new JMenuItem("统计文件数");

        preview.addActionListener(e -> handler.onPreviewFile(fileList.getSelectedValue()));
        rename.addActionListener(e -> handler.onRenameFile(fileList.getSelectedValue()));
        copy.addActionListener(e -> handler.onCopyFile(fileList.getSelectedValue()));
        paste.addActionListener(e -> handler.onPasteFile());
        count.addActionListener(e -> handler.onCountFiles(fileList.getSelectedValue()));

        add(preview);
        add(rename);
        add(copy);
        add(paste);
        add(count);
    }
}