package festivalplanner.gui;

import festivalplanner.Main;
import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalTime;

/**
 * @author Maarten Nieuwenhuize, Coen Boelhouwers
 */
public class AddPerformanceGui extends  JFrame {

	private Database database;
	private OnPerformanceCreatedListener listener;

    public AddPerformanceGui(Database database, OnPerformanceCreatedListener l) {
        listener = l;
        this.database = database;
        init();
    }

    private void init() {
        setSize(300, 400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        setContentPane(mainPanel);
        /*database.getPerformances().add(new Performance(*/
        Stage paul = new Stage("PaulStage");
        LocalTime paulTime = database.findNextEmptyStageTime(paul, Duration.ofMinutes(135));
        if (listener != null) listener.performanceCreated(new Performance(
                paul,
                paulTime == null ? LocalTime.of(20, 0) : paulTime,
                Duration.ofMinutes(135),
                new Artist("Paul l", null, 5),
                new Artist("Paul de m", null, 6)));
        setVisible(true);

    }
}
