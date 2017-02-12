package festivalplanner.gui.guiUtil;

import festivalplanner.data.Database;
import festivalplanner.data.Performance;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Collection;

/**
 * @author Maarten Nieuwenhuize
 */
public class FileSystem {
    private File file;
    private Database database;

    public FileSystem(Database database) {
        this.database = database;
    }

    public void newAgenda() {
        if(askForSaving() == true) {
            database.clear();
            database.notifyDataChanged();
        }
    }


    public void save() {
        if (file == null) {
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File("Festival_planner_1")))) {
                output.writeObject(database.getPerformances());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(this.file))) {
                output.writeObject(database.getPerformances());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAs() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("database of the agenda ", "fd"));


        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            File betterFile;
            if (!file.getName().endsWith(".fd")) {
                betterFile = new File(file.getAbsolutePath() + ".fd");
            } else {

                int confirmcode = JOptionPane.showConfirmDialog(null, "File alreadyexist. Do you want to overwrite the file");
                if (confirmcode == JOptionPane.CANCEL_OPTION) {
                    saveAs();
                    return;
                } else if (confirmcode == JOptionPane.NO_OPTION) {
                    return;
                }
                betterFile = new File(file.getAbsolutePath());

            }
            try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(betterFile))) {
                output.writeObject(database.getPerformances());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void open() {
        if(askForSaving() == false)
        {
            return;
        }else {

            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Database of the agenda .fd ", "fd"));

            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.file = fc.getSelectedFile();
                try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()))) {
                    database.clear();
                    database.addPerformances((Collection<Performance>) input.readObject());
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
        int confirmCode = JOptionPane.showConfirmDialog(null, "Do want to save changes");
        if (confirmCode == JOptionPane.OK_OPTION) {
            saveAs();

        }else if (confirmCode == JOptionPane.CANCEL_OPTION || confirmCode == JOptionPane.CLOSED_OPTION) {
            return false;
        }
        return true;
    }
}
