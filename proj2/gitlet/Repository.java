package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

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


    /** Adds a copy of the file as it currently exists to the staging area. */
    public static void add(String fn) {
        try {
            File userFile = Utils.join(CWD, fn);
            if (userFile.exists()) {
                File stageVer = Utils.join(GITLET_DIR, "Stage", "Add", fn);

                if (stageVer.exists()) {
                    overwriteStaged(stageVer, userFile);
                } else {
                    stageVer.createNewFile();
                    byte[] contents = readContents(userFile);
                    String hash = sha1(contents);
                    writeContents(stageVer, hash);
                }
            }
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when adding the file.");
        }
    }

    /** Creates the required folders to store serialized objects for Gitlet.
     * This system will automatically start with one commit: a commit that
     * contains no files and has the commit message initial commit.  */
    private static void createFolders() {
        try {
            GITLET_DIR.mkdir();

            File stageFolder = Utils.join(GITLET_DIR, "Stage");
            stageFolder.mkdir();

            File stageAddFolder = Utils.join(stageFolder, "Add");
            stageAddFolder.mkdir();

            File stageRemoveFolder = Utils.join(stageFolder, "Remove");
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


    /** A helper method to add(). It deletes the file in the Stage directory
     * is the same as the file the user wishes to add. If the contents are
     * not the same, the contents of the staged file is overwritten. */
    private static void overwriteStaged(File stageVer, File userFile) {
        String stagehash = readContentsAsString(stageVer);
        byte[] contents = readContents(userFile);
        String userhash = sha1(contents);
        if (stagehash.equals(userhash)) {
            stageVer.delete();
        } else {
            writeContents(stageVer, userhash);
        }
    }
}
