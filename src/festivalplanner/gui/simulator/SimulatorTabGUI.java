package festivalplanner.gui.simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * @author Lars Moesman
 */
public class SimulatorTabGUI extends JPanel implements ActionListener {

    private ArrayList<JButton> buttonArrayList;
    private SimulatorPanel simulatorPanel;

    private LocalTime time;
    private LocalTime startTime;
    private JLabel timeLabel;
    private int frame;

    public SimulatorTabGUI() {
    	setName("SimulatorPanel");
        buttonArrayList = new ArrayList<>();
        simulatorPanel = new SimulatorPanel();
        simulatorPanel.setBackground(Color.black);


        SpringLayout springLayout = new SpringLayout();
        this.setLayout(springLayout);

        JButton playButton = new JButton(new ImageIcon(getClass().getResource("/icon's/playIcon.png")));
        JButton zoomInButton = new JButton(new ImageIcon(getClass().getResource("/icon's/zoomInIcon.png")));
        JButton zoomOutButton = new JButton(new ImageIcon(getClass().getResource("/icon's/zoomOutIcon.png")));
        JButton prevButton = new JButton(new ImageIcon(getClass().getResource("/icon's/prevIcon.png")));
        JButton nextButton = new JButton(new ImageIcon(getClass().getResource("/icon's/nextIcon.png")));
        JButton resetButton = new JButton(new ImageIcon(getClass().getResource("/icon's/resetIcon.png")));
		startTime = LocalTime.of(5,0);
		time = startTime;
		if (time.getHour() < 10)
		    if (time.getMinute() < 10)
		        timeLabel = new JLabel("0" + time.getHour() + ":0" + time.getMinute());
		    else
                timeLabel = new JLabel("0" + time.getHour() + ":" + time.getMinute());
        else
            if (time.getMinute() < 10)
                timeLabel = new JLabel(time.getHour() + ":0" + time.getMinute());
            else
                timeLabel = new JLabel(time.getHour() + ":" + time.getMinute());


        buttonArrayList.add(playButton);
        buttonArrayList.add(zoomInButton);
        buttonArrayList.add(zoomOutButton);
        buttonArrayList.add(prevButton);
        buttonArrayList.add(nextButton);
        buttonArrayList.add(resetButton);

        playButton.setName("Pause");

        playButton.addActionListener(e -> {

            if (playButton.getName().equals("Pause")) {
                playButton.setIcon(new ImageIcon(getClass().getResource("/icon's/pauseIcon.png")));
                playButton.setName("Play");
            } else if (playButton.getName().equals("Play")) {
                playButton.setIcon(new ImageIcon(getClass().getResource("/icon's/playIcon.png")));
                playButton.setName("Pause");
            }
        });

        prevButton.addActionListener(e -> {
            simulatorPanel.loadPrev();
            time = time.minusHours(1);
        });

        nextButton.addActionListener(e -> {
            simulatorPanel.loadNext();
            time = time.plusHours(1);
        });

        resetButton.addActionListener(e -> {
            time = startTime;
            simulatorPanel.reset();
        });

        zoomInButton.addActionListener(e -> simulatorPanel.setScale(simulatorPanel.getScale() + 0.25));

        zoomOutButton.addActionListener(e -> simulatorPanel.setScale(simulatorPanel.getScale() - 0.25));

        loadButtons();

        timeLabel.setPreferredSize(new Dimension(60, 30));
        timeLabel.setFont(new Font(timeLabel.getFont().getName(), timeLabel.getFont().getStyle(), 20));
        timeLabel.setForeground(Color.WHITE);

        springLayout.putConstraint(SpringLayout.NORTH, simulatorPanel, 0, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.EAST, simulatorPanel, 0, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.WEST, simulatorPanel, 0, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.SOUTH, simulatorPanel, 0, SpringLayout.SOUTH, this);

        springLayout.putConstraint(SpringLayout.NORTH, timeLabel, -100, SpringLayout.SOUTH, this);
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, timeLabel, 0, SpringLayout.HORIZONTAL_CENTER, this);

        springLayout.putConstraint(SpringLayout.NORTH, playButton, 10, SpringLayout.SOUTH, timeLabel);
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, playButton, 0, SpringLayout.HORIZONTAL_CENTER, this);

        springLayout.putConstraint(SpringLayout.NORTH, zoomInButton, 0, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, zoomInButton, 0, SpringLayout.WEST, this);

        springLayout.putConstraint(SpringLayout.NORTH, zoomOutButton, 0, SpringLayout.SOUTH, zoomInButton);
        springLayout.putConstraint(SpringLayout.WEST, zoomOutButton, 0, SpringLayout.WEST, this);

        springLayout.putConstraint(SpringLayout.NORTH, prevButton, 10, SpringLayout.SOUTH, timeLabel);
        springLayout.putConstraint(SpringLayout.EAST, prevButton, -5, SpringLayout.WEST, playButton);

        springLayout.putConstraint(SpringLayout.NORTH, nextButton, 10, SpringLayout.SOUTH, timeLabel);
        springLayout.putConstraint(SpringLayout.WEST, nextButton, 5, SpringLayout.EAST, playButton);

        springLayout.putConstraint(SpringLayout.NORTH, resetButton, 0, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.EAST, resetButton, 0, SpringLayout.EAST, this);

        add(playButton);
        add(zoomInButton);
        add(zoomOutButton);
        add(prevButton);
        add(nextButton);
        add(resetButton);
        add(timeLabel);
        add(simulatorPanel);


		frame = 0;
        int fps = 60;
        new Timer(1000/fps,this).start();
    }

    public void loadButtons() {
        for (JButton button : buttonArrayList) {
            button.setPreferredSize(new Dimension(50, 50));

            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
        }
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        frame++;
        if(buttonArrayList.get(0).getName().equals("Play")) {
            simulatorPanel.update(time);
            if(frame >= 3) {
                timeLabel.setText(time.truncatedTo(ChronoUnit.MINUTES).toString());
                time = time.plusSeconds(15);
                frame = 0;
                if(time.getSecond() == 0 && time.getMinute() == 0){
                    //save
                }
            }
        }
		repaint();
	}
}
