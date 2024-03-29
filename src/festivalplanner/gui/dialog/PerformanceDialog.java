package festivalplanner.gui.dialog;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author Coen Boelhouwers
 */
public class PerformanceDialog extends JDialog implements Database.OnDataChangedListener {

	private static final double WEIGHT_COLUMN_1 = 0.1;
	private static final double WEIGHT_COLUMN_2 = 0.8;
	private static final double WEIGHT_COLUMN_3 = 0.01;

	private JComboBox<Stage> stageComboBox;
	private JList<Artist> artistJList;
	private JTextField startTimeField;
	private JTextField endTimeField;
	private DisabledTextField genreTextField;
	private DisabledTextField popularityTextField;
	private JLabel errorLabel;
	private JButton saveButton;

	private LocalTime startTime;
	private LocalTime endTime;
	private boolean startTimeValid;
	private boolean endTimeValid;

	private Performance shownPerformance;

	/**
	 * The PerformanceDialog is a detail-pop-up of a single performance.
	 * <p/>
	 * The pop-up can be initialized w/o a performance. In the later case, a new performance
	 * will be created and added to the Database once the user hits confirm.
	 *
	 * @param performance the performance to show, or null if a new one should be created.
	 */
	public PerformanceDialog(Performance performance, Point point) {
		super(null, ModalityType.APPLICATION_MODAL);
		
		this.startTime = LocalTime.now();
		this.endTime = LocalTime.now();

		artistJList = setupArtistsList();

		JButton newArtistButton = new JButton("New");
		newArtistButton.addActionListener(l -> new AddArtistDialog());

		JButton newStageButton = new JButton("New");
		newStageButton.addActionListener(l -> new AddStageDialog());

		stageComboBox = new JComboBox<>();//Database.getStages().toArray());
		Database.getStages().forEach(stageComboBox::addItem);
		stageComboBox.addItemListener(l -> updateFields());

		startTimeField = new JTextField();
		startTimeField.getDocument().addDocumentListener((SimpleDocumentListener)() -> {
			try {
				startTime = LocalTime.parse(startTimeField.getText());
				startTimeField.setForeground(Color.black);
				startTimeValid = true;
			} catch (DateTimeParseException e) {
				startTimeField.setForeground(Color.red);
				startTimeValid = false;
			}
			updateFields();
		});

		endTimeField = new JTextField();
		endTimeField.getDocument().addDocumentListener((SimpleDocumentListener) () -> {
			try {
				endTime = LocalTime.parse(endTimeField.getText());
				endTimeField.setForeground(Color.black);
				endTimeValid = true;
			} catch (DateTimeParseException e) {
				endTimeField.setForeground(Color.red);
				endTimeValid = false;
			}
			updateFields();
		});

		genreTextField = new DisabledTextField("");
		popularityTextField = new DisabledTextField("");

		errorLabel = new JLabel(" ");
		errorLabel.setForeground(Color.red);

		//Create buttons
		saveButton = new JButton("Save");
		saveButton.addActionListener(e -> savePerformance(shownPerformance));

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> closeDialog());

		// Position those widgets
		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.add(new JLabel("Stage:"), constraints(0, 0, WEIGHT_COLUMN_1));
		centerPanel.add(stageComboBox, constraints(1, 0, WEIGHT_COLUMN_2));
		centerPanel.add(newStageButton, constraints(2, 0, WEIGHT_COLUMN_3));
		centerPanel.add(new JLabel("Start time:"), constraints(0, 1, WEIGHT_COLUMN_1));
		centerPanel.add(startTimeField, constraints(1, 1, WEIGHT_COLUMN_2));
		centerPanel.add(new JLabel("End time:"), constraints(0, 2, WEIGHT_COLUMN_1));
		centerPanel.add(endTimeField, constraints(1, 2, WEIGHT_COLUMN_2));
		centerPanel.add(new JLabel("Artists:"), constraints(0, 3, WEIGHT_COLUMN_1));
		centerPanel.add(new JScrollPane(artistJList), constraints(1, 3, WEIGHT_COLUMN_2, 60));
		centerPanel.add(newArtistButton, constraints(2, 3, WEIGHT_COLUMN_3));
		centerPanel.add(new JLabel("Genres:"), constraints(0, 4, WEIGHT_COLUMN_1));
		centerPanel.add(genreTextField, constraints(1, 4, WEIGHT_COLUMN_2));
		centerPanel.add(new JLabel("Popularity:"), constraints(0, 5, WEIGHT_COLUMN_1));
		centerPanel.add(popularityTextField,
				constraints(1, 5, WEIGHT_COLUMN_2));
		centerPanel.add(errorLabel, constraints(0, 6, WEIGHT_COLUMN_1, 0, 3));

		// Make sure that the genre and popularity are constant updated as data changes (internal and external).
		artistJList.addListSelectionListener(e -> updateFields());
		Database.addOnDataChangedListener(this);

		// Initialize fields.
		updateFields();

