package festivalplanner.gui.addMenu;

import festivalplanner.data.Database;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Maarten on 10/02/2017.
 */
public class AddStage extends JFrame {

    public AddStage(Database database)
    {
        setSize(300, 400);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        setContentPane(mainPanel);
        //setUndecorated(true);
        setVisible(true);
        setLayout(null);

        JLabel naamLabel = new JLabel("Naam:");
        naamLabel.setFont(new Font(Font.SERIF,Font.PLAIN,25));
        add(naamLabel);
        naamLabel.setBounds(20,0,100,100);
        JTextField naam = new JTextField("");
        naam.setFont(new Font(Font.SERIF,Font.PLAIN,25));
        add(naam);
        naam.setBounds(130,25,150,50);

        JButton confirm = new JButton("Confirm");
        confirm.setBounds(30,getHeight() - 100, 100,50);
        confirm.addActionListener(e -> {
            //Hier moet je alles opslaan
            dispose();
        });
        add(confirm);

        JButton cancel = new JButton("Cancel");
        cancel.setBounds(getWidth() - 130,getHeight() - 100, 100,50);
        confirm.addActionListener(e -> {
            dispose();
        });
        add(cancel);



    }
}
