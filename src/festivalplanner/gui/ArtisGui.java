package festivalplanner.gui;

import javax.swing.*;

/**
 * @author Maarten Nieuwenhuize
 */
public class ArtisGui extends JFrame {

    public ArtisGui(int i)  {
        setSize(800,600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        setContentPane(mainPanel);


        setVisible(true);

    }

}
