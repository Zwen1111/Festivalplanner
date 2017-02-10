package festivalplanner.gui;

import festivalplanner.Main;
import festivalplanner.data.Database;
import festivalplanner.gui.addMenu.AddArtist;
import festivalplanner.gui.addMenu.AddStage;
import festivalplanner.gui.guiUtil.FileSystem;
import festivalplanner.gui.table.CalendarTable;
import festivalplanner.gui.table2d.CalendarTable2D;

import javax.swing.*;
import java.awt.*;

/**
 * The main GUI handler.
 *
 * @author Coen Boelhouwers, Björn Taks
 */
public class GUIFrame extends JFrame {

	private Database database;
	private FileSystem fileSystem;

	public GUIFrame() {
		database = new Database();
		fileSystem = new FileSystem(database);

		Main.test(database);
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.SCROLL_TAB_LAYOUT);

		JPanel mainPanel = new JPanel(new BorderLayout());



		CalendarTable2D panel2d = new CalendarTable2D(database);

		CalendarTable panelTable = new CalendarTable(database);


		mainPanel.add(tabs,BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(e -> {
			fileSystem.save();
		});
		fileMenu.add(save);

		JMenuItem saveas = new JMenuItem("Save as");
		saveas.addActionListener(e ->  fileSystem.saveAs());
		fileMenu.add(saveas);

		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(e -> {
			 fileSystem.open();
			 repaint();
		});
		fileMenu.add(open);

		JMenu addMenu = new JMenu("Add");
		menuBar.add(addMenu);

		JMenuItem stageMenuItem = new JMenuItem("Stage");
		stageMenuItem.addActionListener(e -> {
			new AddStage(database);
		});
		JMenuItem artistMenuItem = new JMenuItem("Artist");
		artistMenuItem.addActionListener(e -> {
			new AddArtist(database);
		});
		addMenu.add(stageMenuItem);
		addMenu.add(artistMenuItem);


		mainPanel.add(menuBar,BorderLayout.NORTH);

		tabs.add(panel2d);
		tabs.add(panelTable);


		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(mainPanel);
		setSize(800, 600);
		setVisible(true);
	}
}
