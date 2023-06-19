package gitlet;

import java.io.File;
import java.io.IOException;
import static gitlet.HelperMethods.*;
import static gitlet.Utils.*;

/** Represents a gitlet repository, handling every command called by Main.
 *  The descriptions for each method is explained in greater depth below
 *  (above the respective method signatures).
 *
 *  It has 2 instance variables.
 *
 *  CWD: The user's current working directory.
 *  GITLET_DIR: The directory of the .gitlet file.
 *
 *  @author om
 */
public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");


    /** Checks that a Gitlet version-control system does not exist & sets
     * up the required file system. */
    public static void setUpPersistence() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
        }
        createFolders();
    }


    /** Adds a copy of the file as it currently exists to the staging area. */
    public static void add(String filename) {
        try {
            File userFile = Utils.join(CWD, filename);

            if (userFile.exists()) {
                // checkIfTracked(userFile);
                addFile(userFile, filename);
            } else {
                message("File does not exist.");
            }
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when adding the file.");
        }
    }

    /** testing */
    protected static void addFile(File userFile, String filename) throws IOException {
        File stageRm = Utils.join(GITLET_DIR, "Stage", "Remove", filename);
        if (stageRm.exists()) {
            stageRm.delete();
            removedFromDeleted(filename);
            return;
        }

        File stageVer = Utils.join(GITLET_DIR, "Stage", "Add", filename);
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


    /** Unstages the file if it is currently staged for addition. If the file
     *  is tracked in the current commit, it is staged for removal and removed
     *  from the working directory if the user has not already done so. */
    public static void remove(String filename) {
        File toBeRemovedStaged = Utils.join(GITLET_DIR, "Stage", "Add", filename);

        if (toBeRemovedStaged.exists()) {
            toBeRemovedStaged.delete();
            return;
        }

        String commitHash = getHead();
        File commitFilePointer = Utils.join(GITLET_DIR, "Commits", commitHash);
        while (commitFilePointer.exists()) {
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

            if (currentCommit.myParent == null) {
                break;
            }

            commitFilePointer = Utils.join(GITLET_DIR, "Commits", currentCommit.myParent);
        }
        message("No reason to remove the file.");
        System.exit(0);
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
     * with a '*'. Also displays what files have been staged for addition or
     * removal. */
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
     * use cases. Also ensures that for checkout1 & checkout2, the argument
     * of index 1 is "--" as per the spec. */
    public static void checkout(String[] args) {
        if (args.length == 3) {
            if (args[1].equals("--")) {
                checkout1(args[2]);
                return;
            }
            message("Incorrect operands.");
            System.exit(0);

        } else if (args.length == 4) {
            if (args[2].equals("--")) {
                checkout2(args[1], args[3]);
                return;
            }
            message("Incorrect operands.");
            System.exit(0);

        } else if (args.length == 2) {
            checkout3(args[1]);
        }
    }


    /** Creates a new branch with the given name, and points it at the current
     * head commit. */
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
     * the pointer associated with the branch. */
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
        Commit currentCommit = findCommit(commitID);

        if (currentCommit != null) {
            checkoutCommit(currentCommit);
        } else {
            message("No commit with that id exists.");
        }
    }


    /** Merges files from the given branch into the current branch. */
    public static void merge(String branchName) {
        checkForFailureCases(branchName);
        Commit splitPoint = findSplitPoint(branchName);

        if (splitPoint == null) {
            return;
        }

        mergeFiles(splitPoint, branchName);
    }
}
