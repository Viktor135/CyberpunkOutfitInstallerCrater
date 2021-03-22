package de.vsc.coi;

import static de.vsc.coi.utils.FileReaderUtils.resourceToUrL;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.vsc.coi.utils.FileReaderUtils;

public class Gui extends JFrame {

    private JPanel panel;
    private JLabel statusLabel;

    public Gui() {
        super("CpOIC");

    }

    public void init() throws URISyntaxException, IOException {
        final ImageIcon logo = new ImageIcon(ImageIO.read(resourceToUrL("gui/logo.png")));
        //final ImageIcon workingGif = new ImageIcon(ImageIO.read(resourceToUrL("gui/ajax-loader.gif")));

        this.setIconImages(getLogos());
        final JLabel logoLabel = new JLabel(logo, JLabel.CENTER);
        this.statusLabel = new JLabel("", JLabel.CENTER);
        this.statusLabel.setFont(new Font("Serif", Font.PLAIN, 42));
        this.panel = new JPanel(new GridLayout(2, 1));
        this.panel.setBackground(Color.decode("#f8f102"));

        this.panel.add(logoLabel);
        this.panel.add(statusLabel);
        this.add(panel);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        this.pack();
    }

    public void displayError(final String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void displayMessage(final String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void setStatus(final String message) {
            this.statusLabel.setText(message);
    }

    public void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private List<Image> getLogos() throws URISyntaxException, IOException {
        final List<Image> images = new ArrayList<>();
        final String[] logos = FileReaderUtils.resourceToFile("gui/taskbar").list();
        Objects.requireNonNull(logos);
        for (final String x : logos) {
            images.add(ImageIO.read(resourceToUrL("gui/taskbar/" + x)));
        }
        return images;
    }
}
