package festivalplanner.gui.table2d;

import festivalplanner.data.Performance;
import festivalplanner.data.Database;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class CheckPerformanceButton extends JButton {
    public CheckPerformanceButton(Performance performance, Database database) {

        addActionListener(e -> new ArtisGui(performance, database,getBounds()));
    }
}
