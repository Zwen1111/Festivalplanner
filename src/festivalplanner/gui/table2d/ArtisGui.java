package festivalplanner.gui.table2d;

import festivalplanner.Main;
import festivalplanner.data.Artist;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maarten Nieuwenhuize & Zwen van Erkelens
 */
public class ArtisGui extends JFrame {
    private JLabel artistLabel;
    private JComboBox stageComboBox;
    private JComboBox startTimeComboBox;
    private JComboBox endTimeComboBox;
    private JComboBox artistComboBox;
    private JTextField artistText;

    private ArrayList<String> stageNamesOld;
    private ArrayList<LocalTime> hours;
    private ArrayList<String> hoursString;

    private List<String> artists;
    private int i = 0;

    private JTextField genreText = new JTextField("");
    private JTextField popularityText = new JTextField("");

    public ArtisGui(Performance performance, int hour) {
        setSize(220,230);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new FlowLayout());
        JPanel labelPanel = new JPanel(new GridLayout(4,1));
        JPanel textPanel = new JPanel(new GridLayout(4,1));
        JPanel artistPanel = new JPanel(new FlowLayout());
        JPanel artistLabelPanel = new JPanel(new GridLayout(2,1));
        JPanel artistTextPanel = new JPanel(new GridLayout(2,1));
        JPanel buttonPanel = new JPanel(new FlowLayout());

        //Create ArrayList with hours
        hours = new ArrayList<>();
        for (int i = 0; i < hour; i++) {
            for (int minutes = 0; minutes < 60; minutes++) {
                hours.add(LocalTime.of(i,minutes));
            }
        }
        hoursString = new ArrayList<>();
        for (int i = 0; i < hours.size(); i++) {
            hoursString.add(hours.get(i).toString());
        }

        //Create labels
        JLabel stageLabel = new JLabel("Stage:");
        JLabel startTimeLabel = new JLabel("Start time:");
        JLabel endTimeLabel = new JLabel("End time:");
        JLabel genreLabel = new JLabel("Genre:");
        JLabel popularityLabel = new JLabel("Popularity:");

        //Create combobox and the list

            //Stage ComboBox
            stageNamesOld = new ArrayList<>();
            for (Stage stage : Main.getStageArray()) {
                stageNamesOld.add(stage.getName());
            }
            String[] stageNames = new String[stageNamesOld.size()];
            stageNames = stageNamesOld.toArray(stageNames);
            stageComboBox = new JComboBox(stageNames);
            stageComboBox.setSelectedIndex(getRightStage(performance.getStage()));

            //Start time ComboBox
            String[] startTime = new String[hoursString.size()];
            startTime = hoursString.toArray(startTime);
            startTimeComboBox = new JComboBox(startTime);
            startTimeComboBox.setSelectedIndex(getRightTime(performance.getStartTime()));

        //End time ComboBox
        String[] endTime = new String[hoursString.size()];
        endTime = hoursString.toArray(endTime);
        endTimeComboBox = new JComboBox(endTime);
        endTimeComboBox.setSelectedIndex(getRightTime(performance.getEndTime()));

            //Artist ComboBox
            if(performance.getArtists().size() > 1) {
                artists = new ArrayList<>();
                for (Artist artist: performance.getArtists()) {
                    artists.add(artist.getName());
                }
                String[] artistNames = new String[artists.size()];
                artistNames = artists.toArray(artistNames);
                artistComboBox = new JComboBox(artistNames);
                artistLabel = new JLabel("Artists:");
                artistComboBox.addActionListener(e-> {
                    artistInformation(performance.getArtists().get(artistComboBox.getSelectedIndex()));
                });
            }
            else {
                artistLabel = new JLabel("Artist:");
                artistText = new JTextField(performance.getArtistNames());
            }

        //Create buttons
        JButton saveButton= new JButton("Save");
        saveButton.addActionListener(e-> saveButton(performance));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e-> closeButton());

        //Add components on labelPanel
        labelPanel.add(stageLabel);
        labelPanel.add(startTimeLabel);
        labelPanel.add(endTimeLabel);
        labelPanel.add(artistLabel);

        //Add components on textPanel
        textPanel.add(stageComboBox);
        textPanel.add(startTimeComboBox);
        textPanel.add(endTimeComboBox);
        if(performance.getArtists().size() > 1){
            textPanel.add(artistComboBox);
            genreText.setText(performance.getArtists().get(artistComboBox.getSelectedIndex()).getGenre());
            popularityText.setText(Integer.toString(performance.getArtists().get(artistComboBox.getSelectedIndex()).getPopularity()));
        }
        else {
            textPanel.add(artistText);
            genreText.setText(performance.getArtists().get(0).getGenre());
            popularityText.setText(Integer.toString(performance.getArtists().get(0).getPopularity()));
        }

        //Add components on artistLabelPanel
        artistLabelPanel.add(genreLabel);
        artistLabelPanel.add(popularityLabel);

        //Add components on artistTextPanel
        artistTextPanel.add(genreText);
        artistTextPanel.add(popularityText);

        //Add panels on panels
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        artistPanel.add(artistLabelPanel);
        artistPanel.add(artistTextPanel);
        mainPanel.add(artistPanel, BorderLayout.CENTER);
        infoPanel.add(labelPanel);
        infoPanel.add(textPanel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    public void artistInformation(Artist artist){
        genreText.setText(artist.getGenre());
        popularityText.setText(Integer.toString(artist.getPopularity()));
    }

    public void saveButton(Performance performance){
        performance.setStage(Main.getStageArray().get(stageComboBox.getSelectedIndex()));
        performance.setStartTime(hours.get(startTimeComboBox.getSelectedIndex()));
        performance.setEndTime(hours.get(endTimeComboBox.getSelectedIndex()));
    }

    public void closeButton(){
        dispose();
    }

    public int getRightStage(Stage stage){
        int stageNumber = -1;

        for (int i = 0; i < Main.getStageArray().size(); i++) {
            if (Main.getStageArray().get(i) == stage){
                stageNumber = i;
            }
        }

        return stageNumber;
    }

    public int getRightTime(LocalTime time){
        int TimeNumber = -1;

        for (int i = 0; i < hours.size(); i++) {
            if (hours.get(i).equals(time)){
                TimeNumber = i;
            }
        }
        return TimeNumber;
    }
}
