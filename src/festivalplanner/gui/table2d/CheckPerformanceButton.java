package festivalplanner.gui.table2d;

import festivalplanner.data.Performance;
import festivalplanner.data.Database;
import festivalplanner.gui.dialog.PerformanceDialog;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Maarten Nieuwenhuize
 */
public class CheckPerformanceButton extends JButton {

    public CheckPerformanceButton(Performance performance) {
        addActionListener(e ->
				new PerformanceDialog(performance, getBounds().getLocation()));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(SwingUtilities.isRightMouseButton(e)) {
                    int optioncode = JOptionPane.showConfirmDialog(null,"Do you want to remove this Performance");
                    if(optioncode == JOptionPane.OK_OPTION) {
                        if(!Database.removePerformance(performance)) {
                            JOptionPane.showMessageDialog(null,"Error performance not removed");
                        }
                    }
                }
            }
        });
    }
}
