package festivalplanner.gui.table2d;

import festivalplanner.data.Performance;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class CheckPerformanceButton extends JButton {
    public CheckPerformanceButton(Performance performance, int hour) {
        addActionListener(e -> new ArtisGui(performance, hour));
    }
}
