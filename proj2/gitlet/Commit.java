package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.util.Date;
import java.util.ArrayList;

/** Represents a gitlet commit object.
 *  Commit is class that has 5 instance variables.
 *  dateAndTime: Stores the date & time of the commit.
 *  message: Stores the message of commit.
 *  references: Stores the all the references to the files part of the commit.
 *  myParent: Stores a reference to the previous commit.
 *  @author om
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The Date & Time of this Commit. */
    private String dateAndTime;

    /** The message of this Commit. */
    private String message;

    /** The references to the file contents for each commit, stored in an ArrayList. */
    private ArrayList<File>[] references;

    /** The reference to this Commit's immediate parent. */
    private Commit myParent;

    public Commit() {

    }
}
