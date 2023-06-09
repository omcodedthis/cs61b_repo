package gitlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
            message("A Gitlet version-control system already exists in the current directory.");
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
                trackThis(fn);

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
                message("File does not exist.");
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

            if ((msg == null) || (msg.equals(""))) {
                message("Please enter a commit message.");
                return;
            }

            if ((addDirectory.length == 0) && (rmDirectory.length == 0)) {
                message("No changes added to the commit.");
                return;
            }

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
                throw new GitletException("The directory for tracked files (Add) returned null.");
            }

            if (rmDirectory != null) {
                int totalFiles = rmDirectory.length;
                for (int j = 0; j < totalFiles; j++) {
                    untrackThis(rmDirectory[j].getName());

                    rmDirectory[j].delete();
                }
                clearDeleted();
            } else {
                throw new GitletException("The directory for tracked files(add) returned null.");
            }

            addThisCommit(newCommit);
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when creating a new commit.");
        }
    }


    /**  Unstages the file if it is currently staged for addition. If the file
     *  is tracked in the current commit, it is staged for removal and removed
     *  from the working directory if the user has not already done so. */
    public static void remove(String filename) {
        File toBeRemovedStaged = Utils.join(GITLET_DIR, "Stage", "Add", filename);

        if (toBeRemovedStaged.exists()) {
            toBeRemovedStaged.delete();
        }

        String commitHash = getHead();
        File commitFilePointer = Utils.join(GITLET_DIR, "Commits", commitHash);
        if (commitFilePointer.exists()) {
            Commit currentCommit = readObject(commitFilePointer, Commit.class);

            for (Reference x: currentCommit.references) {
                if (x == null) {
                    break;
                }

                if (filename.equals(x.filename)) {
                    stageForRemoval(filename, x.blob);
                    return;
                }
            }
        }
        message("No reason to remove the file.");
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


    /** Like log, except displays information about all commits ever made. */
    public static void glblog() {
        File commitsFolder = Utils.join(GITLET_DIR, "Commits");
        File[] commitsDirectory = commitsFolder.listFiles();

        for (int i = 0; i < commitsDirectory.length; i++) {
            String currentFileName = commitsDirectory[i].getName();

            if (isSHA1(currentFileName)) {
                Commit currentCommit = readObject(commitsDirectory[i], Commit.class);
                printCommitDetails(currentCommit, currentFileName);
            }
        }
    }


    /** Prints out the ids of all commits that have the given commit message,
     * one per line. */
    public static void find(String commitMessage) {
        File commitsFolder = Utils.join(GITLET_DIR, "Commits");
        File[] commitsDirectory = commitsFolder.listFiles();
        boolean found = false;

        for (int i = 0; i < commitsDirectory.length; i++) {
            String currentFileName = commitsDirectory[i].getName();

            if (isSHA1(currentFileName)) {
                Commit currentCommit = readObject(commitsDirectory[i], Commit.class);
                boolean outcome = checkCommitMessage(currentFileName, currentCommit, commitMessage);

                if (outcome) {
                    found = true;
                }
            }
        }

        if (!found) {
            message("Found no commit with that message.");
        }
    }


    /** Displays what branches currently exist, and marks the current branch
     *  with a '*'. Also displays what files have been staged for addition or
     *  removal. */
    public static void status() {
        printBranches();

        printStaged();

        printRemoved();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
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


    /** Creates a new branch with the given name, and points it at the current
     *  head commit. */
    public static void branch(String branchName) {
        try {
            File commitsFolder = Utils.join(GITLET_DIR, "Commits");
            File branch = Utils.join(commitsFolder, branchName);

            if (branch.exists()) {
                message("A branch with that name already exists.");
            } else {
                branch.createNewFile();
                String hash = getHead();
                writeContents(branch, hash);
            }
        } catch (IOException e) {
            throw new GitletException("An error occured when creating this branch.");
        }
    }


    /** Deletes the branch with the given name. This only means to delete
     *  the pointer associated with the branch. */
    public static void removeBranch(String branchName) {
        File commitsFolder = Utils.join(GITLET_DIR, "Commits");
        File branch = Utils.join(commitsFolder, branchName);

        if (branch.exists()) {
            checkAndRemoveBranch(branchName);
        } else {
            message("A branch with that name does not exist.");
        }
    }


    /**  Checks out all the files tracked by the given commit. Removes
     * tracked files that are not present in that commit. Also moves the
     * current branch’s head to that commit node. */
    public static void reset(String commitID) {
        File commitFilePointer = Utils.join(GITLET_DIR, "Commits", commitID);

        if (commitFilePointer.exists()) {
            Commit currentCommit = readObject(commitFilePointer, Commit.class);
            checkoutCommit(currentCommit);
        } else {
            message("No commit with that id exists.");
        }
    }



    /* HELPER METHODS (In Alphabetical Order) */


    /** Writes the Commit object to a file and updates HEAD & master. */
    private static void addThisCommit(Commit newCommit) throws IOException {
        File commits = Utils.join(GITLET_DIR, "Commits");

        byte[] serialized = serialize(newCommit);
        String hash = sha1(serialized);

        File addCommit = Utils.join(commits, hash);
        addCommit.createNewFile();
        writeContents(addCommit, serialized);

        File master = Utils.join(GITLET_DIR, "Commits", "master");
        writeContents(master, hash);
    }


    /** Reads & adds the name of the deleted file to DELETED_FILES. */
    private static void addToDeleted(String filename) {
        File deletedFiles = Utils.join(GITLET_DIR, "Stage", "Deleted_Files");

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        ArrayList<String> deleted = readObject(deletedFiles, ArrayList.class);

        deleted.add(filename);
        writeObject(deletedFiles, deleted);
    }


    /** Checks that the specified branch exists & removes the branch if as long
     * as it is not the HEAD. */
    private static void checkAndRemoveBranch(String branchName) {
        File head = Utils.join(GITLET_DIR, "Commits", "HEAD");
        String headBranch = readContentsAsString(head);

        if (headBranch.equals(branchName)) {
            message("Cannot remove the current branch.");
        } else {
            writeToFile(head, "master");

            File branch = Utils.join(GITLET_DIR, "Commits", branchName);
            branch.delete();
        }
    }


    /** Prints the Commit ID if commitMessage is the same as the currentCommit's
     * message. */
    private static boolean checkCommitMessage(String currentFileName, Commit currentCommit, String commitMessage) {
        String cMsg = currentCommit.getMessage();

        if (cMsg.equals(commitMessage)) {
            System.out.println(currentFileName);
            return true;
        } else {
            return false;
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

                if (currentRef == null) {
                    break;
                }

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
        boolean found = false;

        if (commitFilePointer.exists()) {
            Commit currentCommit = readObject(commitFilePointer, Commit.class);

            for (int i = 0; i < currentCommit.references.length; i++) {
                Reference currentRef = currentCommit.references[i];

                if (currentRef == null) {
                    break;
                }

                if ((currentRef.filename).equals(filename)) {
                    File filePointer = Utils.join(CWD, filename);
                    overwriteFile(filePointer, currentRef);
                    found = true;
                    return;
                }
            }

            if (!found) {
                message("File does not exist in that commit.");
                return;
            }

        } else {
            message("No commit with that id exists.");
        }
    }


    /** Takes all files in the commit at the head of the given branch, and
     * puts them in the working directory, overwriting the versions of the
     * files that are already there if they exist. Also, at the end of this
     * command, the given branch will now be considered the current
     * branch (HEAD). */
    private static void checkout3(String branch) {
        File branchFile = Utils.join(GITLET_DIR, "Commits", branch);

        if (!branchFile.exists()) {
            message("No such branch exists.");
            return;
        }

        if (currentBranch(branch)) {
            message("No need to checkout the current branch.");
            return;
        }

        String commitID = readContentsAsString(branchFile);
        File commitFilePointer = Utils.join(GITLET_DIR, "Commits", commitID);

        if (commitFilePointer.exists()) {
            Commit currentCommit = readObject(commitFilePointer, Commit.class);

            checkoutCommit(currentCommit);

            File head = Utils.join(GITLET_DIR, "Commits", "HEAD");
            writeToFile(head, branch);
        }
    }


    /** Checks out the commit as per the 3rd Checkout scenario (branch) as given
     * in the spec. */
    private static void checkoutCommit(Commit currentCommit) {
        for (int i = 0; i < currentCommit.references.length; i++) {
            Reference currentRef = currentCommit.references[i];

            if (currentRef == null) {
                continue;
            }

            if (isTracked(currentRef.filename)) {
                message("There is an untracked file in the way; delete it, or add and commit it first.");
            }

            File filePointer = Utils.join(CWD, currentRef.filename);
            overwriteFile(filePointer, currentRef);
        }
    }


    /** Clears the ArrayList stored in Deleted_Files. */
    private static void clearDeleted() {
        File deletedFiles = Utils.join(GITLET_DIR, "Stage", "Deleted_Files");

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        ArrayList<String> deleted = readObject(deletedFiles, ArrayList.class);

        deleted.clear();
        writeObject(deletedFiles, deleted);
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
            writeToFile(head, "master");

            File master = Utils.join(commitsFolder, "master");
            master.createNewFile();
            writeContents(master, shaHash);

            ArrayList<String> deleted = new ArrayList<>();
            File deletedFiles = Utils.join(GITLET_DIR, "Stage", "Deleted_Files");
            deletedFiles.createNewFile();
            writeObject(deletedFiles, deleted);

            ArrayList<String> tracked = new ArrayList<>();
            File trackedFiles = Utils.join(GITLET_DIR, "Tracked", "Tracked_Files");
            trackedFiles.createNewFile();
            writeObject(trackedFiles, tracked);
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when setting up the repository.");
        }
    }


    /** Returns true if the branch is the current branch. */
    private static boolean currentBranch(String branch) {
        File head = Utils.join(GITLET_DIR, "Commits", "HEAD");
        String headBranch = readContentsAsString(head);

        if (headBranch.equals(branch)) {
            message("No need to checkout the current branch.");
            return true;
        } else {
            return false;
        }
    }


    /** Returns the SHA-1 hash of the HEAD commit. */
    private static String getHead() {
        File head = Utils.join(GITLET_DIR, "Commits", "HEAD");
        if (head.exists()) {
            String headBranch = readContentsAsString(head);

            File headBranchFile = Utils.join(GITLET_DIR, "Commits", headBranch);
            String headHash = readContentsAsString(headBranchFile);

            return headHash;
        } else {
            throw new GitletException("The HEAD file is missing.");
        }
    }


    /** Returns true if the given string is a valid SHA-1 hash. */
    private static boolean isSHA1(String hash) {
        return hash.matches("^[a-fA-F0-9]{40}$");
    }


    /** Adds the files to Tracked_Files. */
    private static boolean isTracked(String filename) {
        File trackedFiles = Utils.join(GITLET_DIR, "Tracked", "Tracked_Files");

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        ArrayList<String> tracked = readObject(trackedFiles, ArrayList.class);

        for(String x: tracked) {
            if (x.equals(filename)) {
                return true;
            }
        }
        return false;
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


    /** Reads the details from a Commit object & prints it to the terminal. */
    private static void printCommitDetails(Commit currentCommit, String commitHash) {
        System.out.println("===");
        System.out.println("commit " + commitHash);
        System.out.println("Date: " + currentCommit.getDateAndTime());
        System.out.println(currentCommit.getMessage());
        System.out.println();
    }


    /** Prints the Branches. The current branch is marked with a '*'. */
    private static void printBranches() {
        System.out.println("=== Branches ===");

        File commitsDirectory = Utils.join(GITLET_DIR, "Commits");
        File head = Utils.join(commitsDirectory, "HEAD");
        String currentBranch = readContentsAsString(head);

        System.out.println("*" + currentBranch);

        File[] commitsDirList = commitsDirectory.listFiles();

        for (File x: commitsDirList) {
            String filename = x.getName();

            if ((!isSHA1(filename)) && (!(filename).equals(currentBranch)) && (!(filename).equals("HEAD"))) {
                System.out.println(filename);
            }
        }

        System.out.println();
    }


    /** Prints the Removed Files. */
    private static void printRemoved() {
        System.out.println("=== Removed Files ===");

        File deletedFiles = Utils.join(GITLET_DIR, "Stage", "Deleted_Files");

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        ArrayList<String> deleted = readObject(deletedFiles, ArrayList.class);

        for (String x: deleted) {
            System.out.println(x);
        }

        System.out.println();
    }


    /** Prints the Staged Files. */
    private static void printStaged() {
        System.out.println("=== Staged Files ===");

        File stageAddFolder = Utils.join(GITLET_DIR, "Stage", "Add");
        File[] addDirectory = stageAddFolder.listFiles();
        File stageRmFolder = Utils.join(GITLET_DIR, "Stage", "Remove");
        File[] rmDirectory = stageRmFolder.listFiles();

        if (addDirectory != null) {
            int totalFiles = addDirectory.length;
            for (int i = 0; i < totalFiles; i++) {
                String filename = addDirectory[i].getName();
                System.out.println(filename);
            }
        }

        if (rmDirectory != null) {
            int totalFiles = rmDirectory.length;
            for (int j = 0; j < totalFiles; j++) {
                String filename = rmDirectory[j].getName();
                System.out.println(filename);
            }
        }

        System.out.println();
    }


    /** File is staged for removal & removed from the working directory. */
    private static void stageForRemoval(String filename, String blobID) {
        try {
            File stageRmDirectory = Utils.join(GITLET_DIR, "Stage", "Remove");

            File stageForRm = Utils.join(stageRmDirectory, filename);
            stageForRm.createNewFile();
            writeToFile(stageForRm, blobID);

            File userFile = Utils.join(CWD, filename);

            addToDeleted(filename);
            if (userFile.exists()) {
                userFile.delete();
            }
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when staging the file for removal.");
        }
    }


    /** Adds the files to Tracked_Files. */
    private static void trackThis(String filename) {
        File trackedFiles = Utils.join(GITLET_DIR, "Tracked", "Tracked_Files");

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        ArrayList<String> tracked = readObject(trackedFiles, ArrayList.class);

        tracked.add(filename);

        writeObject(trackedFiles, tracked);
    }


    /** Untracks the given file from Tracked_Files. */
    private static void untrackThis(String filename) {
        File trackedFiles = Utils.join(GITLET_DIR, "Tracked", "Tracked_Files");

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        ArrayList<String> tracked = readObject(trackedFiles, ArrayList.class);

        tracked.remove(filename);

        writeObject(trackedFiles, tracked);
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
