package festivalplanner;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;
import festivalplanner.gui.GUIFrame;

import javax.swing.*;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * @author Coen Boelhouwers
 */
public class Main {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Could not find cross-platform UI.");
			e.printStackTrace();
		}
		new GUIFrame();
	}

//	public static void test(Database database) {
//        database.addStage(new Stage("Main-stage"));
//        database.addStage(new Stage("Garbage-stage"));
//
//	    database.addArtist(new Artist("Ellie Goulding", "Pop", 7));
//		database.addArtist(new Artist("Afrojack", "Hardstyle", 8));
//		database.addArtist(new Artist("Bob", null, 5));
//		database.addArtist(new Artist("Bobine", null, 6));
//		database.addArtist(new Artist("Lone ranger", null, 5));
//
//		database.addPerformance(new Performance(
//				database.getStages().get(0),
//				LocalTime.of(20,30),
//				LocalTime.of(22,0),
//				database.getArtists().get(1), database.getArtists().get(0)));
//		database.addPerformance(new Performance(
//                database.getStages().get(0),
//				LocalTime.of(12,0),
//				LocalTime.of(14,0),
//				database.getArtists().get(2), database.getArtists().get(3)));
//		database.addPerformance(new Performance(
//                database.getStages().get(1),
//				LocalTime.of(19,0),
//				LocalTime.of(22,0),
//				database.getArtists().get(4), database.getArtists().get(3)));
//	}
}
