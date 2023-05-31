package gitlet;

import jdk.jshell.execution.Util;

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
                File blobDirectory = Utils.join(GITLET_DIR, "Blobs");

                if (stageVer.exists()) {
                    overwriteStaged(stageVer, userFile);
                } else {
                    stageVer.createNewFile();
                    byte[] contents = readContents(userFile);
                    String hash = sha1(contents);
                    writeContents(stageVer, hash);

                    File blob = Utils.join(blobDirectory, hash);
                    blob.createNewFile();
                    writeContents(blob, serialize(contents));
                }
            } else {
                throw new GitletException("No changes added to the commit.");
            }
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when adding the file.");
        }
    }


    /** Saves a snapshot of tracked files in the current commit and staging
     * area so that they can be restored at a later time, creating a new
     * commit. */
    public static void commit(String msg) {
        try {
            File stageAddFolder = Utils.join(GITLET_DIR, "Stage", "Add");
            File[] addDirectory = stageAddFolder.listFiles();
            File stageRmFolder = Utils.join(GITLET_DIR, "Stage", "Remove");
            File[] rmDirectory = stageRmFolder.listFiles();

            String headCommit = getHead();
            Commit newCommit = new Commit(msg, headCommit);

            if (addDirectory != null) {
                int totalFiles = addDirectory.length;
                for (int i = 0; i < totalFiles; i++) {
                    String filename = addDirectory[i].getName();
                    String blob = readContentsAsString(addDirectory[i]);

                    Reference reference = new Reference(filename, blob);
                    newCommit.references[i] = reference;
                    addDirectory[i].delete();
                }
            } else {
                throw new GitletException("The directory for tracked files(add) returned null.");
            }

            if (rmDirectory != null) {
                int totalFiles = rmDirectory.length;
                for (int j = 0; j < totalFiles; j++) {
                    rmDirectory[j].delete();
                }
            } else {
                throw new GitletException("The directory for tracked files(add) returned null.");
            }

            addThisCommit(newCommit);
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when creating a new commit.");
        }
    }


    /**  Starting at the current head commit, display information about each
     *  commit backwards along the commit tree until the initial commit. */
    public static void log() {
        String commitHash = getHead();
        File CommitFilePointer = Utils.join(GITLET_DIR, "Commits", commitHash);

        while (CommitFilePointer.exists()) {
            Commit currentCommit = readObject(CommitFilePointer, Commit.class);

            printCommitDetails(currentCommit, commitHash);

            commitHash = currentCommit.myParent;

            if (commitHash == null) {
                break;
            }
            CommitFilePointer = Utils.join(GITLET_DIR, "Commits", commitHash);
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
    private static void overwriteStaged(File stageVer, File userFile) throws IOException {
        File blobDirectory = Utils.join(GITLET_DIR, "Blobs");

        String stagehash = readContentsAsString(stageVer);
        byte[] contents = readContents(userFile);
        String userhash = sha1(contents);

        if (stagehash.equals(userhash)) {
            stageVer.delete();

        } else {
            writeContents(stageVer, userhash);

            File blob = Utils.join(blobDirectory, userhash);
            blob.createNewFile();
            writeContents(blob, contents);
        }
    }


    /** Returns the SHA-1 hash of the HEAD commit. */
    private static String getHead() {
        File head = Utils.join(GITLET_DIR, "Commits", "HEAD");
        if (head.exists()) {
            String headHash = readContentsAsString(head);
            return headHash;
        } else {
            throw new GitletException("The HEAD file is missing.");
        }
    }


    /** Writes the Commit object to a file and updates HEAD & Master. */
    private static void addThisCommit(Commit newCommit) throws IOException {
        File commits = Utils.join(GITLET_DIR, "Commits");

        byte[] serialized = serialize(newCommit);
        String hash = sha1(serialized);

        File addCommit = Utils.join(commits, hash);
        addCommit.createNewFile();
        writeContents(addCommit, serialized);

        File head = Utils.join(GITLET_DIR, "Commits", "HEAD");
        writeContents(head, hash);

        File master = Utils.join(GITLET_DIR, "Commits", "Master");
        writeContents(master, hash);
    }


    /** Reads the details from a Commit object & prints it to the terminal. */
    private static void printCommitDetails(Commit currentCommit, String commitHash) {
        System.out.println("===");
        System.out.println("commit " + commitHash);
        System.out.println("Date: " + currentCommit.getDateAndTime());
        System.out.println(currentCommit.getMessage());
        System.out.println();
    }
}
