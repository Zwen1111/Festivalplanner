package festivalplanner.gui.table2d;

import festivalplanner.data.Performance;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class ArtisGui extends JFrame {

    public ArtisGui(Performance performance)  {
        setSize(800,600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        setContentPane(mainPanel);
        JTextField textField = new JTextField(performance.toString());
        mainPanel.add(textField);

        setVisible(true);

    }

}
