package festivalplanner.gui;

import festivalplanner.data.Database;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Maarten on 07/02/2017.
 */
public class AddButton extends JButton {
    AddButton(Database database, ArrayList<JButton> buttons, JComponent component) {

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setIcon(new ImageIcon("Assets\\icon's\\addIcon.png"));
        addActionListener(e -> {
            new AddPerformanceGui(database, buttons, component);

        });

    }

    AddButton(Database database) {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setIcon(new ImageIcon("Assets\\icon's\\addIcon.png"));
        addActionListener(e -> {
            new AddPerformanceGui(database);

        });
    }


}
