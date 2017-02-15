package festivalplanner.gui.table2d;

import festivalplanner.data.Performance;
import festivalplanner.data.Database;
import festivalplanner.gui.PerformanceOverview;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Maarten Nieuwenhuize
 */
public class CheckPerformanceButton extends JButton implements PerformanceOverview.OnClosedListener{
    private boolean hasOpened;
    public CheckPerformanceButton(Performance performance, Database database) {
        hasOpened = false;
        addActionListener(e -> {
            if(hasOpened == false)
            {
                PerformanceOverview performanceOverview = new PerformanceOverview(database, performance);
                performanceOverview.setLocation(getBounds().getLocation());
                performanceOverview.addListener(this);
                hasOpened = true;
            }



        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(SwingUtilities.isRightMouseButton(e)) {
                    int optioncode = JOptionPane.showConfirmDialog(null,"Do you want to remove this Performance");
                    if(optioncode == JOptionPane.OK_OPTION) {
                        if(database.removePerformance(performance) == false) {
                            JOptionPane.showMessageDialog(null,"Error performance not removed");
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClosed() {
        hasOpened = false;
    }
}
