package festivalplanner.gui.simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author Lars Moesman
 */
public class SimulatorPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener {

    private static final double MIN_ZOOM = 0.65;
    //private static final double MIN_ZOOM_Y = 0.75;
    private static final double MAX_ZOOM = 5.65;
    //private static final double MAX_ZOOM_Y = 5.75;

    private ArrayList<JButton> buttonArrayList;
    private TileMapPanel tileMapPanel;
    private Point2D mousePosition;

    public SimulatorPanel() {
    	setName("TileMapPanel");
        buttonArrayList = new ArrayList<>();
        tileMapPanel = new TileMapPanel();
        tileMapPanel.setBackground(Color.black);

        mousePosition = new Point2D.Double(0, 0);

        SpringLayout springLayout = new SpringLayout();
        this.setLayout(springLayout);
        tileMapPanel.addMouseMotionListener(this);
        tileMapPanel.addMouseListener(this);
        tileMapPanel.addMouseWheelListener(this);

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

        zoomInButton.addActionListener(e -> {
            //tileMapPanel.translateBy(null);
            /*if ((tileMapPanel.getScaleY() < MAX_ZOOM_Y) && (tileMapPanel.getScaleX() < MAX_ZOOM_X)) {
                double oldX = tileMapPanel.getScaleX();
                double oldY = tileMapPanel.getScaleY();
                tileMapPanel.setScaleX(oldX + 0.25);
                tileMapPanel.setScaleY(oldY + 0.25);
            }*/
            if (tileMapPanel.getScale() < MAX_ZOOM) tileMapPanel.setScale(tileMapPanel.getScale() + 0.25);
            this.repaint();
        });

        zoomOutButton.addActionListener(e -> {
			//tileMapPanel.translateBy(null);
            /*if ((tileMapPanel.getScaleY() > MIN_ZOOM_Y) && (tileMapPanel.getScaleX() > MIN_ZOOM_X)) {
                double oldX = tileMapPanel.getScaleX();
                double oldY = tileMapPanel.getScaleY();
                tileMapPanel.setScaleX(oldX - 0.25);
                tileMapPanel.setScaleY(oldY - 0.25);
            }*/
			if (tileMapPanel.getScale() > MIN_ZOOM) tileMapPanel.setScale(tileMapPanel.getScale() - 0.25);
			this.repaint();
		});

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

    @Override
    public void mouseDragged(MouseEvent e) {
        tileMapPanel.translateBy(new Point2D.Double( e.getX() - mousePosition.getX()  ,  e.getY() - mousePosition.getY()));
        mousePosition = new Point2D.Double(e.getX(), e.getY());
        this.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePosition = new Point2D.Double(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
    	double value = e.getPreciseWheelRotation() * 0.1;
		System.out.println("wheel: " + value);
    	if ((value < 0 && tileMapPanel.getScale() > MIN_ZOOM) ||
				(value > 0 && tileMapPanel.getScale() < MAX_ZOOM)) {
			tileMapPanel.scaleBy(value);
			System.out.println("scaled");
			repaint();
		}
	}
}
