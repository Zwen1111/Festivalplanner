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
public class FileSystem implements Database.OnDataChangedListener{
    private File file;
    private Database database;
    private Boolean hasDataChanged;

    public FileSystem(Database database) {
        hasDataChanged = false;
        this.database = database;
    }

    public void newAgenda() {
        if(askForSaving() == true) {
            file = null;
            database.clear();
            database.notifyDataChanged();
        }
    }


    public Boolean save() {
        if (file == null) {
            return saveAs();
        } else {
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(this.file))) {
                output.writeObject(database.getPerformances());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public Boolean saveAs() {

        JFileChooser fc = new JFileChooser();
        if(file == null) {
            fc.setSelectedFile(new File("Festival_planner_1.fd"));
        }else fc.setSelectedFile(file);
        fc.setFileFilter(new FileNameExtensionFilter("database of the agenda ", "fd"));

        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            File betterFile;
            if (!file.getName().endsWith(".fd")) {
                betterFile = new File(file.getAbsolutePath() + ".fd");
            } else {

                if(file.exists()) {
                    int confirmcode = JOptionPane.showConfirmDialog(null, "File already exists. Do you want to overwrite the file");
                    if (confirmcode == JOptionPane.CANCEL_OPTION) {
                        saveAs();
                    } else if (confirmcode == JOptionPane.NO_OPTION) {
                        return false;
                    }

                }betterFile = new File(file.getAbsolutePath());

            }
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(betterFile))) {
                output.writeObject(database.getPerformances());} catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    public void open() {

        if(askForSaving() == false)
        {
            return;
        }else {

            JFileChooser fc = new JFileChooser();
            if(file == null) {
                fc.setSelectedFile(new File("Festival_planner_1.fd"));
            }else fc.setSelectedFile(file);

            fc.setFileFilter(new FileNameExtensionFilter("Database of the agenda .fd ", "fd"));

            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.file = fc.getSelectedFile();
                try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()))) {
                    database.clear();
                    try {
						database.addPerformances((Collection<Performance>) input.readObject());
					} catch (InvalidClassException e) {
						JOptionPane.showMessageDialog(null, "There seems to be a mismatch" +
										" of versions between the software. Sadly, we cannot recover the data.",
								"Could not read file", JOptionPane.ERROR_MESSAGE);
					}
                    database.notifyDataChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getNameFile() {
        if (file == null) {
            return "Festival_planner_1";
        } else return file.getName();
    }


    private boolean askForSaving()
    {
        if(hasDataChanged) {
            int confirmCode = JOptionPane.showConfirmDialog(null, "Do want to save changes");
            if (confirmCode == JOptionPane.OK_OPTION) {
                return save();

            } else if (confirmCode == JOptionPane.CANCEL_OPTION || confirmCode == JOptionPane.CLOSED_OPTION) {
                return false;
            }

        }return true;
    }

    @Override
    public void onDataChanged() {
        hasDataChanged = true;
    }
}
