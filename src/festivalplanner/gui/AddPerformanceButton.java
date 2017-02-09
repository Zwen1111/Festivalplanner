package festivalplanner.gui;

import festivalplanner.data.Database;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class AddPerformanceButton extends JButton {

    public AddPerformanceButton(Database database, OnPerformanceCreatedListener l) {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setIcon(new ImageIcon(getClass().getResource("/icon's/addIcon.png")));
        addActionListener(e -> new AddPerformanceGui(database, l));
    }

}
