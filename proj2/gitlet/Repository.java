package gitlet;

import java.io.File;
import java.io.IOException;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author om
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");



    /* TODO: fill in the rest of this class. */
    public static void setUpPersistence() {
        try {
            if (GITLET_DIR.exists()) {
                throw new GitletException("A Gitlet version-control system already exists in the current directory.");
            }

            GITLET_DIR.mkdir();
            File commitsFile = Utils.join(GITLET_DIR, "commits");
            commitsFile.createNewFile();


        } catch (IOException e) {
            throw new GitletException("An IOException error occured when setting up the repository.");
        }
    }

}
