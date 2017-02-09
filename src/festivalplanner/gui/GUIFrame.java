package festivalplanner.gui;

import festivalplanner.Main;
import festivalplanner.data.Database;
import festivalplanner.gui.table.CalendarTable;
import festivalplanner.gui.table2d.CalendarTable2D;

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

		CalendarTable2D panel2d = new CalendarTable2D(database);

		CalendarTable panelTable = new CalendarTable(database);

		tabs.add(panel2d);
		tabs.add(panelTable);





		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(tabs);
		setSize(800, 600);
		setVisible(true);
	}
}
