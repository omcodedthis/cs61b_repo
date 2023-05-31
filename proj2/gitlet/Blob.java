package gitlet;

import java.io.File;
import java.io.Serializable;

/** Represents a Blob object.
 *  Reference is class that has 1 instance variable.
 *  contents: Stores the relevant contents of files.
 *  @author om
 */
public class Blob implements Serializable {
    public File contents;

    public Blob(File cts) {
        contents = cts;
    }
}
