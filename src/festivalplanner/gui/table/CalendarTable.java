package festivalplanner.gui.table;

import festivalplanner.Main;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;
import festivalplanner.gui.AddPerformanceButton;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class CalendarTable extends JPanel {

    private AbstractTableModel model;
    private ArrayList<Performance> performancesSorted;
    private Main main;
    private Database database;
    private JPanel mainScreen;
    private int index;
    private int maxIndex;

    public CalendarTable(Database database) {
		JTable table = new JTable();
		AddPerformanceButton addButton = new AddPerformanceButton(database);
        setName("Table");
		this.database = database;
		index = 0;
        filterPerformances();

        LocalTime timeTable = LocalTime.of(0,0);
        table.setModel(model = new AbstractTableModel()
        {
            @Override
            public int getRowCount () {
            return 48;
        }

            @Override
            public int getColumnCount () {
            return 4;
        }

            @Override
            public Object getValueAt ( int rowIndex, int columnIndex){
            switch (columnIndex) {
                case 0:
                    return timeTable.plusMinutes(30 * rowIndex).toString();
                case 1:
                    if ((performanceTimeCheck(timeTable.plusMinutes(30 * rowIndex)) != null)) {
                    return (performanceTimeCheck(timeTable.plusMinutes(30 * rowIndex))).getArtistNames();
                }

                case 2:
                    if ((performanceTimeCheck(timeTable.plusMinutes(30 * rowIndex)) != null)) {
                        return (performanceTimeCheck(timeTable.plusMinutes(30 * rowIndex))).generatePopularity();
                    }

                case 3:
                    if ((performanceTimeCheck(timeTable.plusMinutes(30 * rowIndex)) != null)) {
                        return (performanceTimeCheck(timeTable.plusMinutes(30 * rowIndex))).getArtistGenres();
                    }
                default:
                    return null;


            }
        }
            @Override
            public String getColumnName ( int column){
            if (column == 0)
                return "Time";
            else if (column == 1)
                return "Artist";
            else if (column == 2)
                return "Popularity";
            else if (column == 3)
                return "Genre";
            return "";
        }


        });

        index = 0;
        mainScreen = new JPanel(new BorderLayout());
        JTable mainStage = new JTable();
        JTable codeStage = new JTable();
        JTable littleGirlStage = new JTable();
        JTable teenageStage = new JTable();
        mainStage.setModel(model);
        codeStage.setModel(model);
        littleGirlStage.setModel(model);
        teenageStage.setModel(model);

        mainScreen.add(new JScrollPane(mainStage), BorderLayout.CENTER);

        codeStage.setVisible(false);
        littleGirlStage.setVisible(false);
        teenageStage.setVisible(false);
        JPanel bottemScreen = new JPanel();
        JLabel stageLabel = new JLabel("");

        JButton next = new JButton("next");
        next.addActionListener((ActionEvent e) ->
        {
            index++;
            if (index > maxIndex) {
                index = 0;
            }
            filterPerformances();
            stageLabel.setText(indexToStage());
            this.repaint();
        });
        JButton back = new JButton("back");
        back.addActionListener(e ->
        {
            index--;
            if (index < 0) {
                index = maxIndex;
            }
            filterPerformances();
            stageLabel.setText(indexToStage());
            this.repaint();

        });

        stageLabel.setText(indexToStage());
        bottemScreen.add(addButton);
        bottemScreen.add(back);
        bottemScreen.add(stageLabel);
        bottemScreen.add(next);
        mainScreen.add(bottemScreen, BorderLayout.SOUTH);

        this.add(mainScreen);
    }

    public void filterByStage(Stage stage) {
        performancesSorted = new ArrayList<>();
        for (Performance p : database.getPerformances()) {
            if (p.getStage().getName().equals(stage.getName())) {
                performancesSorted.add(p);
            }
        }
    }

    public void filterPerformances() {
    	List<Stage> stages = database.getStages();
    	filterByStage(stages.get(index));
        maxIndex = stages.size() - 1;
    }

    public String indexToStage() {
        String text = "";
        List<Stage> stages = database.getStages();


        text = stages.get(index).getName();

        return text;
    }

    public Performance performanceTimeCheck(LocalTime timeTable) {
        try {
            for(Performance p : performancesSorted) {
                if (timeTable.compareTo(p.getStartTime()) >= 0 && timeTable.compareTo(p.getEndTime()) < 0) {
                    return p;
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        return null;
    }

}
