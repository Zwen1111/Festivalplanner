package festivalplanner.gui.simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * @author Lars Moesman
 */
public class SimulatorTabGUI extends JPanel implements ActionListener {

    private ArrayList<JButton> buttonArrayList;
    private SimulatorPanel simulatorPanel;

    private LocalTime time;
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
		time = LocalTime.of(7,0);
		timeLabel = new JLabel(time.getHour() + ":" + time.getMinute());


        buttonArrayList.add(playButton);
        buttonArrayList.add(zoomInButton);
        buttonArrayList.add(zoomOutButton);

        playButton.setName("Pause");

        playButton.addActionListener(e -> {

            if (playButton.getName().equals("Play")) {
                playButton.setIcon(new ImageIcon(getClass().getResource("/icon's/pauseIcon.png")));
                playButton.setName("Pause");
            } else if (playButton.getName().equals("Pause")) {
                playButton.setIcon(new ImageIcon(getClass().getResource("/icon's/playIcon.png")));
                playButton.setName("Play");
            }
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

        add(playButton);
        add(zoomInButton);
        add(zoomOutButton);
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
            if(frame >= 7) {
                timeLabel.setText(time.toString());
                time = time.plusMinutes(1);
                frame = 0;
            }
            simulatorPanel.update(time);
        }

		repaint();
	}
}
