package festivalplanner.gui.table2d;

import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.gui.AddPerformanceButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Maarten Nieuwehuize
 */
public class CalendarTable2D extends JPanel implements Database.OnDataChangedListener{
    private int tableHeight;
    private int columHeigth;
    private int beginTableX;
    private int beginTableY;
    private int widthTimeColum;
    private int hour;
    private int tableWidth;
    private ArrayList<CheckPerformanceButton> buttons;
    private Database database;
    private AddPerformanceButton addPerformance;

    public CalendarTable2D(Database database) {
        setName("2D Table");

        //sets the variable begin coords of the agenda
        this.database = database;
        beginTableX = 10;
        beginTableY = 30;
        widthTimeColum = beginTableX + 40;
        hour = 24;
        setLayout(null);
        buttons = new ArrayList<>();

        //adds a a arrayList of buttons with the size of the amount of performances.
        for (int i = 0; i < database.getPerformances().size(); i++) {
            CheckPerformanceButton button = new CheckPerformanceButton(database.getPerformances().get(i), database);
            add(button);
            buttons.add(button);
        }

        //add's a button from wich you can add perfromance's
        addPerformance = new AddPerformanceButton(database);

        add(addPerformance);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        tableHeight = getHeight() - 140;
        columHeigth = tableHeight + 10;
        tableWidth = getWidth() - beginTableX;


        int amountOfStages = database.getStages().size();
        int stageWidth = ((tableWidth - widthTimeColum) / amountOfStages) ;
        tableWidth = (tableWidth / amountOfStages) * amountOfStages;

        /*makes the background of the agenda excluding the border*/
        int heightRow = (int) Math.floor(tableHeight / hour);
        int currentYRow = beginTableY;
        for (int i = 0; i < hour; i++) {

            if (i % 2 == 0) {
                g2d.setColor(Color.GRAY);
            } else g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(beginTableX, currentYRow, tableWidth - beginTableX - 1, heightRow);

            String timeString;
            if (i >= 10) {
                timeString = i + ":00";
            } else timeString = "0" + i + ":00";
            g2d.setColor(Color.black);

            Font sizeString;
            if (heightRow >= 16) {
                sizeString = new Font("Serif", Font.BOLD, 16);
            } else sizeString = new Font("Serif", Font.BOLD, heightRow);

            g2d.setFont(sizeString);
            g2d.drawString(timeString, beginTableX, currentYRow + heightRow - 1);
            currentYRow += heightRow;
        }

        /*draws the border of the background*/
        g2d.setColor(Color.black);
        g2d.drawRect(beginTableX, beginTableY, tableWidth - beginTableX - 1, heightRow * hour);
        //draws the line next to the time collum
        g2d.drawLine(widthTimeColum, beginTableY, widthTimeColum, heightRow * hour + beginTableY);

        /*draws the line above the agenda*/
        g2d.setColor(Color.darkGray);
        g2d.fillRect(10, 10, tableWidth - beginTableX, beginTableY - 10);

        /*writes time above the time collum*/
        g2d.setColor(Color.white);
        g2d.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        g2d.drawString("Time", 10, 30);

        /*write's the stagename above collum and puts a line between collums*/

            g2d.setFont(new Font(Font.SERIF, Font.BOLD, 20));

        for (int i = 0; i < amountOfStages; i++) {

            g2d.setColor(Color.WHITE);


            String nameStage = database.getStages().get(i).getName();
            String croppedNameStage = "";
            if(nameStage.length() < stageWidth / 12)
            {
                 croppedNameStage = nameStage;
            } else {croppedNameStage = nameStage.substring(0,stageWidth / 12) + "...";}

            g2d.drawString(croppedNameStage,
                    widthTimeColum + stageWidth * i,
                    beginTableY - 2);
            g2d.setColor(Color.black);
            g2d.drawLine(widthTimeColum + stageWidth * i, beginTableY, widthTimeColum +
                           stageWidth * i,
                    heightRow * hour + beginTableY);
        }


        while (database.getPerformances().size() > buttons.size())
        {
            CheckPerformanceButton button = new CheckPerformanceButton(database.getPerformances().get(buttons.size()), database);
            add(button);
            buttons.add(button);
        }
        //sets the button on the right place of a performances
        for (int i = 0; i < database.getPerformances().size(); i++) {

            JButton button = buttons.get(i);
            button.setForeground(Color.white);


            Performance perf = database.getPerformances().get(i);
            button.setText(perf.getArtistNames());


            int colIndex = database.getStages().indexOf(perf.getStage());

            Performance performance = database.getPerformances().get(i);

            button.setBackground(Color.DARK_GRAY);

            int startYOfPerformance = (int) Math.floor(heightRow * (performance.getStartTime().getHour() + ((float) performance.getStartTime().getMinute() / 60)));
            int endWidthOfPerformance = (int) Math.floor(heightRow * ((performance.getEndTime().getHour() + ((float) performance.getEndTime().getMinute() / 60)) - (performance.getStartTime().getHour() + ((float) performance.getStartTime().getMinute() / 60))));

            button.setBounds(widthTimeColum + (tableWidth - widthTimeColum) / amountOfStages * colIndex,
                    startYOfPerformance + beginTableY,
                    ((tableWidth - widthTimeColum) / amountOfStages), endWidthOfPerformance);
            if (endWidthOfPerformance/2 > 15){button.setFont(new Font("Serif", Font.BOLD, 20));}else{
                button.setFont(new Font("Serif", Font.BOLD, endWidthOfPerformance/2));
            }

        }


        //set the place of the addPerformance button
        addPerformance.setBounds(beginTableX, getHeight() - 100, 85, 85);


        //repaint();

    }

    @Override
    public void onDataChanged() {
        for (CheckPerformanceButton button : buttons) {
            remove(button);
        }
        buttons = new ArrayList<>();
        repaint();
    }
}
