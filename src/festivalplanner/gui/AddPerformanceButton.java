package festivalplanner.gui;

import festivalplanner.data.Database;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class AddPerformanceButton extends JButton {

    public AddPerformanceButton(Database database) {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setIcon(new ImageIcon(getClass().getResource("/icon's/addIcon.png")));
        addActionListener(e -> {
            /*if(database.getStages().size() == 0) {
                JOptionPane.showMessageDialog(null,"Please add a stage");
                return;
            }else if(database.getArtists().size() == 0) {
                JOptionPane.showMessageDialog(null,"Please add a Artist");
                return;
            }*/
            new AddPerformanceGui(database);
        });

    }
}
