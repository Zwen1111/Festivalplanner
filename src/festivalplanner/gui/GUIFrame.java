package festivalplanner.gui;

import festivalplanner.data.Database;
import festivalplanner.gui.simulator.SimulatorTabGUI;
import festivalplanner.gui.table.CalendarTable;
import festivalplanner.gui.table2d.CalendarTable2D;
import festivalplanner.simulator.Simulator;
import festivalplanner.util.FileSystem;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;

/**
 * The main GUI handler.
 *
 * @author Coen Boelhouwers, Björn Taks
 */
public class GUIFrame extends JFrame implements Database.OnDataChangedListener{

	public static final String APP_NAME = "Festivalplanner";
	public static final String APP_VERSION = "0.1";

	private FileSystem fileSystem;

	public GUIFrame() {
		setTitle(APP_NAME + " v" + APP_VERSION);

		//Main.test(Database);
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.SCROLL_TAB_LAYOUT);

		JPanel mainPanel = new JPanel(new BorderLayout());

		CalendarTable2D panel2d = new CalendarTable2D();
		CalendarTable panelTable = new CalendarTable();
		JPanel simulator = new SimulatorTabGUI();

		fileSystem = new FileSystem();

		tabs.addChangeListener(e -> {
            Component p = ((JTabbedPane) e.getSource()).getSelectedComponent();
            if (p instanceof SimulatorTabGUI) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });

		Database.addOnDataChangedListener(this);
		Database.addOnDataChangedListener(panel2d);
		Database.addOnDataChangedListener(panelTable);

		mainPanel.add(tabs,BorderLayout.CENTER);

		//Create the File menu
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem newAgenda = new JMenuItem("New");
		newAgenda.addActionListener(e -> fileSystem.newCalendar());
		fileMenu.add(newAgenda);

		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(e -> fileSystem.open());
		fileMenu.add(open);

		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(e -> fileSystem.save());
		fileMenu.add(save);

		JMenuItem saveas = new JMenuItem("Save as");
		saveas.addActionListener(e ->  fileSystem.saveAs());
		fileMenu.add(saveas);

		JMenu options = new JMenu("Options");
		menuBar.add(options);

		JMenuItem visitorsAmount = new JMenuItem("Visitors Amount");
		visitorsAmount.addActionListener(e ->{
			try {
				Simulator.setMaxVisitors(Integer.parseInt(JOptionPane.showInputDialog(null,
						"The maximum amount of visitors the simulator should simulate.",
						Simulator.getMaxVisitors())));
			}catch (NumberFormatException ex){
					JOptionPane.showMessageDialog(null,"Please Insert a number","Warning",JOptionPane.WARNING_MESSAGE);
			}catch (Exception e1){
				e1.printStackTrace();
			}
		});
		options.add(visitorsAmount);

		JMenuItem snapshotInterval = new JMenuItem("Save Interval");
		snapshotInterval.addActionListener(e ->{
			try {
				Simulator.setSaveInterval(Duration.ofMinutes(Integer.parseInt(JOptionPane.showInputDialog(null,
						"The targeted interval at which the simulator should make a snapshot of its current\n" +
								"state. A lesser interval results in detailed jumping, but limits the maximum\n" +
								"time you can revert. A bigger number results in more time, but greater steps.\n" +
								"Time in simulated-minutes",
						Simulator.getSaveInterval().getSeconds() / 60))));
			}catch (NumberFormatException ex){
				JOptionPane.showMessageDialog(null,"Please Insert a number","Warning",JOptionPane.WARNING_MESSAGE);
			}catch (Exception e1){
				e1.printStackTrace();
			}
		});
		options.add(snapshotInterval);


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

	@Override
	public void onDataChanged() {
		setTitle(fileSystem.getFileName() + " - " + APP_NAME + " v" + APP_VERSION);
		repaint();
	}
}
