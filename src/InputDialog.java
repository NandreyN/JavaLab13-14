import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

        editBoxPanel = new JPanel(new GridLayout(1, 5));

        editBoxPanel.add(recBookField);
        editBoxPanel.add(surnameField);
        editBoxPanel.add(subjectField);
        editBoxPanel.add(termField);
        editBoxPanel.add(markField);


        MouseListener focusHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ((JTextField)e.getSource()).setText("");
            }
        };

        for (int i = 0; i< editBoxPanel.getComponentCount();i++)
        {
            editBoxPanel.getComponent(i).addMouseListener(focusHandler);
        }

        buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(buttonsPanel, BorderLayout.SOUTH);
        add(editBoxPanel, BorderLayout.CENTER);


        okButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                record = new Record();
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

        cancelButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
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
