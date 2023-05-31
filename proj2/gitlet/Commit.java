package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
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
public class Commit implements Serializable {
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
    public Reference[] references = new Reference[100];

    /** The reference to this Commit's immediate parent. */
    public String myParent;


    /** Constructor for the Commit object. */
    public Commit(String msg, String parent) {
        if (parent == null) {
            dateAndTime = formatDateAndTime(true);
            message = "initial commit";
            myParent = null;
        } else {
            dateAndTime = formatDateAndTime(false);
            message = msg;
            myParent = parent;
        }
    }


    /** Returns the message of the commit as a string. */
    public String getDateAndTime() {
        return dateAndTime;
    }


    /** Returns the message of the commit as a string. */
    public String getMessage() {
        return message;
    }


    /** Formats the dateAndTime to match the autograder requirements. The
     * orginal implementation just used "dateAndTime = (new Date(0)).toString();"
     * & dateAndTime = (new Date()).toString(); for line 43 & 48 respectively
     * which does not require calling this helper method. */
    private String formatDateAndTime(boolean isFirst) {
        if (isFirst) {
            String timestamp = "Thu Jan 01 00:00:00 1970 +0800";
            return timestamp;
        } else {
            String timestamp = (new Date()).toString();
            String holder = "www www d+ dd:dd:dd";

            char[] dateActual = timestamp.toCharArray();
            char[] formatter = holder.toCharArray();
            for (int i = 0; i < 19; i++) {
                formatter[i] = dateActual[i];
            }

            String date = "YYYY";
            char[] dateHolder = date.toCharArray();

            for (int i = 24; i < 28; i++) {
                dateHolder[i-24] = dateActual[i];
            }

            timestamp = new String(formatter);
            date = new String(dateHolder);
            timestamp = timestamp + " " + date + " +0800";

            return timestamp;
        }
    }
}
