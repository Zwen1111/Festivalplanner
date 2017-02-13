package festivalplanner.gui.table2d;

import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.gui.PerformanceOverview;

import java.awt.*;

/**
 * @author Maarten Nieuwenhuize, Zwen van Erkelens, Coen Boelhouwers
 */
public class ArtisGui extends PerformanceOverview {

	public ArtisGui(Performance performance, Database database, Rectangle rectangle) {
		super(database);
		setPerformance(performance);
		setLocation(rectangle.getLocation());
	}

	/*private static final double WEIGHT_LEFT = 0.1;
	private static final double WEIGHT_RIGHT = 0.9;

	private JComboBox<String> stageComboBox;
	private JList<Artist> artistJList;

	private JSpinner startTimeJSpinner;
	private JSpinner endTimeJSpinner;

	private Database database;

	public ArtisGui(Performance performance, Database database, Rectangle rectangle) {
		setSize(350, 300);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocation(rectangle.getLocation());
		this.database = database;

		artistJList = setupArtistsList(database);

		stageComboBox = new JComboBox<>();
		database.getStages().forEach(stage -> stageComboBox.addItem(stage.getName()));
		stageComboBox.setSelectedIndex(getRightStage(performance.getStage()));

		//Start time ComboBox
		startTimeJSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE));
		startTimeJSpinner.setEditor(new JSpinner.DateEditor(startTimeJSpinner, "HH:mm"));

		//End time ComboBox
		endTimeJSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE));
		endTimeJSpinner.setEditor(new JSpinner.DateEditor(endTimeJSpinner, "HH:mm"));

		// Sadly, there are few ways to convert between an old Date and new LocalTime.
		DateFormat time = new SimpleDateFormat("HH:mm");
		try {
			startTimeJSpinner.setValue(time.parse(performance.getStartTime().toString()));
			endTimeJSpinner.setValue(time.parse(performance.getEndTime().toString()));
		} catch (ParseException e) {
			System.err.println("Could not parse the time. Should not normally happen.");
		}

		//Create buttons
		JButton savePerformance = new JButton("Save");
		savePerformance.addActionListener(e -> savePerformance(performance));

		JButton closeDialog = new JButton("Close");
		closeDialog.addActionListener(e -> closeDialog());

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
		DisabledTextField genreTextField = new DisabledTextField(getGenreArtists(artistJList.getSelectedValuesList()));
		centerPanel.add(genreTextField, constraints(1, 4, WEIGHT_RIGHT, 0));
		centerPanel.add(new JLabel("Popularity:"), constraints(0, 5, WEIGHT_LEFT, 0));
		DisabledTextField popularityTextField = new DisabledTextField(String.valueOf(database.getPerformances().get(0).generatePopularity()));
		centerPanel.add(popularityTextField,
				constraints(1, 5, WEIGHT_RIGHT, 0));

		//makes sure that the genre and popularity display updates
		artistJList.addListSelectionListener(e -> {
			genreTextField.setText(getGenreArtists(artistJList.getSelectedValuesList()));
			int popularityTotal = 0;
			for (Artist artist : artistJList.getSelectedValuesList()) {
				popularityTotal += artist.getPopularity();
			}
			int popularity = popularityTotal / artistJList.getSelectedValuesList().size();
			popularityTextField.setText(Integer.toString(popularity));
		});

		JPanel bottomPanel = new JPanel();
		bottomPanel.add(savePerformance);
		bottomPanel.add(closeDialog);

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
		leftColumn.insets = new Insets(2, 2, 2, 2);
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

	public void savePerformance(Performance performance) {
		performance.setStage(database.getStages().get(stageComboBox.getSelectedIndex()));
		performance.setStartTime(LocalDateTime.ofInstant(((Date) startTimeJSpinner.getValue()).toInstant(),
				ZoneId.systemDefault()).toLocalTime());
		performance.setEndTime(LocalDateTime.ofInstant(((Date) endTimeJSpinner.getValue()).toInstant(),
				ZoneId.systemDefault()).toLocalTime());
		performance.getArtists().clear();
		performance.getArtists().addAll(artistJList.getSelectedValuesList());
		database.notifyDataChanged();
		dispose();
	}

	public void closeDialog() {
		dispose();
	}

	public String getGenreArtists(java.util.List<Artist> artists) {
		StringBuilder builder = new StringBuilder(artists.get(0).getGenre());
		for (int i = 1; i < artists.size(); i++)
			builder.append(", ").append(artists.get(i).getGenre());
		return builder.toString();
	}

	public int getRightStage(Stage stage) {
		int stageNumber = -1;

		for (int i = 0; i < database.getStages().size(); i++) {
			if (database.getStages().get(i).equals(stage)) {
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
	}*/
}