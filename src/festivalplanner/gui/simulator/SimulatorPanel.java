package festivalplanner.gui.simulator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Lars Moesman
 */
public class SimulatorPanel extends JPanel {

    private ArrayList<JButton> buttonArrayList;
    private TileMapPanel tileMapPanel;

    public SimulatorPanel() {
    	setName("TileMapPanel");
        buttonArrayList = new ArrayList<>();
        tileMapPanel = new TileMapPanel();
        tileMapPanel.setBackground(Color.black);



        SpringLayout springLayout = new SpringLayout();
        this.setLayout(springLayout);

        JButton playButton = new JButton(new ImageIcon(getClass().getResource("/icon's/playIcon.png")));
        JButton zoomInButton = new JButton(new ImageIcon(getClass().getResource("/icon's/zoomInIcon.png")));
        JButton zoomOutButton = new JButton(new ImageIcon(getClass().getResource("/icon's/zoomOutIcon.png")));
        JLabel timeLabel = new JLabel("10:31");



        buttonArrayList.add(playButton);
        buttonArrayList.add(zoomInButton);
        buttonArrayList.add(zoomOutButton);

        playButton.setName("Play");

        playButton.addActionListener(e -> {

            if (playButton.getName().equals("Play")) {
                playButton.setIcon(new ImageIcon(getClass().getResource("/icon's/pauseIcon.png")));
                playButton.setName("Pause");
            } else if (playButton.getName().equals("Pause")) {
                playButton.setIcon(new ImageIcon(getClass().getResource("/icon's/playIcon.png")));
                playButton.setName("Play");
            }
        });

        zoomInButton.addActionListener(e -> tileMapPanel.setScale(tileMapPanel.getScale() + 0.25));

        zoomOutButton.addActionListener(e -> tileMapPanel.setScale(tileMapPanel.getScale() - 0.25));

        loadButtons();

        timeLabel.setPreferredSize(new Dimension(60, 30));
        timeLabel.setFont(new Font(timeLabel.getFont().getName(), timeLabel.getFont().getStyle(), 20));
        timeLabel.setForeground(Color.WHITE);

        springLayout.putConstraint(SpringLayout.NORTH, tileMapPanel, 0, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.EAST, tileMapPanel, 0, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.WEST, tileMapPanel, 0, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.SOUTH, tileMapPanel, 0, SpringLayout.SOUTH, this);

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
        add(tileMapPanel);
    }

    public void loadButtons() {
        for (JButton button : buttonArrayList) {
            button.setPreferredSize(new Dimension(50, 50));

            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
        }
    }
}
