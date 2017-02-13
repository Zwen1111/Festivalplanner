package festivalplanner.gui;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.gui.menu.AddArtist;
import festivalplanner.gui.menu.AddStage;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * @author Coen Boelhouwers
 */
public class PerformanceOverview extends JFrame implements Database.OnDataChangedListener {

	private static final double WEIGHT_COLUMN_1 = 0.1;
	private static final double WEIGHT_COLUMN_2 = 0.8;
	private static final double WEIGHT_COLUMN_3 = 0.01;

	private JComboBox<Object> stageComboBox;
	private JList<Artist> artistJList;
	private JTextField startTimeField;
	private JTextField endTimeField;
	private DisabledTextField genreTextField;
	private DisabledTextField popularityTextField;
	private JButton saveButton;

	private LocalTime startTime;
	private LocalTime endTime;
	private boolean startTimeValid;
	private boolean endTimeValid;

	private Database database;
	private Performance shownPerformance;

	public PerformanceOverview(Database database) {
		setSize(350, 300);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.database = database;
		this.startTime = LocalTime.now();
		this.endTime = LocalTime.now();

		artistJList = setupArtistsList(database);

		JButton newArtistButton = new JButton("New");
		newArtistButton.addActionListener(l -> new AddArtist(database));

		JButton newStageButton = new JButton("New");
		newStageButton.addActionListener(l -> new AddStage(database));

		stageComboBox = new JComboBox<>(database.getStages().toArray());

		startTimeField = new JTextField();
		startTimeField.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				try {
					startTime = LocalTime.parse(((JTextField) input).getText());
					input.setForeground(Color.black);
					startTimeValid = true;
				} catch (DateTimeParseException e) {
					input.setForeground(Color.red);
					startTimeValid = false;
				}
				updateFields();
				return startTimeValid;
			}
		});
		startTimeField.setText(startTime.truncatedTo(ChronoUnit.MINUTES).toString());

		endTimeField = new JTextField();
		endTimeField.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				try {
					endTime = LocalTime.parse(((JTextField) input).getText());
					input.setForeground(Color.black);
					endTimeValid = true;
				} catch (DateTimeParseException e) {
					input.setForeground(Color.red);
					endTimeValid = false;
				}
				updateFields();
				return endTimeValid;
			}
		});
		endTimeField.setText(endTime.truncatedTo(ChronoUnit.MINUTES).plusHours(1).toString());

		genreTextField = new DisabledTextField("");
		popularityTextField = new DisabledTextField("");

		//Create buttons
		saveButton = new JButton("Save");
		saveButton.addActionListener(e -> savePerformance(shownPerformance));

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> closeDialog());

		// Position those widgets
		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.add(new JLabel("Stage:"), constraints(0, 0, WEIGHT_COLUMN_1, 0));
		centerPanel.add(stageComboBox, constraints(1, 0, WEIGHT_COLUMN_2, 0));
		centerPanel.add(newStageButton, constraints(2, 0, WEIGHT_COLUMN_3, 0));
		centerPanel.add(new JLabel("Start time:"), constraints(0, 1, WEIGHT_COLUMN_1, 0));
		centerPanel.add(startTimeField, constraints(1, 1, WEIGHT_COLUMN_2, 0));
		centerPanel.add(new JLabel("End time:"), constraints(0, 2, WEIGHT_COLUMN_1, 0));
		centerPanel.add(endTimeField, constraints(1, 2, WEIGHT_COLUMN_2, 0));
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

			startTime = performance.getStartTime();
			endTime = performance.getEndTime();
			startTimeField.setText(startTime.toString());
			endTimeField.setText(endTime.toString());
			startTimeValid = true;
			endTimeValid = true;
		}
		updateFields();
		return this;
	}

	private void updateFields() {
		genreTextField.setText(Performance.getArtistGenres(artistJList.getSelectedValuesList()));
		popularityTextField.setText(String.format("%.1f", Performance.generatePopularity(
				artistJList.getSelectedValuesList())));
		saveButton.setEnabled(artistJList.getSelectedValuesList().size() >= 1 &&
				startTimeValid && endTimeValid &&
				stageComboBox.getSelectedIndex() >= 0 && endTime.isAfter(startTime));
		System.out.println("update. artists:" + (artistJList.getSelectedValuesList().size() >= 1) +
				", startValid:" + startTimeValid + ", endValid:" + endTimeValid +
				", stage:" + (stageComboBox.getSelectedIndex() >= 0) + ", after:" + endTime.isAfter(startTime));
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
		DefaultListModel<Artist> model = new DefaultListModel<>();
		database.getArtists().forEach(model::addElement);
		list.setModel(model);
		return list;
	}

	public void savePerformance(Performance performance) {
		if (performance == null) {
			Performance newPerformance = new Performance(
					database.getStages().get(stageComboBox.getSelectedIndex()),
					startTime, endTime,
					artistJList.getSelectedValuesList());
			database.addPerformance(newPerformance);
		} else {
			performance.setStage(database.getStages().get(stageComboBox.getSelectedIndex()));
			performance.setStartTime(startTime);
			performance.setEndTime(endTime);
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
