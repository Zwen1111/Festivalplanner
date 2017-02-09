package festivalplanner.gui.table2d;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class CheckPerformanceButton extends JButton {
    public CheckPerformanceButton(int index) {
        addActionListener(e -> new ArtisGui(index));
    }
}
