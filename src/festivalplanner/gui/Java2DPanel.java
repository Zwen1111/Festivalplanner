package festivalplanner.gui;

import festivalplanner.data.Database;
import festivalplanner.data.Performance;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Java2DPanel extends JPanel {
    private int tableHeight;
    private int columHeigth;
    private int beginTableX;
    private int beginTableY;
    private int widthTimeColum;
    private int hour;
    private int tableWidth;
    private ArrayList<JButton> buttons;
    private Database database;
    private AddButton addPerformance;

    Java2DPanel(Database database) {
        setName("2D Table");

        //add's a button from wich you can add perfromance's
        addPerformance = new AddButton();
        add(addPerformance);


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
            JButton button = new JButton();
            final int index = i;
            button.addActionListener(e -> {
                new ArtisGui(index);
            });
            add(button);
            buttons.add(button);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        tableHeight = getHeight() - 140;
        columHeigth = tableHeight + 10;
        tableWidth = getWidth() - beginTableX;


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
        g2d.drawLine(widthTimeColum, beginTableY, widthTimeColum, heightRow * hour + 10);

        /*draws the line above the agenda*/
        g2d.setColor(Color.darkGray);
        g2d.fillRect(10, 10, tableWidth - beginTableX, beginTableY - 10);

        /*writes time above the time collum*/
        g2d.setColor(Color.white);
        g2d.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        g2d.drawString("Time", 10, 30);

        /*write's the stagename above collum and puts a line between collums*/
        if (getWidth() / 55 >= 20) {
            g2d.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        } else g2d.setFont(new Font(Font.SERIF, Font.BOLD, getWidth() / 60));
        int amountOfStages = database.getStages().size();
        for (int i = 0; i < amountOfStages; i++) {

            g2d.setColor(Color.WHITE);
            g2d.drawString(database.getStages().get(i).getName(),
                    widthTimeColum + ((tableWidth - widthTimeColum) / amountOfStages) * i,
                    beginTableY - 2);
            g2d.setColor(Color.black);
            g2d.drawLine(widthTimeColum + ((tableWidth - widthTimeColum) / amountOfStages) * i, beginTableY, widthTimeColum +
                            ((tableWidth - widthTimeColum) / amountOfStages) * i,
                    heightRow * hour + beginTableY);
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
            button.setBounds(widthTimeColum + (tableWidth -widthTimeColum)/amountOfStages*colIndex,
                    heightRow *
                            performance.getStartTime().getHour() + beginTableY,
                    ((tableWidth - widthTimeColum) / amountOfStages), heightRow *
                            (performance.getEndTime().getHour() -
                                    performance.getStartTime().getHour()));

        }


        //set the place of the addPerformance button
        addPerformance.setBounds(beginTableX,getHeight() - 100, 85,85);



        //repaint();
    }

}
