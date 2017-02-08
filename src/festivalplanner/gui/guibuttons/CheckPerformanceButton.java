package festivalplanner.gui.guibuttons;

import festivalplanner.gui.ArtisGui;

import javax.swing.*;

/**
 * Created by Maarten on 08/02/2017.
 */
public class CheckPerformanceButton extends JButton {
    public CheckPerformanceButton(int index) {
        addActionListener(e -> {
            new ArtisGui(index);

        });
    }
}
