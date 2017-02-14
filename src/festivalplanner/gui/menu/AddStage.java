package festivalplanner.gui.menu;

import festivalplanner.data.Database;
import festivalplanner.data.Stage;

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

        JLabel naamLabel = new JLabel("Name:");
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
            Stage newStage = new Stage(naam.getText());
            for (Stage stage : database.getStages()) {
                if(newStage.equals(stage)) {
                    JOptionPane.showMessageDialog(null,"This stage already exists");
                    return;
                }
            }
            database.addStage(newStage);
            dispose();
        });
        add(confirm);

        JButton cancel = new JButton("Cancel");
        cancel.setBounds(getWidth() - 130,getHeight() - 100, 100,50);
        cancel.addActionListener(e -> {
            dispose();
        });
        add(cancel);



    }
}