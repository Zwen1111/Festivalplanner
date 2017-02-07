package festivalplanner.gui;

import javax.swing.*;

/**
 * Created by Maarten on 06/02/2017.
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
