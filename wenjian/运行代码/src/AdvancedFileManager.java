import javax.swing.*;

public class AdvancedFileManager extends JFrame {
    private FileTreePanel treePanel;
    private FileListPanel listPanel;
    private ControlPanel controlPanel;
    private StatusPanel statusPanel;
    private FileOperationHandler fileOperationHandler;

    public AdvancedFileManager() {
        setTitle("简易文件资源管理器 - Advanced");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        fileOperationHandler = new FileOperationHandler(this);
        treePanel = new FileTreePanel(fileOperationHandler);
        listPanel = new FileListPanel(fileOperationHandler);
        controlPanel = new ControlPanel(fileOperationHandler);
        statusPanel = new StatusPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, listPanel);
        splitPane.setDividerLocation(300);

        getContentPane().add(controlPanel, java.awt.BorderLayout.NORTH);
        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);
        getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

        // 控制器连接
        fileOperationHandler.setPanels(treePanel, listPanel, controlPanel, statusPanel);

        // 初始状态
        treePanel.selectDefault();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdvancedFileManager::new);
    }
}