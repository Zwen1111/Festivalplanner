package festivalplanner.gui;

import festivalplanner.data.Database;
import festivalplanner.gui.table2d.AddPerformanceGui;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class AddPerformanceButton extends JButton {

    public AddPerformanceButton(Database database) {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setIcon(new ImageIcon(getClass().getResource("/icon's/addIcon.png")));
        addActionListener(e -> new AddPerformanceGui(database));
    }
}
