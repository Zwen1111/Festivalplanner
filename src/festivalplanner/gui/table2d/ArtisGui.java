package festivalplanner.gui.table2d;

import festivalplanner.data.Performance;

/**
 * @author Maarten Nieuwenhuize
 */
public class ArtisGui extends JFrame {

    public ArtisGui(Performance performance) {
        setSize(300,400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        setContentPane(mainPanel);
        JTextField textField = new JTextField(performance.toString());
        mainPanel.add(textField);

        setVisible(true);
    }
}
