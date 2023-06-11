package gitlet;

import java.io.Serializable;

/** Represents a Reference object.
 *  Reference is class that has 2 instance variables.
 *  filename: Filename of the file that is staged for add/rm.
 *  contents: Stores the relevant Blob (contents of files).
 *  @author om
 */
public class Reference implements Serializable {
    private String filename;
    private String blob;


    /** Constructor for the Reference object. */
    public Reference(String fn, String blb) {
        filename = fn;
        blob = blb;
    }

}
