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
        setIcon(new ImageIcon("Assets\\icon's\\addIcon.png"));
        addActionListener(e -> {
            new AddPerformanceGui();
        });

    }
}
