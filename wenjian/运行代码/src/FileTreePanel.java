import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileTreePanel extends JPanel {
    JTree tree;
    DefaultTreeModel treeModel;
    DefaultMutableTreeNode rootNode;
    FileOperationHandler handler;

    public FileTreePanel(FileOperationHandler handler) {
        this.handler = handler;
        setLayout(new BorderLayout());
        File rootFile = new File(System.getProperty("user.home"));
        rootNode = new DefaultMutableTreeNode(new FileNode(rootFile));
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        loadChildNodes(rootNode, rootFile, 1);

        tree.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent event) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                FileNode fnode = (FileNode) node.getUserObject();
                if (node.getChildCount() == 1 && ((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject().equals("Loading...")) {
                    node.removeAllChildren();
                    loadChildNodes(node, fnode.file, 2);
                    treeModel.reload(node);
                }
            }
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent event) {}
        });

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) return;
            FileNode fnode = (FileNode) node.getUserObject();
            handler.onDirSelected(fnode.file);
        });

        tree.setCellRenderer(new FileTreeCellRenderer());
        JScrollPane scroll = new JScrollPane(tree);
        add(scroll, BorderLayout.CENTER);
    }

    // 加载子节点
    private void loadChildNodes(DefaultMutableTreeNode parent, File dir, int depth) {
        if (!dir.isDirectory()) return;
        File[] files = dir.listFiles(File::isDirectory);
        if (files == null) return;
        Arrays.sort(files, Comparator.comparing(File::getName));
        for (File f : files) {
            if (f.isHidden()) continue;
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(new FileNode(f));
            if (depth == 1) {
                child.add(new DefaultMutableTreeNode("Loading..."));
            }
            parent.add(child);
        }
    }

    public void selectDefault() {
        tree.setSelectionRow(0);
    }
}