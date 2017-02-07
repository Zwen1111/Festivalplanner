package festivalplanner;

import festivalplanner.data.Artist;
import festivalplanner.data.Database;
import festivalplanner.data.Performance;
import festivalplanner.data.Stage;
import festivalplanner.gui.GUIFrame;

import java.time.LocalTime;
import java.util.List;

/**
 * @author Coen Boelhouwers
 */
public class Main {

	public static void main(String[] args) {
		new GUIFrame();
	}

	public static void test(Database database) {
		Stage stage1 = new Stage("Stage numero uno");
		Stage stage2 = new Stage("Stage numero dos");

		database.getPerformances().add(new Performance(
				stage1,
				LocalTime.of(20,0),
				LocalTime.of(22,0),
				new Artist("Bob", null, 5),
				new Artist("Bobine", null, 6)));
		database.getPerformances().add(new Performance(
				stage1,
				LocalTime.of(22,0),
				LocalTime.MIDNIGHT,
				new Artist("Afrojack", null, 5)));
		database.getPerformances().add(new Performance(
				stage2,
				LocalTime.of(19,0),
				LocalTime.MIDNIGHT,
				new Artist("Lone ranger", null, 5),
				new Artist("Bobine", null, 6)));
	}
}
