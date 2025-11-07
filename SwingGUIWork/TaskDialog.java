package SwingGUIWork;

import javax.swing.*;
import java.awt.*;

public class TaskDialog extends JDialog{
    private JComboBox<String> dayComboBox;
    private JSpinner startHourSpinner, startMinuteSpinner, endHourSpinner, endMinuteSpinner;
    private JTextField activityTextField;
    private JButton saveButton,cancelButton;
    private RoutineEntry<String> newEntry = null, existingEntry;

    private TaskDialog(Frame owner, RoutineEntry<String> entryToEdit) {
        super(owner, "", true);
        this.existingEntry = entryToEdit;
        setLayout(new BorderLayout(10, 10));

        initComponents(owner);
    }

    private void initComponents(Frame owner){     
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        dayComboBox = new JComboBox<>(days);
        formPanel.add(new JLabel("Day: "));
        formPanel.add(dayComboBox);

        JPanel startTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        startHourSpinner = new JSpinner(new SpinnerNumberModel(9, 0, 23, 1));
        startMinuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        startTimePanel.add(startHourSpinner);
        startTimePanel.add(new JLabel(":"));
        startTimePanel.add(startMinuteSpinner);

        formPanel.add(new JLabel("Start Time (HH:MM): "));
        formPanel.add(startTimePanel);

        JPanel endTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        endHourSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 23, 1));
        endMinuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        endTimePanel.add(endHourSpinner);
        endTimePanel.add(new JLabel(":"));
        endTimePanel.add(endMinuteSpinner);

        formPanel.add(new JLabel("End Time (HH:MM): "));
        formPanel.add(endTimePanel);

        activityTextField = new JTextField();
        formPanel.add(new JLabel("Task: "));
        formPanel.add(activityTextField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        saveButton = new JButton();
        cancelButton = new JButton("Cancel");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH); 

        if(existingEntry == null){
            setTitle("Add new Task");
            saveButton.setText("Add");
        } else {
            setTitle("Edit Task");
            saveButton.setText("Update");
            populateFields();
        }

        this.getRootPane().setDefaultButton(saveButton);

        pack();
        setLocationRelativeTo(owner);
    }

    public static TaskDialog createAddTaskDialog(Frame owner){
        return new TaskDialog(owner, null);
    }

    public static TaskDialog createEditTaskDialog(Frame owner, RoutineEntry<String> entryToEdit) {
        return new TaskDialog(owner, entryToEdit);
    }

    private void populateFields() {
        dayComboBox.setSelectedItem(existingEntry.getDay());
        startHourSpinner.setValue(existingEntry.getStartHour());
        startMinuteSpinner.setValue(existingEntry.getStartMinute());
        endHourSpinner.setValue(existingEntry.getEndHour());
        endMinuteSpinner.setValue(existingEntry.getEndMinute());
        activityTextField.setText(existingEntry.getActivity());
    }


    private void onSave() {
        String day = dayComboBox.getSelectedItem().toString();
        int startHour = (int) startHourSpinner.getValue();
        int startMinute = (int) startMinuteSpinner.getValue();
        int endHour = (int) endHourSpinner.getValue();
        int endMinute = (int) endMinuteSpinner.getValue();
        String activity = activityTextField.getText();
    
        if (activity.isBlank()) {
            JOptionPane.showMessageDialog(this, "Task Cannot Be Empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int totalStartMinutes = (startHour * 60) + startMinute;
        int totalEndMinutes = (endHour * 60) + endMinute;

        if (totalEndMinutes <= totalStartMinutes) {
            JOptionPane.showMessageDialog(this,
                "Invalid Time: The end time must be after the start time.",
                "Time Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.newEntry = new RoutineEntry<String>(day, startHour, startMinute, endHour, endMinute, activity);
        dispose();
    }


    private void onCancel(){
        this.newEntry = null;
        dispose();
    }    

    public RoutineEntry<String> getNewEntry() {
        return newEntry;
    }
}