		JPanel bottomPanel = new JPanel();
		bottomPanel.add(saveButton);
		bottomPanel.add(closeButton);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);
		setSize(350, 300);
		setPerformance(performance);
		if (point != null) setLocation(point);
		setVisible(true);
	}

	private static GridBagConstraints constraints(int x, int y, double weight) {
		return constraints(x, y, weight, 0, 1);
	}

	private static GridBagConstraints constraints(int x, int y, double weight, int pad) {
		return constraints(x, y, weight, pad, 1);
	}

	private static GridBagConstraints constraints(int x, int y, double weight, int pad, int vSpan) {
		GridBagConstraints leftColumn = new GridBagConstraints();
		leftColumn.weightx = weight;
		leftColumn.gridx = x;
		leftColumn.gridy = y;
		leftColumn.ipady = pad;
		leftColumn.fill = GridBagConstraints.HORIZONTAL;
		leftColumn.gridwidth = vSpan;
		leftColumn.insets = new Insets(2,2,2,2);
		return leftColumn;
	}

	public PerformanceDialog setPerformance(Performance performance) {
		shownPerformance = performance;
		if (performance != null) {
			// Select the current performing artist(s) in the list.
			List<Artist> artists = Database.getArtists();
			artistJList.removeSelectionInterval(0, artists.size() - 1);
			performance.getArtists().forEach(artist -> {
				int index = Database.getArtists().indexOf(artist);
				artistJList.addSelectionInterval(index, index);
			});

			stageComboBox.setSelectedIndex(Database.getStages().indexOf(performance.getStage()));

			startTime = performance.getStartTime();
			endTime = performance.getEndTime();
			startTimeField.setText(startTime.toString());
			endTimeField.setText(endTime.toString());
			startTimeValid = true;
			endTimeValid = true;
		} else {
			startTimeField.setText(startTime.truncatedTo(ChronoUnit.MINUTES).toString());
			endTimeField.setText(endTime.truncatedTo(ChronoUnit.MINUTES).plusHours(1).toString());
		}
		updateFields();
		return this;
	}

	private void updateFields() {
		genreTextField.setText(Performance.getArtistGenres(artistJList.getSelectedValuesList()));
		popularityTextField.setText(String.format("%.1f", Performance.generatePopularity(
				artistJList.getSelectedValuesList())));
		StringBuilder builder = new StringBuilder();
		if (artistJList.getSelectedValuesList().size() == 0) disableSave(builder, "No artist selected");
		if (!(startTimeValid && endTimeValid)) disableSave(builder, "Times not valid");
		if (stageComboBox.getSelectedIndex() < 0) disableSave(builder, "No stage selected");
		if (endTime.isBefore(startTime)) disableSave(builder, "End time is before start time");
		if (Database.isStageInUse((Stage) stageComboBox.getSelectedItem(), startTime, endTime, shownPerformance))
			disableSave(builder, "Stage already in use at this time");
		errorLabel.setText(builder.toString());
		if (builder.length() == 0) saveButton.setEnabled(true);
	}

	private void disableSave(StringBuilder builder, String message) {
		if (builder.length() != 0) builder.append("; ");
		builder.append(message);
		saveButton.setEnabled(false);
	}

	private JList<Artist> setupArtistsList() {
		JList<Artist> list = new JList<>();
		DefaultListModel<Artist> model = new DefaultListModel<>();
		Database.getArtists().forEach(model::addElement);
		list.setModel(model);
		return list;
	}

	public void savePerformance(Performance performance) {
		Database.removeOnDataChangedListener(this);
		if (performance == null) {
			Performance newPerformance = new Performance(
					Database.getStages().get(stageComboBox.getSelectedIndex()),
					startTime, endTime,
					artistJList.getSelectedValuesList());
			Database.addPerformance(newPerformance);
		} else {
			performance.setStage(Database.getStages().get(stageComboBox.getSelectedIndex()));
			performance.setStartTime(startTime);
			performance.setEndTime(endTime);
			performance.getArtists().clear();
			performance.getArtists().addAll(artistJList.getSelectedValuesList());
			Database.notifyDataChanged();
		}
		closeDialog();
	}

	public void closeDialog(){
		Database.removeOnDataChangedListener(this);
		dispose();
	}

	@Override
	public void onDataChanged() {
		//Rebuild stage list:
		Stage previousSelectedStage = (Stage) stageComboBox.getSelectedItem();
		stageComboBox.removeAllItems();
		Database.getStages().forEach(stageComboBox::addItem);
		stageComboBox.setSelectedIndex(Database.getStages().indexOf(previousSelectedStage));

		//Rebuild artist list:
		List<Artist> previousSelectedArtists = artistJList.getSelectedValuesList();
		DefaultListModel<Artist> model = (DefaultListModel<Artist>) artistJList.getModel();
		model.clear();
		Database.getArtists().forEach(model::addElement);
		previousSelectedArtists.forEach(artist -> {
			int index = Database.getArtists().indexOf(artist);
			artistJList.addSelectionInterval(index, index);
		});
		updateFields();
	}

	private static class DisabledTextField extends JTextField {

		public DisabledTextField(String text) {
			super(text);
			setEnabled(false);
		}
	}

	@FunctionalInterface
	private interface SimpleDocumentListener extends DocumentListener {

		void update();
		@Override
		default void insertUpdate(DocumentEvent e) {
			update();
		}

		@Override
		default void removeUpdate(DocumentEvent e) {
			update();
		}

		@Override
		default void changedUpdate(DocumentEvent e) {
			update();
		}
	}
}
