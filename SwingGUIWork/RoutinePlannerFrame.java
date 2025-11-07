package SwingGUIWork;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoutinePlannerFrame extends JFrame {

    // --- GUI Components ---
    private JTabbedPane weekTabs;
    private JPanel controlPanel;
    private JButton addTaskButton, editTaskButton, deleteWeekButton;
    private JCheckBox repeatWeekCheckBox;
    
    // --- REFACTORED DATA STRUCTURES ---
    private List<Map<String, List<RoutineEntry<String>>>> allWeeksData;
    private List<JTable> allWeekTables;

    private boolean isModifyingTabs = false;

    public RoutinePlannerFrame() {
        // --- Frame Setup ---
        setTitle("Weekly Routine Planner by Satyam Mondal - 11700124058");
        setSize(1080, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Data & GUI Initialization ---
        initData();
        initComponents();
    }

    /**
     * Initializes the data structures for ALL weeks.
     */
    private void initData() {
        // Initialize our new Lists
        allWeeksData = new ArrayList<>();
        allWeekTables = new ArrayList<>();

        // Add the data for Week 1
        allWeeksData.add(DailyRoutineGUI.initializeWeeklyData());
    }

    /**
     * Initializes all GUI components.
     */
    private void initComponents() {
        weekTabs = new JTabbedPane();

        // Create and add the table for Week 1
        JTable week1Table = createWeekTable();
        allWeekTables.add(week1Table); // Add it to our new list
        JScrollPane scrollPane1 = new JScrollPane(week1Table);
        weekTabs.addTab("Week 1", scrollPane1);

        // Add the "+" tab
        weekTabs.addTab("+", null);
        
        // --- NEW: Add a listener to the tabs ---
        weekTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(isModifyingTabs){
                    return;
                }                
                int selectedIndex = weekTabs.getSelectedIndex();
                int plusTabIndex = weekTabs.getTabCount() - 1;
                // Logic for adding a new week
                if (selectedIndex == plusTabIndex) {
                    isModifyingTabs = true;
                    int newTabIndex = addNewWeek();
                    isModifyingTabs = false;

                    weekTabs.setSelectedIndex(newTabIndex);
                }
                updateButtonVisibility();
            }
        });
        add(weekTabs, BorderLayout.CENTER);
        
        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addTaskButton = new JButton("Add Task");
        editTaskButton = new JButton("Edit");
        deleteWeekButton = new JButton("Delete This Week");
        repeatWeekCheckBox = new JCheckBox("Repeat Previous Week");

        deleteWeekButton.setVisible(false); 
        repeatWeekCheckBox.setVisible(false);

        controlPanel.add(addTaskButton);
        controlPanel.add(editTaskButton);
        controlPanel.add(deleteWeekButton);
        controlPanel.add(repeatWeekCheckBox);
        add(controlPanel, BorderLayout.SOUTH);

        addTaskButton.addActionListener(e -> onAddTask());
        editTaskButton.addActionListener(e -> onEditTask());
        deleteWeekButton.addActionListener(e -> onDeleteWeek());
        
        repeatWeekCheckBox.addActionListener(e -> {
            if(repeatWeekCheckBox.isSelected()) {
                onRepeatWeek();
            }
        });        
    }

    private void onRepeatWeek() {
        int currnetIndex = weekTabs.getSelectedIndex(), sourceIndex = - 1;

        if(currnetIndex == 0) {
            return;
        } else if(currnetIndex == 1) {
            sourceIndex = 0;
        } else {
            List<String> prevWeekNames = new ArrayList<>();

            for(int i = 0; i < currnetIndex; i++) {
                prevWeekNames.add(weekTabs.getTitleAt(i));
            }

            String[] options = prevWeekNames.toArray(new String[0]);
            String chosenWeekName = (String) JOptionPane.showInputDialog(
                this,
                "Which previous week do you want to copy?", 
                "Choose Week to Repeat",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[options.length - 1]
            );

            if(chosenWeekName == null){
                repeatWeekCheckBox.setSelected(false);
                return;
            }

            for(int i = 0; i < options.length; i++) {
                if(options[i].equals(chosenWeekName)) {
                    sourceIndex = i;
                    break;
                }
            }
        }

        if (sourceIndex != -1) {
            copyWeek(sourceIndex, currnetIndex);
        }

        repeatWeekCheckBox.setSelected(false);
    }

    private void copyWeek(int sourceIndex, int currnetIndex) {
        String sourceWeekName = weekTabs.getTitleAt(sourceIndex);
        String destWeekName = weekTabs.getTitleAt(currnetIndex);

        int choice = JOptionPane.showConfirmDialog(this,
            "This will copy all tasks from '" + sourceWeekName + "' to '" + destWeekName + "'.\n" + 
            "Existing tasks in '" + destWeekName + "' will be deleted.\n\n" +
            "Are you sure you want to continue?",
            "Confirm Repeat Week",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if(choice == JOptionPane.NO_OPTION) {
            return;
        }

        Map<String, List<RoutineEntry<String>>> sourceData = allWeeksData.get(sourceIndex);
        Map<String, List<RoutineEntry<String>>> destData = allWeeksData.get(currnetIndex);

        for(Map.Entry<String, List<RoutineEntry<String>>> entry : sourceData.entrySet()) {
            String day = entry.getKey();
            
            List<RoutineEntry<String>> destTasks = destData.get(day), sourceTasks = entry.getValue();
            destTasks.clear();

            for(RoutineEntry<String> task : sourceTasks) {
                RoutineEntry<String> copy = new RoutineEntry<String>(
                    task.getDay(), 
                    task.getStartHour(), task.getStartMinute(), 
                    task.getEndHour(), task.getEndMinute(), 
                    task.getActivity()
                );
                destTasks.add(copy);
            }            
        }
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for(String day : days) {
            updateTableForDay(day);
        }
    }

    private void onDeleteWeek() {
        int selectedIndex = weekTabs.getSelectedIndex();
        
        if (selectedIndex == weekTabs.getTabCount() - 1) {
            return;
        } else if (selectedIndex == 0) {
            JOptionPane.showMessageDialog(this,
                "You cannot delete Week 1.",
                "Action Not Allowed",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String weekName = weekTabs.getTitleAt(selectedIndex);
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to permanently delete '" +
            weekName + "' and all its tasks?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            
            isModifyingTabs = true; 
        
            weekTabs.remove(selectedIndex);
            allWeeksData.remove(selectedIndex);
            allWeekTables.remove(selectedIndex);

            for (int i = selectedIndex; i < weekTabs.getTabCount() - 1; i++) {
                weekTabs.setTitleAt(i, "Week " + (i + 1));
            }
            
            isModifyingTabs = false; 
            weekTabs.setSelectedIndex(selectedIndex - 1); 
        }
    }
    private int addNewWeek() {
        Map<String, List<RoutineEntry<String>>> newWeekData = DailyRoutineGUI.initializeWeeklyData();
        JTable newWeekTable = createWeekTable();

        allWeeksData.add(newWeekData);
        allWeekTables.add(newWeekTable);

        JScrollPane newScrollPane = new JScrollPane(newWeekTable);
        int newWeekIndex = weekTabs.getTabCount() - 1; // Index where the "+" tab is
        String newWeekName = "Week " + (newWeekIndex + 1);

        weekTabs.insertTab(newWeekName, null, newScrollPane, null, newWeekIndex);
    
        return newWeekIndex;
    }

    private void updateButtonVisibility() {
        int selectedIndex = weekTabs.getSelectedIndex();

        if (selectedIndex < 0) return; 

        int plusTabIndex = weekTabs.getTabCount() - 1;

        if (selectedIndex == 0) {
            // Week 1 is selected
            repeatWeekCheckBox.setVisible(false);
            deleteWeekButton.setVisible(false);
        } else if (selectedIndex < plusTabIndex) {
            // Any other week (Week 2, 3, etc.) is selected
            repeatWeekCheckBox.setVisible(true);
            deleteWeekButton.setVisible(true);
        } else {
            // The "+" tab is selected
            repeatWeekCheckBox.setVisible(false);
            deleteWeekButton.setVisible(false);
        }
    }

    private JTable createWeekTable() {
        String[] columnNames = {"Day", "Tasks"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 7) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        for (int i = 0; i < days.length; i++) {
            tableModel.setValueAt(days[i], i, 0);
        }
        table.setRowHeight(100);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(0).setMaxWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(500);
        table.setDefaultRenderer(Object.class, new MultiLineCellRenderer());

        return table;
    }
    
    private Map<String, List<RoutineEntry<String>>> getActiveDataMap() {
        int index = weekTabs.getSelectedIndex();
        return allWeeksData.get(index);
    }

    private JTable getActiveTable() {
        int index = weekTabs.getSelectedIndex();
        return allWeekTables.get(index);
    }

    private void onAddTask() {
        TaskDialog dialog = TaskDialog.createAddTaskDialog(this);
        dialog.setVisible(true);
        RoutineEntry<String> entry = dialog.getNewEntry();
        
        if (entry != null) {
            // Get the data for the currently selected week and add to it
            Map<String, List<RoutineEntry<String>>> currentData = getActiveDataMap();
            currentData.get(entry.getDay()).add(entry);
            
            // Update the table for that day
            updateTableForDay(entry.getDay());
        }
    }

    private void onEditTask() {
        JTable currentTable = getActiveTable();
        Map<String, List<RoutineEntry<String>>> currentData = getActiveDataMap();

        int selectedRow = currentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this, 
                "Please select a day (row) from the table first.", 
                "No Day Selected", 
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String day = (String) currentTable.getValueAt(selectedRow, 0);
        List<RoutineEntry<String>> tasksForDay = currentData.get(day);

        if (tasksForDay.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, 
                "There are no tasks to edit for " + day + ".", 
                "No Tasks", 
                JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        RoutineEntry[] taskArray = tasksForDay.toArray(new RoutineEntry[0]);
        RoutineEntry<String> taskToEdit = (RoutineEntry<String>) JOptionPane.showInputDialog(
            this, "Please select a task to edit:", "Edit Task",
            JOptionPane.PLAIN_MESSAGE, null, taskArray, taskArray[0]);

        if (taskToEdit == null) return; // User cancelled

        TaskDialog dialog = TaskDialog.createEditTaskDialog(this, taskToEdit);
        dialog.setVisible(true);
        RoutineEntry<String> updatedEntry = dialog.getNewEntry();

        if (updatedEntry != null) {            
            currentData.get(taskToEdit.getDay()).remove(taskToEdit);
            
            currentData.get(updatedEntry.getDay()).add(updatedEntry);

            updateTableForDay(taskToEdit.getDay());

            if (!taskToEdit.getDay().equals(updatedEntry.getDay())) {
                updateTableForDay(updatedEntry.getDay());
            }
        }
    }

    private void updateTableForDay(String day) {
        JTable currentTable = getActiveTable();
        Map<String, List<RoutineEntry<String>>> currentData = getActiveDataMap();
        
        int row = getRowForDay(day); 
        if (row == -1) return;

        List<RoutineEntry<String>> entries = currentData.get(day);
        
        StringBuilder html = new StringBuilder("<html>");
        for (RoutineEntry<String> entry : entries) {
            html.append(entry.getFormattedEntry()).append("<br>");
        }
        html.append("</html>");

        DefaultTableModel model = (DefaultTableModel) currentTable.getModel();
        model.setValueAt(html.toString(), row, 1);
    }

    private int getRowForDay(String day) {
        JTable currentTable = getActiveTable();
        DefaultTableModel model = (DefaultTableModel) currentTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(day)) {
                return i;
            }
        }
        return -1;
    }
}