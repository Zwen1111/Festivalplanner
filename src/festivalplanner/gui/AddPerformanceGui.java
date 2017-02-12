package festivalplanner.gui.table2d;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author Maarten Nieuwenhuize, Zwen van Erkelens, Coen Boelhouwers
 */
public class AddPerformanceGui extends JFrame {

    private static final double WEIGHT_LEFT = 0.1;
    private static final double WEIGHT_RIGHT = 0.9;

    private JComboBox<String> stageComboBox;
    private JList<Artist> artistJList;

    private JSpinner startTimeJSpinner;
    private JSpinner endTimeJSpinner;

    private Database database;

    public AddPerformanceGui(Database database) {
        setSize(350, 300);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.database = database;

        artistJList = setupArtistsList(database);
        // Select the current performing artist(s) in the list.
        database.getArtists().forEach(artist -> {
            int index = database.getArtists().indexOf(artist);
            artistJList.addSelectionInterval(index, index);
        });

        stageComboBox = new JComboBox<>();
        database.getStages().forEach(stage -> stageComboBox.addItem(stage.getName()));
        stageComboBox.setSelectedIndex(0);

        //Start time ComboBox
        startTimeJSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE));
        startTimeJSpinner.setEditor(new JSpinner.DateEditor(startTimeJSpinner, "HH:mm"));

        //End time ComboBox
        endTimeJSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE));
        endTimeJSpinner.setEditor(new JSpinner.DateEditor(endTimeJSpinner, "HH:mm"));

        // Sadly, there are few ways to convert between an old Date and new LocalTime.
        DateFormat time = new SimpleDateFormat("HH:mm");
        try {
            startTimeJSpinner.setValue(time.parse("10:00"));
            endTimeJSpinner.setValue(time.parse("12:00"));
        } catch (ParseException e) {
            System.err.println("Could not parse the time. Should not normally happen.");
        }

        //Create buttons
        JButton addButton = new JButton("Save");
        addButton.addActionListener(e -> saveButton(database));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> closeButton());

        // Position those widgets
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(new JLabel("Stage:"), constraints(0, 0, WEIGHT_LEFT, 0));
        centerPanel.add(stageComboBox, constraints(1, 0, WEIGHT_RIGHT, 0));
        centerPanel.add(new JLabel("Start time:"), constraints(0, 1, WEIGHT_LEFT, 0));
        centerPanel.add(startTimeJSpinner, constraints(1, 1, WEIGHT_RIGHT, 0));
        centerPanel.add(new JLabel("End time:"), constraints(0, 2, WEIGHT_LEFT, 0));
        centerPanel.add(endTimeJSpinner, constraints(1, 2, WEIGHT_RIGHT, 0));
        centerPanel.add(new JLabel("Artists:"), constraints(0, 3, WEIGHT_LEFT, 0));
        centerPanel.add(new JScrollPane(artistJList), constraints(1, 3, WEIGHT_RIGHT, 60));
        centerPanel.add(new JLabel("Genres:"), constraints(0, 4, WEIGHT_LEFT, 0));
        centerPanel.add(new DisabledTextField(database.getPerformances().get(0).getArtistGenres()), constraints(1, 4, WEIGHT_RIGHT, 0));
        centerPanel.add(new JLabel("Popularity:"), constraints(0, 5, WEIGHT_LEFT, 0));
        centerPanel.add(new DisabledTextField(String.valueOf(database.getPerformances().get(0).generatePopularity())),
                constraints(1, 5, WEIGHT_RIGHT, 0));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        bottomPanel.add(closeButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private static GridBagConstraints constraints(int x, int y, double weight, int pad) {
        GridBagConstraints leftColumn = new GridBagConstraints();
        leftColumn.weightx = weight;
        leftColumn.gridx = x;
        leftColumn.gridy = y;
        leftColumn.ipady = pad;
        leftColumn.fill = GridBagConstraints.HORIZONTAL;
        leftColumn.insets = new Insets(2,2,2,2);
        return leftColumn;
    }

    private JList<Artist> setupArtistsList(Database database) {
        JList<Artist> list = new JList<>();
        ListModel<Artist> model = new DefaultListModel<Artist>() {
            @Override
            public int getSize() {
                return database.getArtists().size();
            }

            @Override
            public Artist getElementAt(int index) {
                return database.getArtists().get(index);
            }
        };
        list.setModel(model);
        return list;
    }

    public void saveButton(Database database){
        Stage stage = database.getStages().get(stageComboBox.getSelectedIndex());
        LocalTime starTime = LocalDateTime.ofInstant(((Date) startTimeJSpinner.getValue()).toInstant(),
                ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = LocalDateTime.ofInstant(((Date) endTimeJSpinner.getValue()).toInstant(),
                ZoneId.systemDefault()).toLocalTime();
        java.util.List<Artist> artists = artistJList.getSelectedValuesList();
        database.addPerformance(new Performance(
                stage,starTime,endTime,artists
        ));
        database.notifyDataChanged();
        dispose();
    }

    public void closeButton(){
        dispose();
    }


    private static class DisabledTextField extends JTextField {

        public DisabledTextField(String text) {
            super(text);
            setEnabled(false);
        }
    }
}