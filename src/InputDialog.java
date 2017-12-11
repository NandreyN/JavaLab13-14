import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

class InputDialog extends JDialog {

    private Record record;
    private JTextField recBookField, surnameField, subjectField, termField, markField;
    private JButton cancelButton, okButton;
    private JPanel buttonsPanel, editBoxPanel;

    InputDialog(JFrame owner) {
        super(owner, "Input", true);

        recBookField = new JFormattedTextField("Record Book number");
        surnameField = new JFormattedTextField("Surname");
        subjectField = new JFormattedTextField("Subject");
        termField = new JFormattedTextField("Term");
        markField = new JFormattedTextField("Mark");

        cancelButton = new JButton("Cancel");
        okButton = new JButton("Ok");
        record = new Record();

        editBoxPanel = new JPanel(new GridLayout(1, 5));

        editBoxPanel.add(recBookField);
        editBoxPanel.add(surnameField);
        editBoxPanel.add(subjectField);
        editBoxPanel.add(termField);
        editBoxPanel.add(markField);

        buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(buttonsPanel, BorderLayout.SOUTH);
        add(editBoxPanel, BorderLayout.CENTER);


        okButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                record.setSurname(surnameField.getText());
                record.setMark(markField.getText());
                record.setTermNumber(termField.getText());
                record.setRecordBookNumber(recBookField.getText());
                record.setSubject(subjectField.getText());

                if (isRecordValid()) {
                    setVisible(false);
                    return;
                }

                JOptionPane.showMessageDialog(null, "Please fill in fileds correctly.");
            }
        });
    }

    private boolean isRecordValid() {
        return !record.getRecordBookNumber().equals("") && !record.getSurname().equals("") && !record.getSubject().equals("")
                && !record.getTermNumber().equals("") && !record.getMark().equals("");
    }

    Record getRecord() {
        return record;
    }
}
