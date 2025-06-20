import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FileOperationHandler {
    private FileTreePanel treePanel;
    private FileListPanel listPanel;
    private ControlPanel controlPanel;
    private StatusPanel statusPanel;
    private AdvancedFileManager frame;

    private File clipboardFile = null;

    public FileOperationHandler(AdvancedFileManager frame) {
        this.frame = frame;
    }

    public void setPanels(FileTreePanel treePanel, FileListPanel listPanel, ControlPanel controlPanel, StatusPanel statusPanel) {
        this.treePanel = treePanel;
        this.listPanel = listPanel;
        this.controlPanel = controlPanel;
        this.statusPanel = statusPanel;
    }

    public void onDirSelected(File dir) {
        listPanel.showFiles(dir);
    }

    public void onFileSelected(File file) {
        // 可扩展显示文件详情
    }

    public void onSearchFile(String name) {
        final File dir = listPanel.currentDir;
        if (name == null || name.isEmpty() || dir == null) {
            JOptionPane.showMessageDialog(frame, "请输入文件名并选择目录！");
            return;
        }
        statusPanel.setStatus("正在查找...");
        statusPanel.setProgressVisible(true);
        statusPanel.setProgressIndeterminate(true);
        SwingWorker<List<File>, Void> worker = new SwingWorker<List<File>, Void>() {
            protected List<File> doInBackground() {
                List<File> result = new ArrayList<>();
                findFiles(dir, name, result);
                return result;
            }
            protected void done() {
                statusPanel.setProgressVisible(false);
                try {
                    List<File> result = get();
                    listPanel.listModel.clear();
                    for (File f : result) listPanel.listModel.addElement(f);
                    statusPanel.setStatus("查找完成，共 " + result.size() + " 个文件");
                } catch (InterruptedException | ExecutionException e) {
                    statusPanel.setStatus("查找出错");
                }
            }
        };
        worker.execute();
    }

    private void findFiles(File dir, String name, List<File> result) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.getName().contains(name)) result.add(f);
            if (f.isDirectory()) findFiles(f, name, result);
        }
    }

    public void onCopyFile(File file) {
        if (file == null) file = listPanel.getSelectedFile();
        if (file != null) {
            clipboardFile = file;
            statusPanel.setStatus("已复制: " + file.getName());
        } else {
            statusPanel.setStatus("请先选中文件");
        }
    }

    public void onPasteFile() {
        if (clipboardFile == null || listPanel.currentDir == null) {
            statusPanel.setStatus("剪贴板为空或未选择目录");
            return;
        }
        final File dest = getNoOverwriteFile(new File(listPanel.currentDir, clipboardFile.getName()));
        statusPanel.setStatus("正在粘贴...");
        statusPanel.setProgressVisible(true);
        statusPanel.setProgressIndeterminate(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                if (clipboardFile.isDirectory()) {
                    copyDir(clipboardFile, dest);
                } else {
                    Files.copy(clipboardFile.toPath(), dest.toPath());
                }
                return null;
            }
            protected void done() {
                statusPanel.setProgressVisible(false);
                listPanel.showFiles(listPanel.currentDir);
                statusPanel.setStatus("粘贴完成: " + dest.getName());
            }
        };
        worker.execute();
    }

    private File getNoOverwriteFile(File file) {
        if (!file.exists()) return file;
        String name = file.getName();
        String base = name, ext = "";
        int dot = name.lastIndexOf('.');
        if (dot != -1) {
            base = name.substring(0, dot);
            ext = name.substring(dot);
        }
        int i = 1;
        File f = new File(file.getParent(), base + "_copy" + ext);
        while (f.exists()) {
            f = new File(file.getParent(), base + "_copy" + (i++) + ext);
        }
        return f;
    }

    private void copyDir(File src, File dst) throws IOException {
        if (!dst.exists()) dst.mkdir();
        for (File f : src.listFiles()) {
            File newDst = new File(dst, f.getName());
            if (f.isDirectory()) {
                copyDir(f, newDst);
            } else {
                Files.copy(f.toPath(), newDst.toPath());
            }
        }
    }

    public void onPreviewFile(File file) {
        if (file == null) file = listPanel.getSelectedFile();
        if (file == null || !file.isFile() || !isTextFile(file.getName())) {
            statusPanel.setStatus("请选择支持的文本文件(.txt,.java,.ini,.bat)");
            return;
        }
        final File finalFile = file;
        statusPanel.setStatus("正在预览...");
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            protected String doInBackground() {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(finalFile), "UTF-8"))) {
                    String line;
                    int count = 0;
                    while ((line = reader.readLine()) != null && count < 100) {
                        sb.append(line).append("\n");
                        count++;
                    }
                } catch (IOException e) {
                    sb.append("读取失败: ").append(e.getMessage());
                }
                return sb.toString();
            }
            protected void done() {
                statusPanel.setStatus("预览完成");
                try {
                    String content = get();
                    JTextArea area = new JTextArea(content);
                    area.setEditable(false);
                    JScrollPane pane = new JScrollPane(area);
                    pane.setPreferredSize(new java.awt.Dimension(600, 400));
                    JOptionPane.showMessageDialog(frame, pane, "预览: " + finalFile.getName(), JOptionPane.PLAIN_MESSAGE);
                } catch (Exception e) {}
            }
        };
        worker.execute();
    }

    private boolean isTextFile(String name) {
        return name.endsWith(".txt") || name.endsWith(".java")
                || name.endsWith(".ini") || name.endsWith(".bat");
    }

    public void onRenameFile(File file) {
        if (file == null) file = listPanel.getSelectedFile();
        if (file == null) {
            statusPanel.setStatus("请先选中文件");
            return;
        }
        final File finalFile = file;
        String newName = JOptionPane.showInputDialog(frame, "输入新文件名:", finalFile.getName());
        if (newName != null && !newName.trim().isEmpty()) {
            File newFile = new File(finalFile.getParent(), newName);
            if (finalFile.renameTo(newFile)) {
                listPanel.showFiles(listPanel.currentDir);
                statusPanel.setStatus("重命名成功");
            } else {
                statusPanel.setStatus("重命名失败");
            }
        }
    }

    public void onCountFiles(File file) {
        if (file == null) file = listPanel.getSelectedFile();
        if (file == null) file = listPanel.currentDir;
        if (file == null || !file.isDirectory()) {
            statusPanel.setStatus("请先选择文件夹");
            return;
        }
        final File finalFile = file;
        statusPanel.setStatus("正在统计...");
        statusPanel.setProgressVisible(true);
        statusPanel.setProgressIndeterminate(true);
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            protected Integer doInBackground() {
                return countFiles(finalFile);
            }
            protected void done() {
                statusPanel.setProgressVisible(false);
                try {
                    int count = get();
                    statusPanel.setStatus("共有文件: " + count + " 个");
                    JOptionPane.showMessageDialog(frame, "文件夹内共有文件: " + count + " 个");
                } catch (Exception e) {
                    statusPanel.setStatus("统计失败");
                }
            }
        };
        worker.execute();
    }

    private int countFiles(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return 0;
        int count = 0;
        for (File f : files) {
            if (f.isFile()) count++;
            else if (f.isDirectory()) count += countFiles(f);
        }
        return count;
    }
}