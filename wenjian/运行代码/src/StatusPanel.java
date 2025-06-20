import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    JLabel statusLabel;
    JProgressBar progressBar;

    public StatusPanel() {
        setLayout(new BorderLayout());
        statusLabel = new JLabel(" 就绪 ");
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        add(statusLabel, BorderLayout.WEST);
        add(progressBar, BorderLayout.CENTER);
    }
    public void setStatus(String text) { statusLabel.setText(" " + text + " "); }
    public void setProgressVisible(boolean visible) { progressBar.setVisible(visible); }
    public void setProgressIndeterminate(boolean ind) { progressBar.setIndeterminate(ind); }
    public void setProgressValue(int v) { progressBar.setValue(v); }
}