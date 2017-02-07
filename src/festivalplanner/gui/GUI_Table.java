package festivalplanner.gui;

import festivalplanner.Main;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class GUI_Table extends JTable {

    private AbstractTableModel model;
    private ArrayList<Performance> performancesSorted;
    private Main main;
    private Database database;
    private JPanel mainScreen;
    private int index;

    public GUI_Table(Database database) {
		this.database = database;
        sortPerformances();

        LocalTime timeTable = LocalTime.of(0,0);
        this.setModel(model = new AbstractTableModel()
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
    //    JFrame frame = new JFrame("tabel");
    //    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
            if (index > 3) {
                index = 0;
            }
            sortPerformances();
            stageLabel.setText(indexToStage());
            mainScreen.repaint();
        });
        JButton back = new JButton("back");
        back.addActionListener(e ->
        {
            index--;
            if (index < 0) {
                index = 3;
            }
            sortPerformances();
            stageLabel.setText(indexToStage());
            mainScreen.repaint();

        });

        stageLabel.setText(indexToStage());

        bottemScreen.add(back);
        bottemScreen.add(stageLabel);
        bottemScreen.add(next);
        mainScreen.add(bottemScreen, BorderLayout.SOUTH);

//        frame.setContentPane(mainScreen);
//        frame.setSize(600, 800);
//        frame.setVisible(true);


    }

    public void findByStage(Stage stage) {
        performancesSorted = new ArrayList<>();
        for (Performance p : database.getPerformances()) {
            if (p.getStage().equals(stage)) {
                performancesSorted.add(p);
            }
        }
    }

    public void sortPerformances() {
    	List<Stage> stages = database.getStages();
    	stages.forEach(this::findByStage);
        switch (index) {
            case 0:
                //findByStage("MainStage");
                break;
            case 1:
                //findByStage("LittleGirlStage");
                break;
            case 2:
                //findByStage("CodeStage");
                break;
            case 3:
                //findByStage("TeenageStage");
                break;
            default:
                break;
        }
    }

    public String indexToStage() {
        String text = "";
        switch (index) {
            case 0:
                text = "Mainstage";
                break;
            case 1:
                text = "LittleGirlStage";
                break;
            case 2:
                text = "CodeStage";
                break;
            case 3:
                text = "TeenageStage";
                break;
            default:
                break;
        }

        return text;
    }

    public Performance performanceTimeCheck(LocalTime timeTable) {
        try {
            for(Performance p : performancesSorted)
                if(timeTable.compareTo(p.getStartTime()) >= 0 && timeTable.compareTo(p.getEndTime()) <= 0) {
                    return p;
                }
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        return null;
    }

    public JPanel getMainScreen() {
        return mainScreen;
    }

}
