package festivalplanner.util;

import festivalplanner.data.Database;
import festivalplanner.data.Performance;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Collection;

/**
 * @author Maarten Nieuwenhuize
 */
public class FileSystem implements Database.OnDataChangedListener {

	private static final String FILTER_DESCRIPTION = "Festivalplanner Database";
	private static final String DATABASE_EXTENSION = "fd";

    private File file;
    private boolean hasDataChanged;

    public FileSystem() {
        hasDataChanged = false;
        Database.addOnDataChangedListener(this);
    }

    public void newCalendar() {
        if(closeCurrentCalendar()) {
            file = null;
            Database.clearPerformances();
            hasDataChanged = false;
        }
    }


    public boolean save() {
        if (file == null) {
            return saveAs();
        } else {
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(this.file))) {
                output.writeObject(Database.getPerformances());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean saveAs() {

        JFileChooser fc = new JFileChooser();

        if (file == null) fc.setSelectedFile(new File("unnamed." + DATABASE_EXTENSION));
        else fc.setSelectedFile(file);

        fc.setFileFilter(new FileNameExtensionFilter(FILTER_DESCRIPTION, DATABASE_EXTENSION));

        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            File betterFile;
            if (!file.getName().endsWith(DATABASE_EXTENSION)) {
                betterFile = new File(file.getAbsolutePath() + DATABASE_EXTENSION);
            } else {
                if(file.exists()) {
                    int confirmCode = JOptionPane.showConfirmDialog(null,
							"File already exists. Do you want to overwrite the file?");
                    if (confirmCode == JOptionPane.CANCEL_OPTION) {
                        saveAs();
                    } else if (confirmCode == JOptionPane.NO_OPTION) {
                        return false;
                    }
                }
                betterFile = new File(file.getAbsolutePath());
            }
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(betterFile))) {
                output.writeObject(Database.getPerformances());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    public void open() {
        if (closeCurrentCalendar()) {
            JFileChooser fc = new JFileChooser();

            fc.setFileFilter(new FileNameExtensionFilter(FILTER_DESCRIPTION, DATABASE_EXTENSION));

            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()))) {
                    try {
						Collection<Performance> newPerformances = (Collection<Performance>) input.readObject();
						//Successful read. Only now replace the performances.
						this.file = fc.getSelectedFile();
						Database.clearPerformances();
						Database.addPerformances(newPerformances);
						hasDataChanged = false;
					} catch (InvalidClassException e) {
						JOptionPane.showMessageDialog(null, "There seems to be a mismatch" +
										" of versions between the software. Sadly, we cannot recover the data.",
								"Could not read file", JOptionPane.ERROR_MESSAGE);
					}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getFileName() {
        if (file == null) {
            return "unnamed." + DATABASE_EXTENSION;
        } else return file.getName();
    }

	/**
	 * Tries to gently close the currently opened calendar. If nothing has changed, nothing needs
	 * to be done. Otherwise a dialog will be shown notifying the user of unsaved changes and giving the option
	 * to yet save the file.
	 * <p>
	 * Success can be gained in multiple different ways. A user can save the file, but the decision
	 * not to save the file is also considered a successful operation. Canceling or dismissing the dialog is
	 * considered a failure and thereby advises depending code to cease further execution.
	 *
	 * @return true if the old calendar has successfully closed, false otherwise.
	 */
    private boolean closeCurrentCalendar() {
        if(hasDataChanged) {
			int confirmCode = JOptionPane.showConfirmDialog(null, "Do want to save changes?");
			switch (confirmCode) {
				case JOptionPane.OK_OPTION: //Yes, save.
					hasDataChanged = !save();
					return !hasDataChanged;
				case JOptionPane.NO_OPTION: //No, don't save.
					hasDataChanged = false;
					return true;
				case JOptionPane.CANCEL_OPTION:
				case JOptionPane.CLOSED_OPTION: //No decision made.
					return false;
			}
		}
        return true;
    }

    @Override
    public void onDataChanged() {
        hasDataChanged = true;
    }
}
