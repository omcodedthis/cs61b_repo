package gitlet;

import java.io.File;
import java.io.FileWriter;
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
                    String contents = readContentsAsString(userFile);
                    String hash = sha1(contents);
                    writeToFile(stageVer, hash);

                    File blob = Utils.join(blobDirectory, hash);
                    blob.createNewFile();

                    writeToFile(blob, contents);

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


    /** Starting at the current head commit, display information about each
     *  commit backwards along the commit tree until the initial commit. */
    public static void log() {
        String commitHash = getHead();
        File commitFilePointer = Utils.join(GITLET_DIR, "Commits", commitHash);

        while (commitFilePointer.exists()) {
            Commit currentCommit = readObject(commitFilePointer, Commit.class);

            printCommitDetails(currentCommit, commitHash);

            commitHash = currentCommit.myParent;

            if (commitHash == null) {
                break;
            }
            commitFilePointer = Utils.join(GITLET_DIR, "Commits", commitHash);
        }
    }


    /** Checks out files depending on what its arguments are with 3 possible
     * use cases. */
    public static void checkout(String[] args) {
        if (args.length == 3) {
            checkout1(args[2]);
        } else if (args.length == 4) {
            checkout2(args[1], args[3]);
        } else if (args.length == 2) {
            checkout3(args[1]);
        }
    }


    /** Takes the version of the file as it exists in the head commit and
     * puts it in the working directory, overwriting the version of the file
     * that’s already there if there is one. */
    private static void checkout1(String filename) {
        String headCommitHash = getHead();
        File commitFilePointer = Utils.join(GITLET_DIR, "Commits", headCommitHash);

        if (commitFilePointer.exists()) {
            Commit currentCommit = readObject(commitFilePointer, Commit.class);

            for (int i = 0; i < currentCommit.references.length; i++) {
                Reference currentRef = currentCommit.references[i];

                if ((currentRef.filename).equals(filename)) {
                    File filePointer = Utils.join(CWD, filename);
                    overwriteFile(filePointer, currentRef);
                    return;
                }
            }
        }
    }


    /** Takes the version of the file as it exists in the commit with the
     *  given id, and puts it in the working directory, overwriting the
     *  version of the file that’s already there if there is one. */
    private static void checkout2(String commitID, String filename) {
        File commitFilePointer = Utils.join(GITLET_DIR, "Commits", commitID);

        if (commitFilePointer.exists()) {
            Commit currentCommit = readObject(commitFilePointer, Commit.class);

            for (int i = 0; i < currentCommit.references.length; i++) {
                Reference currentRef = currentCommit.references[i];

                if ((currentRef.filename).equals(filename)) {
                    File filePointer = Utils.join(CWD, filename);
                    overwriteFile(filePointer, currentRef);
                    return;
                }
            }
        }
    }


    /** Takes the version of the file as it exists in the commit with the
     *  given id, and puts it in the working directory, overwriting the
     *  version of the file that’s already there if there is one. */
    private static void checkout3(String branch) {
        File branchFile = Utils.join(GITLET_DIR, "Commits", branch);
        String commitID = null;

        if (branchFile.exists()) {
            commitID = readContentsAsString(branchFile);
        }

        File commitFilePointer = Utils.join(GITLET_DIR, "Commits", commitID);

        if (commitFilePointer.exists()) {
            Commit currentCommit = readObject(commitFilePointer, Commit.class);

            for (int i = 0; i < currentCommit.references.length; i++) {
                Reference currentRef = currentCommit.references[i];

                if (currentRef == null) {
                    continue;
                }
                File filePointer = Utils.join(CWD, currentRef.filename);
                overwriteFile(filePointer, currentRef);
            }
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
        String contents = readContentsAsString(userFile);
        String userhash = sha1(contents);

        if (stagehash.equals(userhash)) {
            stageVer.delete();

        } else {
            writeToFile(stageVer, userhash);

            File blob = Utils.join(blobDirectory, userhash);
            blob.createNewFile();

            writeToFile(blob, contents);
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


    /** Overwrites the specified file contents if it exists. */
    private static void overwriteFile(File filePointer, Reference currentRef) {
        try {
            if (filePointer.exists()) {
                File blob = Utils.join(GITLET_DIR, "Blobs", currentRef.blob);
                String contents = readContentsAsString(blob);

                writeToFile(filePointer, contents);
            } else {
                filePointer.createNewFile();
                File blob = Utils.join(GITLET_DIR, "Blobs", currentRef.blob);
                String contents = readContentsAsString(blob);

                writeToFile(filePointer, contents);
            }
        } catch (IOException e) {
            throw new GitletException("An IOException error occured during checkout.");
        }
    }


    /** The writeContents()/writeObject() provided by CS61B staff in Utils
     *  did not work as intended as it adds random characters to files
     *  when writing content to a file (possibly because it was deprecated).
     *  Hence, this a working helper method I came up with which has the same
     *  functionality but avoids adding these random characters to the file. */
    private static void writeToFile(File filePointer, String contents) {
        try {
            FileWriter writer = new FileWriter(filePointer);
            writer.write(contents);
            writer.close();
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when writing content to files.");
        }
    }
}
