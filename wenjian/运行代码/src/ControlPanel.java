import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    JTextField searchField;
    JButton searchBtn, copyBtn, pasteBtn, renameBtn, previewBtn, countBtn;
    FileOperationHandler handler;

    public ControlPanel(FileOperationHandler handler) {
        this.handler = handler;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(25);
        searchBtn = new JButton("查找文件");
        copyBtn = new JButton("复制");
        pasteBtn = new JButton("粘贴");
        renameBtn = new JButton("重命名");
        previewBtn = new JButton("预览文本");
        countBtn = new JButton("统计文件数");

        add(searchField);
        add(searchBtn);
        add(copyBtn);
        add(pasteBtn);
        add(renameBtn);
        add(previewBtn);
        add(countBtn);

        searchBtn.addActionListener(e -> handler.onSearchFile(searchField.getText().trim()));
        copyBtn.addActionListener(e -> handler.onCopyFile(null));
        pasteBtn.addActionListener(e -> handler.onPasteFile());
        renameBtn.addActionListener(e -> handler.onRenameFile(null));
        previewBtn.addActionListener(e -> handler.onPreviewFile(null));
        countBtn.addActionListener(e -> handler.onCountFiles(null));
    }
}