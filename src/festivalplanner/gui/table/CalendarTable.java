package festivalplanner.gui.table;

import festivalplanner.Main;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;
import festivalplanner.gui.AddPerformanceButton;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class CalendarTable extends JPanel implements Database.OnDataChangedListener {

    private AbstractTableModel model;
    private ArrayList<Performance> performancesSorted;
    private Database database;
    private JPanel mainScreen;
    private int index;
    private JComboBox stageComboBox;
    private ArrayList<String> comboBoxItems;

    public CalendarTable(Database database) {
        JTable table = new JTable();
        stageComboBox = new JComboBox();
        AddPerformanceButton addButton = new AddPerformanceButton(database);
        setName("Table");
        this.database = database;
        index = 0;
        comboBoxItems = new ArrayList<>();

        filterPerformances();

        LocalTime timeTable = LocalTime.of(0, 0);
        table.setModel(model = new AbstractTableModel() {
            @Override
            public int getRowCount() {

                return 48;
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
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
            public String getColumnName(int column) {
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

        fillComboBox();

        stageComboBox.addActionListener(e -> {
            index = stageComboBox.getSelectedIndex();
            filterPerformances();
            this.repaint();
        });

//        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
//
//
//        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
//        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
//        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
//        sorter.setSortKeys(sortKeys);
//        table.setRowSorter(sorter);


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

        bottemScreen.add(addButton);
        bottemScreen.add(stageComboBox);
        mainScreen.add(bottemScreen, BorderLayout.SOUTH);

        this.add(mainScreen);
    }

    public void fillComboBox() {
        for (Stage stage : database.getStages()) {
            if (!comboBoxItems.contains(stage.getName())) {
                comboBoxItems.add(stage.getName());
                stageComboBox.addItem(stage.getName());
            }
        }
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
        if (stages.size() > 0) {
            filterByStage(stages.get(index));
        }

    }

    public Performance performanceTimeCheck(LocalTime timeTable) {
        try {
            for (Performance p : performancesSorted) {
                if (timeTable.compareTo(p.getStartTime()) >= 0 && timeTable.compareTo(p.getEndTime()) < 0) {
                    return p;
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        return null;
    }

    @Override
    public void onDataChanged() {

        if (database.getPerformances().size() == 0) {
            index = 0;
            performancesSorted = new ArrayList<>();
        }
        if (database.getStages().size() == 0) {
            stageComboBox.removeAllItems();
            comboBoxItems.clear();
        } else {
            fillComboBox();
        }

        model.fireTableDataChanged();
        repaint();

    }
}

