package festivalplanner;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;
import festivalplanner.gui.GUIFrame;

import javax.swing.*;
import java.time.LocalTime;

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

	public static void test(Database database) {
		Stage stage1 = new Stage("Main-stage");
		Stage stage2 = new Stage("Garbage-stage");

		Artist ellie = new Artist("Ellie Goulding", "Pop", 7);
		Artist afro = new Artist("Afrojack", "Hardstyle", 8);
		Artist bob = new Artist("Bob", null, 5);
		Artist bobine = new Artist("Bobine", null, 6);
		Artist lone = new Artist("Lone ranger", null, 5);

		database.getPerformances().add(new Performance(
				stage1,
				LocalTime.of(20,30),
				LocalTime.of(22,0),
				afro, ellie));
		database.getPerformances().add(new Performance(
				stage1,
				LocalTime.of(12,0),
				LocalTime.of(14,0),
				bob, bobine));
		database.getPerformances().add(new Performance(
				stage2,
				LocalTime.of(19,0),
				LocalTime.of(22,0),
				lone, bobine));
	}
}
