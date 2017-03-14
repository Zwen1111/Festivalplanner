package festivalplanner.gui.dialog;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Maarten on 10/02/2017.
 */
public class AddArtistDialog extends JDialog {

    public AddArtistDialog() {
        super(null, ModalityType.APPLICATION_MODAL);
        setSize(300, 400);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        setContentPane(mainPanel);
        //setUndecorated(true);
        //setVisible(true);
        setLayout(null);

        JLabel naamLabel = new JLabel("Name:");
        naamLabel.setFont(new Font(Font.SERIF,Font.PLAIN,25));
        add(naamLabel);
        naamLabel.setBounds(20,0,100,100);
        JTextField naam = new JTextField("");
        naam.setFont(new Font(Font.SERIF,Font.PLAIN,25));
        add(naam);
        naam.setBounds(130,25,150,50);


        JLabel genreLabel = new JLabel("Genre:");
        genreLabel.setFont(new Font(Font.SERIF,Font.PLAIN,25));
        add(genreLabel);
        genreLabel.setBounds(20,120,100,100);
        JTextField genre = new JTextField("");
        genre.setFont(new Font(Font.SERIF,Font.PLAIN,25));
        add(genre);
        genre.setBounds(130,145,150,50);

        JLabel popularityLabel = new JLabel("Popularity:");
        popularityLabel.setFont(new Font(Font.SERIF,Font.PLAIN,25));
        add(popularityLabel);
        popularityLabel.setBounds(20,220,100,100);
        JSpinner popularity = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        popularity.setFont(new Font(Font.SERIF,Font.PLAIN,25));
        add(popularity);
        popularity.setBounds(130,245,150,50);

        JButton confirm = new JButton("Confirm");
        confirm.setBounds(30,getHeight() - 100, 100,50);
        confirm.addActionListener(e -> {
            Database.addArtist(new Artist(naam.getText(),genre.getText(), (int) popularity.getValue()));
            dispose();
        });
        add(confirm);

        JButton cancel = new JButton("Cancel");
        cancel.setBounds(getWidth() - 130,getHeight() - 100, 100,50);
        cancel.addActionListener(e -> {
            dispose();
        });
        add(cancel);
        //Once setVisible is called on a dialog, the main thread is blocked.
		//Keep this call last!
        setVisible(true);
    }
}
