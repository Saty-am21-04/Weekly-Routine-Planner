package SwingGUIWork;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
 
    public MultiLineCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        if(!isSelected && row % 2 == 1) {
            setBackground(new Color(240, 240, 240));
        }

        if (column == 0 ){
            setFont(table.getFont().deriveFont(Font.BOLD));
            setText((value == null) ? "" : value.toString());
        } else {
            setFont(table.getFont().deriveFont(Font.PLAIN));
            String text = (value == null) ? "" : value.toString();

            text = text.replaceAll("<br>", "\n")
                       .replaceAll("<html>", "") 
                       .replaceAll("</html>", "");
            setText(text);
        }

        return this;
    }
}