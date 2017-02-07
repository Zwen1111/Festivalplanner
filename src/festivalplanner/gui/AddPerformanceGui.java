package festivalplanner.gui;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import javax.swing.*;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by Maarten on 07/02/2017.
 */
public class AddPerformanceGui extends  JFrame {
    private Database database;
    public AddPerformanceGui(Database database, ArrayList<JButton> buttons, JComponent component) {
        this.database = database;
        init();



        JButton button = new JButton();
        button.addActionListener(e -> {
            new ArtisGui(buttons.size());
        });
        buttons.add(button);
        component.repaint();
    }

    public void init()
    {
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);



        JPanel mainPanel = new JPanel();
        setContentPane(mainPanel);
        database.getPerformances().add(new Performance(
                new Stage("PaulStage"),
                LocalTime.of(20, 0),
                LocalTime.of(22, 0),
                new Artist("Paul l", null, 5),
                new Artist("Paul de m", null, 6)));
        setVisible(true);
    }

    public AddPerformanceGui(Database database)
    {
        this.database = database;
        init();
    }
}
