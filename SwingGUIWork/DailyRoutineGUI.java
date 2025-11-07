/*
 * Design a GUI application in java where a daily routine can be automatically set up by the user for each week, using tools of collection framework, generics, and event handling.
 * 
 * Run this file to execute all files..
 */

package SwingGUIWork;

import javax.swing.*;
import java.util.*;

public class DailyRoutineGUI {

    public static Map<String, List<RoutineEntry<String>>> initializeWeeklyData() {
        Map<String, List<RoutineEntry<String>>> data = new HashMap<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : days) {
            data.put(day, new ArrayList<>());
        }
        return data;
    }

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            RoutinePlannerFrame mainFrame = new RoutinePlannerFrame();
            mainFrame.setVisible(true);
        });
    }
}