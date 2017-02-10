package festivalplanner.gui.table2d;

import festivalplanner.data.Performance;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class CheckPerformanceButton extends JButton {
    public CheckPerformanceButton(Performance performance) {
        addActionListener(e -> new ArtisGui(performance));
    }
}
