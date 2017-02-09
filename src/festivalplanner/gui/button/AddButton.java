package festivalplanner.gui.button;

import festivalplanner.data.Database;
import festivalplanner.gui.AddPerformanceGui;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class AddButton extends JButton {

    public AddButton(Database database, JComponent component) {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setIcon(new ImageIcon(getClass().getResource("/icon's/addIcon.png")));
        addActionListener(e -> new AddPerformanceGui(database, component));
    }

    public AddButton(Database database) {
        this(database, null);
    }
}
