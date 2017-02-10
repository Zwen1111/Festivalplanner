package festivalplanner.gui.table2d;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Maarten Nieuwenhuize, Zwen van Erkelens, Coen Boelhouwers
 */
public class ArtisGui extends JFrame {
    private JLabel artistLabel;
    private JComboBox stageComboBox;
    private JComboBox artistComboBox;
    private JTextField artistText;

    private JSpinner startTimeComboBox;
    private JSpinner endTimeComboBox;

    private List<String> stageNamesOld;
    private List<LocalTime> hours;
    private List<String> hoursString;
    private List<String> genreValues;
    private List<Integer> popularityValues;

    private List<String> artists;
    private int i = 0;
    private int previousIndex;

    private Database database;

    private JTextField genreText = new JTextField("");
    private JTextField popularityText = new JTextField("");

    public ArtisGui(Performance performance, Database database) {
        setSize(220,250);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.database = database;


        //JPanel mainPanel = new JPanel(new BorderLayout());
        //JPanel infoPanel = new JPanel();
        //JPanel labelPanel = new JPanel(new GridLayout(4,1));
        //JPanel textPanel = new JPanel(new GridLayout(4,1));
        JPanel artistPanel = new JPanel(new FlowLayout());
        JPanel artistLabelPanel = new JPanel(new GridLayout(2,1));
        JPanel artistTextPanel = new JPanel(new GridLayout(2,1));
        //JPanel buttonPanel = new JPanel(new FlowLayout());



        Date date = new Date();

        genreValues = new ArrayList<>();
        popularityValues = new ArrayList<>();
        previousIndex = -1;

        //Create labels

        //JLabel startTimeLabel = new JLabel("Start time:");
        //JLabel endTimeLabel = new JLabel("End time:");
        //JLabel genreLabel = new JLabel("Genre:");
        //JLabel popularityLabel = new JLabel("Popularity:");

        //Create combobox and the list

            //Stage ComboBox
            stageNamesOld = new ArrayList<>();
            for (Stage stage : database.getStages()) {
                stageNamesOld.add(stage.getName());
            }
            String[] stageNames = new String[stageNamesOld.size()];
            stageNames = stageNamesOld.toArray(stageNames);
            stageComboBox = new JComboBox(stageNames);
            stageComboBox.setSelectedIndex(getRightStage(performance.getStage()));

            //Start time ComboBox
            SpinnerDateModel st = new SpinnerDateModel(date, null, null, Calendar.MINUTE);
            startTimeComboBox = new JSpinner(st);
            JSpinner.DateEditor stde = new JSpinner.DateEditor(startTimeComboBox, "HH:mm");
            startTimeComboBox.setEditor(stde);
            SimpleDateFormat time = new SimpleDateFormat("HH:mm");
            try {
                startTimeComboBox.setValue(time.parseObject(performance.getStartTime().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
                ((JSpinner.DateEditor) startTimeComboBox.getEditor()).getTextField().setForeground(Color.RED);
            }

        //End time ComboBox
            SpinnerDateModel et = new SpinnerDateModel(date, null, null, Calendar.MINUTE);
            endTimeComboBox = new JSpinner(et);
            JSpinner.DateEditor etde = new JSpinner.DateEditor(endTimeComboBox, "HH:mm");
            endTimeComboBox.setEditor(etde);

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
                for (Artist artist : performance.getArtists()) {
                    genreValues.add(artist.getGenre());
                    popularityValues.add(artist.getPopularity());
                }
                artistComboBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if(e.getStateChange() == ItemEvent.DESELECTED){
                            for (int i = 0; i < artists.size(); i++) {
                                if(artists.get(i).equals(e.getItem())){
                                    previousIndex = i;
                                }
                            }
                        }
                    }
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
        //labelPanel.add(stageLabel);
        //labelPanel.add(startTimeLabel);
        //labelPanel.add(endTimeLabel);
        //labelPanel.add(artistLabel);

        //Add components on textPanel
        textPanel.add(stageComboBox);
        textPanel.add(startTimeComboBox);
        textPanel.add(endTimeComboBox);
        //if(performance.getArtists().size() > 1){
            textPanel.add(artistComboBox);
            genreText.setText(genreValues.get(artistComboBox.getSelectedIndex()));
            popularityText.setText(Integer.toString(performance.getArtists().get(artistComboBox.getSelectedIndex()).getPopularity()));
        //}
        //else {
        //    textPanel.add(artistText);
        //    genreText.setText(genreValues.get(artistComboBox.getSelectedIndex()));
        //    popularityText.setText(Integer.toString(performance.getArtists().get(0).getPopularity()));
        //}

        //Add components on artistLabelPanel
        artistLabelPanel.add(genreLabel);
        artistLabelPanel.add(popularityLabel);

        //Add components on artistTextPanel
        artistTextPanel.add(genreText);
        artistTextPanel.add(popularityText);

        // Position those widgets
        //buttonPanel.add(saveButton);
        //buttonPanel.add(closeButton);

        artistPanel.add(artistLabelPanel);
        artistPanel.add(artistTextPanel);
        mainPanel.add(artistPanel, BorderLayout.CENTER);
        infoPanel.add(labelPanel);
        infoPanel.add(textPanel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);



		JPanel centerPanel = new JPanel(new GridLayout(6, 2));
		centerPanel.add(new JLabel("Stage:"));
		centerPanel.add(stageComboBox);
		centerPanel.add(new JLabel("Start time:"));
		centerPanel.add(startTimeComboBox);
		centerPanel.add(new JLabel("End time:"));
		centerPanel.add(endTimeComboBox);
		centerPanel.add(new JLabel("Artists:"));
		centerPanel.add(new JTextField(performance.getArtistNames()));
		centerPanel.add(new JLabel("Genres:"));
		centerPanel.add(new DisabledTextField(performance.getArtistGenres()));
		centerPanel.add(new JLabel("Popularity:"));
		centerPanel.add(new DisabledTextField(String.valueOf(performance.generatePopularity())));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(saveButton);
        bottomPanel.add(closeButton);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    public void artistInformation(Artist artist){
        //Saves the values in genreText and popularityText
        if(genreValues.get(previousIndex).equals(genreText.getText())){}else{
            genreValues.set(previousIndex, genreText.getText());
        }
        if(popularityValues.get(previousIndex).equals(popularityText.getText())){}else{
            popularityValues.set(previousIndex, Integer.parseInt(popularityText.getText()));
        }

        genreText.setText(genreValues.get(artistComboBox.getSelectedIndex()));
        popularityText.setText(Integer.toString(popularityValues.get(artistComboBox.getSelectedIndex())));
    }

    public void saveButton(Performance performance){
        performance.setStage(database.getStages().get(stageComboBox.getSelectedIndex()));

        for (int i = 0; i < performance.getArtists().size(); i++) {
            performance.getArtists().get(i).setGenre(genreValues.get(i));
        }
        dispose();
    }

    public void closeButton(){
        dispose();
    }

    public int getRightStage(Stage stage){
        int stageNumber = -1;

        for (int i = 0; i < database.getStages().size(); i++) {
            if (database.getStages().get(i) == stage){
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

    private static class DisabledTextField extends JTextField {

    	public DisabledTextField(String text) {
    		super(text);
    		setEnabled(false);
		}
	}
}