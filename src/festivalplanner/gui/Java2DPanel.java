package festivalplanner.gui;

import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Java2DPanel extends JPanel {
    private int heightTable;
    private int heightColum;
    private int beginTableX;
    private int beginTableY;
    private int widthTimeColum;
    private int hour;
    private int widthTable;
    private ArrayList<JButton> buttons;
    private Database database;

    Java2DPanel(Database database) {
        this.database = database;
        beginTableX = 10;
        beginTableY = 30;
        widthTimeColum = beginTableX + 40;
        hour = 24;
        setLayout(null);
        buttons = new ArrayList<>();

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

        heightTable = getHeight() - 140;
        heightColum = heightTable + 10;
        widthTable = getWidth() - 20;

        int heightRow = (int) Math.floor(heightTable / hour);
        int currentYRow = beginTableY;
        for (int i = 0; i < hour; i++) {

            if (i % 2 == 0) {
                g2d.setColor(Color.GRAY);
            } else g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(beginTableX, currentYRow, widthTable, heightRow);

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

        g2d.setColor(Color.black);
        g2d.drawRect(beginTableX, beginTableY, widthTable, heightRow * hour);
        g2d.drawLine(widthTimeColum, beginTableY, widthTimeColum, heightRow * hour + 10);

        g2d.setColor(Color.darkGray);
        g2d.fillRect(10, 10, widthTable + 1, beginTableY - 10);

        g2d.setColor(Color.white);
        g2d.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        g2d.drawString("Time", 10, 30);

        if (getWidth() / 55 >= 20) {
            g2d.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        } else g2d.setFont(new Font(Font.SERIF, Font.BOLD, getWidth() / 60));

        int amountOfStagesMin1 = database.getStages().size();
        for (int i = 0; i < amountOfStagesMin1; i++) {

            g2d.setColor(Color.WHITE);
            g2d.drawString(database.getStages().get(i).getName(),//performances.get(i).getStage().getName(),
					widthTimeColum + ((widthTable - widthTimeColum) / amountOfStagesMin1) * i,
                    beginTableY - 2);
            g2d.setColor(Color.black);
            g2d.drawLine(widthTimeColum + ((widthTable - widthTimeColum) / amountOfStagesMin1) * i, beginTableY, widthTimeColum +
                            ((widthTable - widthTimeColum) / amountOfStagesMin1) * i,
                    heightRow * hour + beginTableY);
        }

        for (int i = 0; i < database.getPerformances().size()/*performances.size()*/; i++) {
            JButton button = buttons.get(i);
            button.setForeground(Color.white);

            //new
			Performance perf = database.getPerformances().get(i);
			button.setText(perf.getArtists().get(0).getName());
            //button.setText("Artist naam");

            //new
            int colIndex = database.getStages().indexOf(perf.getStage());

            button.setBackground(Color.black);
            button.setBounds(widthTimeColum + (widthTable-widthTimeColum)/amountOfStagesMin1*colIndex,
					//((widthTable - widthTimeColum) / amountOfStagesMin1) * 0,
                    heightRow *
                            10 + beginTableY,
                    ((widthTable - widthTimeColum) / amountOfStagesMin1), heightRow *
                            (12 -
                                    10));

        }


    }

}
