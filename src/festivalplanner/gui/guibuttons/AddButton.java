package festivalplanner.gui.guibuttons;

import festivalplanner.data.Database;
import festivalplanner.gui.AddPerformanceGui;

import javax.swing.*;

/**
 * Created by Maarten on 07/02/2017.
 * Created by Maarten on 07/02/2017.
 */
public class AddButton extends JButton {

    public AddButton(Database database, JComponent component) {

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        if (System.getProperty("os.name").contains("Windows")) {
            setIcon(new ImageIcon("Assets\\icon's\\addIcon.png"));
        }else if (System.getProperty("os.name").contains("Mac")) {
            setIcon(new ImageIcon("Assets/icon's/addIcon.png"));
        }

        addActionListener(e -> {
            new AddPerformanceGui(database, component);

        });

    }

    public AddButton(Database database) {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setIcon(new ImageIcon("Assets\\icon's\\addIcon.png"));
        addActionListener(e -> {
            new AddPerformanceGui(database);

        });
    }


}
