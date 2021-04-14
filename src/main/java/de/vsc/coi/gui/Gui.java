package de.vsc.coi.gui;

import static de.vsc.coi.config.ProjectConfig.project;
import static de.vsc.coi.utils.FileReaderUtils.resourceAsStream;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.vsc.coi.Info;

public class Gui extends JFrame {

    private static final Logger LOGGER = LogManager.getLogger(Gui.class);
    public static final Color CYBERPUNK_YELLOW = Color.decode("#f8f102");
    private static final String[] taskbarLogos = new String[] {"logo 16.png", "logo 32.png", "logo 64.png",
            "logo 128.png"};

    private final GridBagConstraints constraints;
    private InfoPanel infoPanel;
    private JLabel statusLabel;
    private QuestionPanel questionPanel;

    public Gui() {
        super("CpOIC");
        this.setLayout(new GridBagLayout());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
    }

    public static JPanel wrapInPanel(final Component component) {
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CYBERPUNK_YELLOW);
        panel.add(component, new GridBagConstraints());
        return panel;
    }

    public void init() throws URISyntaxException, IOException {
        final ImageIcon logo = new ImageIcon(ImageIO.read(resourceAsStream("gui/logo.png")));

        this.setIconImages(getLogos());
        final JLabel logoLabel = new JLabel(logo, JLabel.CENTER);
        this.statusLabel = new JLabel("Starting...", JLabel.CENTER);
        this.statusLabel.setBorder(new LineBorder(Color.blue));
        this.statusLabel.setFont(new Font("Serif", Font.PLAIN, 42));
        this.getContentPane().setBackground(CYBERPUNK_YELLOW);

        this.addRow(logoLabel);
        this.addRow(statusLabel);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(1218, 609);
        this.setVisible(true);
    }

    public void displayError(final String errorMessage, final ActionListener onButtonPressed) {
        this.setStatus(errorMessage);
        this.statusLabel.setForeground(Color.red);
        final JButton button = new JButton("OK");
        final JPanel errorPanel = wrapInPanel(button);
        button.addActionListener(e -> {
            this.statusLabel.setForeground(Color.black);
            this.remove(errorPanel);
            this.revalidate();
            this.repaint();
            onButtonPressed.actionPerformed(e);
        });
        this.addRow(errorPanel);
        //JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public InfoPanel showInfoPanel(final Info info) {
        infoPanel = new InfoPanel();
        infoPanel.fromInfo(info);
        this.addRow(infoPanel.scrollable());
        this.pack();
        return infoPanel;
    }

    public Info closeInfoPanel() {
        this.remove(infoPanel.scrollable());
        this.revalidate();
        this.repaint();
        final Info info = this.infoPanel.toInfoIfChanched();
        this.infoPanel = null;
        return info;
    }

    public void openYesNoQuestion(final String question, final ActionListener onButtonPress) {
        this.setStatus(question);
        questionPanel = new QuestionPanel(onButtonPress);
        this.addRow(questionPanel);
    }

    public boolean getQuestionAnswer() {
        final Boolean answer = questionPanel.getAnswer();
        this.remove(questionPanel);
        this.revalidate();
        this.repaint();
        return answer;
    }

    public void finished() {
        this.setStatus("Finished!");
        final JButton button = new JButton("OK");
        button.addActionListener(e -> close());
        this.addRow(wrapInPanel(button));
    }

    public void setStatus(final String message) {
        LOGGER.info("Status: " + message);
        this.statusLabel.setText("<html><div style='text-align: center;'>" + message + "</div></html>");
    }

    public void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private List<Image> getLogos() throws IOException {
        final List<Image> images = new ArrayList<>();
        for (final String x : taskbarLogos) {
            images.add(ImageIO.read(resourceAsStream("gui/taskbar/" + x)));
        }
        return images;
    }

    public void addRow(final Component comp) {
        constraints.gridy++;
        super.add(comp, constraints);
        this.revalidate();
    }

    public void showNexusPage() {
        setStatus("Please go to " + project().getNexusUrl() + " to update the CpOIC.<br/>The address was copied to "
                + "your clipboard.");
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(project().getNexusUrl()), null);
    }
}
