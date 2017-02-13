package festivalplanner.gui;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.gui.addMenu.AddArtist;
import festivalplanner.gui.addMenu.AddStage;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Coen Boelhouwers
 */
public class PerformanceOverview extends JFrame implements Database.OnDataChangedListener {

	private static final double WEIGHT_COLUMN_1 = 0.1;
	private static final double WEIGHT_COLUMN_2 = 0.8;
	private static final double WEIGHT_COLUMN_3 = 0.01;

	private JComboBox<Object> stageComboBox;
	private JList<Artist> artistJList;
	private JSpinner startTimeJSpinner;
	private JSpinner endTimeJSpinner;
	private DisabledTextField genreTextField;
	private DisabledTextField popularityTextField;
	private JButton saveButton;
	private JButton closeButton;
	private JButton newStageButton;
	private JButton newArtistButton;

	private Database database;
	private Performance shownPerformance;

	public PerformanceOverview(Database database) {
		setSize(350, 300);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.database = database;

		artistJList = setupArtistsList(database);

		newArtistButton = new JButton("New");
		newArtistButton.addActionListener(l -> new AddArtist(database));

		newStageButton = new JButton("New");
		newStageButton.addActionListener(l -> new AddStage(database));

		stageComboBox = new JComboBox<>(database.getStages().toArray());

		//Start time ComboBox
		startTimeJSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE));
		startTimeJSpinner.setEditor(new JSpinner.DateEditor(startTimeJSpinner, "HH:mm"));
		startTimeJSpinner.setValue(new Date());
		startTimeJSpinner.addChangeListener(l -> updateFields());

		//End time ComboBox
		endTimeJSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE));
		endTimeJSpinner.setEditor(new JSpinner.DateEditor(endTimeJSpinner, "HH:mm"));
		endTimeJSpinner.setValue(new Date());
		endTimeJSpinner.addChangeListener(l -> updateFields());

		genreTextField = new DisabledTextField("");
		popularityTextField = new DisabledTextField("");

		//Create buttons
		saveButton = new JButton("Save");
		saveButton.addActionListener(e -> savePerformance(shownPerformance));

		closeButton = new JButton("Close");
		closeButton.addActionListener(e -> closeDialog());

		// Position those widgets
		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.add(new JLabel("Stage:"), constraints(0, 0, WEIGHT_COLUMN_1, 0));
		centerPanel.add(stageComboBox, constraints(1, 0, WEIGHT_COLUMN_2, 0));
		centerPanel.add(newStageButton, constraints(2, 0, WEIGHT_COLUMN_3, 0));
		centerPanel.add(new JLabel("Start time:"), constraints(0, 1, WEIGHT_COLUMN_1, 0));
		centerPanel.add(startTimeJSpinner, constraints(1, 1, WEIGHT_COLUMN_2, 0));
		centerPanel.add(new JLabel("End time:"), constraints(0, 2, WEIGHT_COLUMN_1, 0));
		centerPanel.add(endTimeJSpinner, constraints(1, 2, WEIGHT_COLUMN_2, 0));
		centerPanel.add(new JLabel("Artists:"), constraints(0, 3, WEIGHT_COLUMN_1, 0));
		centerPanel.add(new JScrollPane(artistJList), constraints(1, 3, WEIGHT_COLUMN_2, 60));
		centerPanel.add(newArtistButton, constraints(2, 3, WEIGHT_COLUMN_3, 0));
		centerPanel.add(new JLabel("Genres:"), constraints(0, 4, WEIGHT_COLUMN_1, 0));
		centerPanel.add(genreTextField, constraints(1, 4, WEIGHT_COLUMN_2, 0));
		centerPanel.add(new JLabel("Popularity:"), constraints(0, 5, WEIGHT_COLUMN_1, 0));
		centerPanel.add(popularityTextField,
				constraints(1, 5, WEIGHT_COLUMN_2, 0));

		// Make sure that the genre and popularity are constant updated as data changes (internal and external).
		artistJList.addListSelectionListener(e -> updateFields());
		database.addOnDataChangedListener(this);

		// Initialize fields.
		updateFields();

		JPanel bottomPanel = new JPanel();
		bottomPanel.add(saveButton);
		bottomPanel.add(closeButton);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);
		setVisible(true);
	}

	public PerformanceOverview setPerformance(Performance performance) {
		shownPerformance = performance;
		if (performance != null) {
			// Select the current performing artist(s) in the list.
			java.util.List<Artist> artists = database.getArtists();
			artistJList.removeSelectionInterval(0, artists.size() - 1);
			performance.getArtists().forEach(artist -> {
				int index = database.getArtists().indexOf(artist);
				artistJList.addSelectionInterval(index, index);
			});

			stageComboBox.setSelectedIndex(database.getStages().indexOf(performance.getStage()));

			// Sadly, converting between an old Date and new the LocalTime is hard.
			DateFormat time = new SimpleDateFormat("HH:mm");
			try {
				startTimeJSpinner.setValue(time.parse(performance.getStartTime().toString()));
				endTimeJSpinner.setValue(time.parse(performance.getEndTime().toString()));
			} catch (ParseException e) {
				System.err.println("Could not parse the time. Should not normally happen.");
			}
		}
		return this;
	}

	private void updateFields() {
		genreTextField.setText(Performance.getArtistGenres(artistJList.getSelectedValuesList()));
		popularityTextField.setText(String.format("%.1f", Performance.generatePopularity(
				artistJList.getSelectedValuesList())));
		Date date1 = ((Date) startTimeJSpinner.getValue());
		Date date2 = ((Date) endTimeJSpinner.getValue());
		saveButton.setEnabled(
				artistJList.getSelectedValuesList().size() >= 1 &&
				stageComboBox.getSelectedIndex() >= 0 && ((Date) endTimeJSpinner.getValue()).getTime() >
						((Date) startTimeJSpinner.getValue()).getTime());
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
		/*ListModel<Artist> model = new DefaultListModel<Artist>() {
			@Override
			public int getSize() {
				return database.getArtists().size();
			}

			@Override
			public Artist getElementAt(int index) {
				return database.getArtists().get(index);
			}
		};*/
		DefaultListModel<Artist> model = new DefaultListModel<>();
		database.getArtists().forEach(model::addElement);
		list.setModel(model);
		return list;
	}

	public void savePerformance(Performance performance) {
		if (performance == null) {
			Performance newPerformance = new Performance(
					database.getStages().get(stageComboBox.getSelectedIndex()),
					LocalDateTime.ofInstant(((Date) startTimeJSpinner.getValue()).toInstant(),
							ZoneId.systemDefault()).toLocalTime(),
					LocalDateTime.ofInstant(((Date) endTimeJSpinner.getValue()).toInstant(),
							ZoneId.systemDefault()).toLocalTime(),
					artistJList.getSelectedValuesList());
			database.addPerformance(newPerformance);
		} else {
			performance.setStage(database.getStages().get(stageComboBox.getSelectedIndex()));
			performance.setStartTime(LocalDateTime.ofInstant(((Date) startTimeJSpinner.getValue()).toInstant(),
					ZoneId.systemDefault()).toLocalTime());
			performance.setEndTime(LocalDateTime.ofInstant(((Date) endTimeJSpinner.getValue()).toInstant(),
					ZoneId.systemDefault()).toLocalTime());
			performance.getArtists().clear();
			performance.getArtists().addAll(artistJList.getSelectedValuesList());
			database.notifyDataChanged();
		}
		closeDialog();
	}

	public void closeDialog(){
		database.removeOnDataChangedListener(this);
		dispose();
	}

	@Override
	public void onDataChanged() {
		stageComboBox.removeAllItems();
		database.getStages().forEach(stageComboBox::addItem);
		DefaultListModel<Artist> model = (DefaultListModel<Artist>) artistJList.getModel();
		model.clear();
		database.getArtists().forEach(model::addElement);
		updateFields();
	}

	private static class DisabledTextField extends JTextField {

		public DisabledTextField(String text) {
			super(text);
			setEnabled(false);
		}
	}
}
