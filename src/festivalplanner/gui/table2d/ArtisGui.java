package festivalplanner.gui.table2d;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * @author Maarten Nieuwenhuize, Zwen van Erkelens, Coen Boelhouwers
 */
public class ArtisGui extends JFrame {
    private JLabel artistLabel;
    private JComboBox stageComboBox;
    private JComboBox artistComboBox;
    private JTextField artistText;

    private JSpinner startTimeJSpinner;
    private JSpinner endTimeJSpinner;

    private List<String> stageNamesOld;
    private List<String> genreValues;
    private List<Integer> popularityValues;

    private LocalTime localTime;

    private List<String> artists;
    private int previousIndex;

    private Database database;
    private SimpleDateFormat time;

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

		JList<Artist> artistJList = setupArtistsList(database);

        Date date = new Date();
        time = new SimpleDateFormat("HH:mm");

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
            startTimeJSpinner = new JSpinner(st);
            JSpinner.DateEditor stde = new JSpinner.DateEditor(startTimeJSpinner, "HH:mm");
            startTimeJSpinner.setEditor(stde);
            try {
                startTimeJSpinner.setValue(time.parseObject(performance.getStartTime().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //End time ComboBox
            SpinnerDateModel et = new SpinnerDateModel(date, null, null, Calendar.MINUTE);
            endTimeJSpinner = new JSpinner(et);
            JSpinner.DateEditor etde = new JSpinner.DateEditor(endTimeJSpinner, "HH:mm");
            endTimeJSpinner.setEditor(etde);
            try {
                endTimeJSpinner.setValue(time.parseObject(performance.getEndTime().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

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
        //textPanel.add(stageComboBox);
        //textPanel.add(startTimeJSpinner);
        //textPanel.add(endTimeJSpinner);
        //if(performance.getArtists().size() > 1){
        //    textPanel.add(artistComboBox);
        //    genreText.setText(genreValues.get(artistComboBox.getSelectedIndex()));
        //    popularityText.setText(Integer.toString(performance.getArtists().get(artistComboBox.getSelectedIndex()).getPopularity()));
        //}
        //else {
        //    textPanel.add(artistText);
        //    genreText.setText(genreValues.get(artistComboBox.getSelectedIndex()));
        //    popularityText.setText(Integer.toString(performance.getArtists().get(0).getPopularity()));
        //}

        //Add components on artistLabelPanel
        //artistLabelPanel.add(genreLabel);
        //artistLabelPanel.add(popularityLabel);

        //Add components on artistTextPanel
        //artistTextPanel.add(genreText);
        //artistTextPanel.add(popularityText);

        // Position those widgets
        //buttonPanel.add(saveButton);
        //buttonPanel.add(closeButton);

        //artistPanel.add(artistLabelPanel);
        //artistPanel.add(artistTextPanel);
        //mainPanel.add(artistPanel, BorderLayout.CENTER);
        //infoPanel.add(labelPanel);
        //infoPanel.add(textPanel);
        //mainPanel.add(infoPanel, BorderLayout.NORTH);




		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.add(new JLabel("Stage:"), constraints(0, 0, 0.4, 0));
		centerPanel.add(stageComboBox, constraints(1, 0, 0.6, 0));
		centerPanel.add(new JLabel("Start time:"), constraints(0, 1, 0.4, 0));
		centerPanel.add(startTimeJSpinner, constraints(1, 1, 0.6, 0));
		centerPanel.add(new JLabel("End time:"), constraints(0, 2, 0.4, 0));
		centerPanel.add(endTimeJSpinner, constraints(1, 2, 0.6, 0));
		centerPanel.add(new JLabel("Artists:"), constraints(0, 3, 0.4, 0));
		centerPanel.add(new JScrollPane(artistJList), constraints(1, 3, 0.6, 60));
		centerPanel.add(new JLabel("Genres:"), constraints(0, 4, 0.4, 0));
		centerPanel.add(new DisabledTextField(performance.getArtistGenres()), constraints(1, 4, 0.6, 0));
		centerPanel.add(new JLabel("Popularity:"), constraints(0, 5, 0.4, 0));
		centerPanel.add(new DisabledTextField(String.valueOf(performance.generatePopularity())),
				constraints(1, 5, 0.6, 0));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(saveButton);
        bottomPanel.add(closeButton);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private static GridBagConstraints constraints(int x, int y, double weight, int pad) {
    	GridBagConstraints leftColumn = new GridBagConstraints();
		leftColumn.weightx = 0.4;
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
        //Save stage
        performance.setStage(database.getStages().get(stageComboBox.getSelectedIndex()));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        //Save start time
        localTime = LocalTime.parse(time.format(startTimeJSpinner.getValue()), dtf);
        performance.setStartTime(localTime);

        //Save end time
        localTime = LocalTime.parse(time.format(endTimeJSpinner.getValue()), dtf);
        performance.setEndTime(localTime);

        //Save genre & popularity
        for (int i = 0; i < performance.getArtists().size(); i++) {
            performance.getArtists().get(i).setGenre(genreValues.get(i));
            performance.getArtists().get(i).setPopularity(popularityValues.get(i));
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

	private static class DisabledTextField extends JTextField {

		public DisabledTextField(String text) {
			super(text);
			setEnabled(false);
		}
	}
}