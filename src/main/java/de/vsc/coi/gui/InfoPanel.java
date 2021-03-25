package de.vsc.coi.gui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import de.vsc.coi.Info;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class InfoPanel extends JPanel {

    private final List<FormField> fields;
    private final JScrollPane scrollPane;
    private JButton buttonNext;
    private Info initialInfo;

    public InfoPanel() {
        super(new GridLayout(0, 2));
        this.setBackground(Gui.CYBERPUNK_YELLOW);
        fields = new ArrayList<>();

        scrollPane = new JScrollPane(this);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        init();
    }

    public JScrollPane scrollable() {
        return scrollPane;
    }

    private void init() {
        addRow("Name", Info::setName, Info::getName);
        addRow("Author", Info::setAuthor, Info::getAuthor);
        addRow("Version", Info::setVersion, Info::getVersion);
        addRow("Website", Info::setWebsite, Info::getWebsite);
        addRow("Description", Info::setDescription, Info::getDescription);
        addRow("Groups", Info::setGroupsFromString, Info::getGroupsAsString);

        buttonNext = new JButton("Next");
        add(new JLabel());
        add(Gui.wrapInPanel(buttonNext));
    }

    private void addRow(final String name, final BiConsumer<Info, String> setter, final Function<Info, String> getter) {
        final JLabel label = new JLabel(name, JLabel.LEFT);
        final JTextField textField = new JTextField();
        this.add(label);
        this.add(textField);
        this.fields.add(new FormField(textField, setter, getter));
    }

    public void fromInfo(final Info info) {
        this.initialInfo = info;
        this.fields.forEach(x -> x.setFormValue(info));
    }

    public Info toInfo() {
        final Info info = new Info();
        this.fields.forEach(x -> x.saveFormValue(info));
        return info;
    }

    public Info toInfoIfChanched() {
        final Info newInfo = toInfo();
        if (newInfo.equals(initialInfo)) {
            return null;
        }
        return newInfo;
    }

    public void onNext(final ActionListener listener) {
        this.buttonNext.addActionListener(listener);
    }

    @AllArgsConstructor
    @Getter
    public static class FormField {

        final JTextField field;
        final BiConsumer<Info, String> setter;
        final Function<Info, String> getter;

        public void setFormValue(final Info info) {
            field.setText(getter.apply(info).trim());
        }

        public final void saveFormValue(final Info info) {
            setter.accept(info, field.getText().trim());
        }
    }
}
