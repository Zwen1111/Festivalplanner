package festivalplanner.gui;

import festivalplanner.Main;
import festivalplanner.data.Database;

import javax.swing.*;

/**
 * The main GUI handler.
 *
 * @author Coen Boelhouwers, Björn Taks
 */
public class GUIFrame extends JFrame {

	private Database database;

	public GUIFrame() {
		database = new Database();

		Main.test(database);
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.SCROLL_TAB_LAYOUT);

		Java2DPanel panel2d = new Java2DPanel(database);

		GUI_Table panelTable = new GUI_Table(database);

		tabs.add("Panel 2D", panel2d);
		tabs.add(panelTable);




		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Could not find cross-platform UI.");
			e.printStackTrace();
		}
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(tabs);
		setSize(800, 600);
		setVisible(true);
	}
}
