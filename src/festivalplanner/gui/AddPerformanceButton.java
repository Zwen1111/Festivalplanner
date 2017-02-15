package festivalplanner.gui;

import festivalplanner.data.Database;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class AddPerformanceButton extends JButton implements PerformanceOverview.OnClosedListener{

    private boolean hasOpened;
    public AddPerformanceButton(Database database) {
        hasOpened =false;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setIcon(new ImageIcon(getClass().getResource("/icon's/addIcon.png")));
        addActionListener(e -> {
            if(hasOpened == false)
            {
                new PerformanceOverview(database, null).addListener(this);
                hasOpened = true;
            }

        });
    }

    @Override
    public void onClosed() {
        hasOpened = false;
    }
}


