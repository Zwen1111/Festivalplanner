package festivalplanner.gui;

import festivalplanner.Main;
import festivalplanner.data.Database;
import festivalplanner.gui.guiUtil.DatabaseUpdateAble;
import festivalplanner.gui.guiUtil.FileSystem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * The main GUI handler.
 *
 * @author Coen Boelhouwers, Björn Taks
 */
public class GUIFrame extends JFrame {

	private Database database;
	private FileSystem fileSystem;
	private ArrayList<DatabaseUpdateAble> databaseUpdateAbles;

	public GUIFrame() {
		database = new Database();
		databaseUpdateAbles = new ArrayList<>();

		Main.test(database);

		fileSystem = new FileSystem(database);

		Java2DPanel panel2d = new Java2DPanel(database);

		GUI_Table panelTable = new GUI_Table(database);

		databaseUpdateAbles.add(panel2d);
		databaseUpdateAbles.add(panelTable);

		JPanel mainPanel = new JPanel(new BorderLayout());
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem opslaan = new JMenuItem("Save as");
		opslaan.addActionListener(e -> {
			fileSystem.saveAs();
		});
		menuBar.add(file);
		JMenuItem openen = new JMenuItem("Open...");
		openen.addActionListener(e -> {
			Database newDatabase = fileSystem.open();
			this.database = newDatabase;
			for (DatabaseUpdateAble databaseUpdateAble : databaseUpdateAbles) {
				databaseUpdateAble.updateDatabase(newDatabase);
			}
			panel2d.repaint();
		});
		file.add(opslaan);
		file.add(openen);


		JTabbedPane tabs = new JTabbedPane(JTabbedPane.SCROLL_TAB_LAYOUT);




		tabs.add(panel2d);
		tabs.add(panelTable);
		setMinimumSize(new Dimension(600, 600));


		mainPanel.add(tabs, BorderLayout.CENTER);
		mainPanel.add(menuBar, BorderLayout.NORTH);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(mainPanel);
		setSize(800, 600);
		setVisible(true);
	}

}

