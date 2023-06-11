package gitlet;

import java.io.Serializable;

/** Represents a Reference object.
 *  Reference is class that has 2 instance variables.
 *  filename: Filename of the file that is staged for add/rm.
 *  contents: Stores the relevant Blob (contents of files).
 *  @author om
 */
public class Reference implements Serializable {
    public String filename;
    public String blob;


    /** Constructor for the Reference object. */
    public Reference(String fn, String blb) {
        filename = fn;
        blob = blb;
    }


    /** Returns the filename of the Reference as a string. */
    public String getfilename() {
        return filename;
    }


    /** Returns the blob of the Reference as a string. */
    public String getblob() {
        return blob;
    }
}
