package de.vsc.coi.gui;

import static de.vsc.coi.gui.Gui.CYBERPUNK_YELLOW;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class QuestionPanel extends JPanel {

    private Boolean answer;

    public QuestionPanel(final ActionListener onButtonPress) {
        super(new GridBagLayout());

        this.setBackground(CYBERPUNK_YELLOW);
        final JButton yes = new JButton("Yes");
        final JButton no = new JButton("No");

        yes.addActionListener((e) -> {
            this.answer = true;
            onButtonPress.actionPerformed(e);
        });
        no.addActionListener((e) -> {
            this.answer = false;
            onButtonPress.actionPerformed(e);
        });
        this.setBackground(CYBERPUNK_YELLOW);
        final GridBagConstraints c = new GridBagConstraints();
        this.add(no, c);
        c.gridy++;
        c.insets = new Insets(20, 20, 20, 20);
        this.add(yes, c);
    }

    public Boolean getAnswer() {
        return answer;
    }
}
