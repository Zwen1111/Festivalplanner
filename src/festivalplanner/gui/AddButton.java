package festivalplanner.gui;

import javax.swing.*;

/**
 * Created by Maarten on 07/02/2017.
 */
public class AddButton extends JButton{
    AddButton()
    {

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        if (System.getProperty("os.name").contains("Windows")) {
            setIcon(new ImageIcon("Assets\\icon's\\addIcon.png"));
        }else if (System.getProperty("os.name").contains("Mac")) {
            setIcon(new ImageIcon("Assets/icon's/addIcon.png"));
        }

        addActionListener(e -> {
            new AddPerformanceGui();
        });

    }
}
