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
        if (GITLET_DIR.exists()) {
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        }
        createFolders();
    }


    /** Creates the required folders to store serialized objects for Gitlet.
     * This system will automatically start with one commit: a commit that
     * contains no files and has the commit message initial commit.  */
    private static void createFolders() {
        try {
            GITLET_DIR.mkdir();

            File stageAddFolder = Utils.join(GITLET_DIR, "Stage", "Add");
            stageAddFolder.mkdir();

            File stageRemoveFolder = Utils.join(GITLET_DIR, "Stage", "Remove");
            stageRemoveFolder.mkdir();

            File commitsFolder = Utils.join(GITLET_DIR, "Commits");
            commitsFolder.mkdir();

            File blobsFolder = Utils.join(GITLET_DIR, "Blobs");
            blobsFolder.mkdir();

            Commit firstCommit = new Commit(null, null);
            byte[] serializedCommit = serialize(firstCommit);
            String shaHash = sha1(serializedCommit);
            File first = Utils.join(commitsFolder, shaHash);
            first.createNewFile();
            writeContents(first, serializedCommit);

            File head = Utils.join(commitsFolder, "HEAD");
            head.createNewFile();
            writeContents(head, shaHash);

            File master = Utils.join(commitsFolder, "Master");
            master.createNewFile();
            writeContents(master, shaHash);
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when setting up the repository.");
        }
    }
}
