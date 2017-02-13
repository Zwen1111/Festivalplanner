package festivalplanner.gui;

import festivalplanner.Main;
import festivalplanner.data.Database;
import festivalplanner.gui.menu.AddArtist;
import festivalplanner.gui.menu.AddStage;
import festivalplanner.util.FileSystem;
import festivalplanner.gui.table.CalendarTable;
import festivalplanner.gui.table2d.CalendarTable2D;

import javax.swing.*;
import java.awt.*;

/**
 * The main GUI handler.
 *
 * @author Coen Boelhouwers, Björn Taks
 */
public class GUIFrame extends JFrame implements Database.OnDataChangedListener{

	private Database database;
	private FileSystem fileSystem;

	@Override
	public void onDataChanged() {
		setTitle(fileSystem.getNameFile());
		repaint();
	}

	public GUIFrame() {
		database = new Database();

		fileSystem = new FileSystem(database);
		setTitle(fileSystem.getNameFile());

		Main.test(database);
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.SCROLL_TAB_LAYOUT);

		JPanel mainPanel = new JPanel(new BorderLayout());



		CalendarTable2D panel2d = new CalendarTable2D(database);

		CalendarTable panelTable = new CalendarTable(database);

		JPanel simulator = new JPanel();

		tabs.addChangeListener(e -> {
            Component p = ((JTabbedPane) e.getSource()).getSelectedComponent();
            if(p.equals(simulator)) {

                setExtendedState(JFrame.MAXIMIZED_BOTH);
                //size of simulator = 1915 ,950
                setVisible(true);
            }else {
                setResizable(true);
            }
        });

		database.addOnDataChangedListener(this);
		database.addOnDataChangedListener(panel2d);
		database.addOnDataChangedListener(panelTable);

		mainPanel.add(tabs,BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem newAgenda = new JMenuItem("New");
		newAgenda.addActionListener(e -> {
			database.notifyDataChanged();
			fileSystem.newAgenda();
			repaint();
		});
		fileMenu.add(newAgenda);

		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(e -> {
			fileSystem.open();
			repaint();
		});
		fileMenu.add(open);

		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(e -> {
			fileSystem.save();
		});
		fileMenu.add(save);

		JMenuItem saveas = new JMenuItem("Save as");
		saveas.addActionListener(e ->  fileSystem.saveAs());
		fileMenu.add(saveas);



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
		tabs.add(simulator);


		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(mainPanel);
		setSize(800, 600);
		setMinimumSize(new Dimension(600,650));
		setVisible(true);


	}
}
