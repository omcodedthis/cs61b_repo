package gitlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Contains all the relevant helper methods for Repository.java,
 * The helper methods are in alphabetical order. The descriptions
 * for each method is explained in greater depth below (above the
 * respective method signatures).
 *
 *  It has 2 instance variables.
 *
 *  CWD: The user's current working directory.
 *  GITLET_DIR: The directory of the .gitlet file.
 *
 *  @author om
 */

public class HelperMethods {
    /** The current working directory. */
    private static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    private static final File GITLET_DIR = join(CWD, ".gitlet");


    /** Writes the Commit object to a file and updates HEAD & master. */
    protected static void addThisCommit(Commit newCommit) throws IOException {
        File commitsFolder = Utils.join(GITLET_DIR, "Commits");
        newCommit.myParent = getHead();

        byte[] serialized = serialize(newCommit);
        String hash = sha1(serialized);

        File addCommit = Utils.join(commitsFolder, hash);
        addCommit.createNewFile();
        writeContents(addCommit, serialized);

        File head = Utils.join(commitsFolder, "HEAD");
        File currentBranch = Utils.join(commitsFolder, readContentsAsString(head));
        writeContents(currentBranch, hash);
    }


    /** Adds the file to the staging area if applicable. */
    protected static void addFile(File userFile, String filename) throws IOException {
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


    /** Reads & adds the name of the deleted file to deleted_files. */
    protected static void addToDeleted(String filename) {
        File deletedFiles = Utils.join(GITLET_DIR, "Stage", "deleted_files");

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        ArrayList<String> deleted = readObject(deletedFiles, ArrayList.class);

        deleted.add(filename);
        writeObject(deletedFiles, deleted);
    }


    /** Checks that the specified branch exists & removes the branch if as long
     * as it is not the HEAD. */
    protected static void checkAndRemoveBranch(String branchName) {
        File head = Utils.join(GITLET_DIR, "Commits", "HEAD");
        String headBranch = readContentsAsString(head);

        if (headBranch.equals(branchName)) {
            message("Cannot remove the current branch.");
        } else {
            File file =  Utils.join(CWD, "g.txt");
            if (file.exists()) {
                file.delete();
            }

            File branch = Utils.join(GITLET_DIR, "Commits", branchName);

            branch.delete();
        }
    }


    /** Prints the Commit ID if commitMessage is the same as the currentCommit's
     * message. */
    protected static boolean checkCommitMessage(String currentFileName, Commit currentCommit,
        String commitMessage) {
        String cMsg = currentCommit.getMessage();

        if (cMsg.equals(commitMessage)) {
            System.out.println(currentFileName);
            return true;
        } else {
            return false;
        }
    }


    /** Checks whether the file is tracked & unchanged. */
    protected static void checkIfTracked(File userFile) {
        String contents = readContentsAsString(userFile);
        String userFileBlob = sha1(contents);

        String head = getHead();

        File commitFilePointer = Utils.join(GITLET_DIR, "Commits", head);
        if (commitFilePointer.exists()) {

            Commit currentCommit = readObject(commitFilePointer, Commit.class);

            for (Reference x: currentCommit.references) {
                if (x == null) {
                    break;
                }

                if (((x.filename).equals(userFile.getName())) && ((x.blob).equals(userFileBlob))) {
                    System.exit(0);
                }
            }
        }
    }


    /** Checks that the given branch is a valid branch & no files have been
     * staged. */
    protected static void checkForFailureCases(String branchName) {
        File commitsFolder = Utils.join(GITLET_DIR, "Commits");
        File branch = Utils.join(commitsFolder, branchName);

        if (branch.exists()) {
            File stageAddFolder = Utils.join(GITLET_DIR, "Stage", "Add");
            File[] addDirectory = stageAddFolder.listFiles();
            File stageRmFolder = Utils.join(GITLET_DIR, "Stage", "Remove");
            File[] rmDirectory = stageRmFolder.listFiles();


            if ((addDirectory.length > 0) && (rmDirectory.length > 0)) {
                message("You have uncommitted changes.");
                System.exit(0);
            } else {
                File head = Utils.join(commitsFolder, "HEAD");
                String currentBranch = readContentsAsString(head);

                if (currentBranch.equals(branchName)) {
                    message("Cannot merge a branch with itself.");
                    System.exit(0);
                }
            }
        } else {
            message("A branch with that name does not exist.");
            System.exit(0);
        }


    }


    /** Checks for failure cases before proceeding with the reset. */
    protected static void checkForResetFailures(String commitID) {
        File master = Utils.join(GITLET_DIR, "Commits", "master");
        String hash = readContentsAsString(master);

        if (commitID.equals(hash)) {
            message("There is an untracked file in the way; delete it, "
                +"or add and commit it first.");

            System.exit(0);
        }
    }


    /** Takes the version of the file as it exists in the head commit and
     * puts it in the working directory, overwriting the version of the file
     * that’s already there if there is one. */
    protected static void checkout1(String filename) {
        String headCommitHash = getHead();
        File commitFilePointer = Utils.join(GITLET_DIR, "Commits", headCommitHash);

        if (commitFilePointer.exists()) {
            Commit currentCommit = readObject(commitFilePointer, Commit.class);

            for (int i = 0; i <  currentCommit.references.length; i++) {
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
     * given id, and puts it in the working directory, overwriting the
     * version of the file that’s already there if there is one. */
    protected static void checkout2(String commitID, String filename) {
        Commit currentCommit = findCommit(commitID);
        boolean found = false;

        if (currentCommit != null) {
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
    protected static void checkout3(String branch) {
        File branchFile = Utils.join(GITLET_DIR, "Commits", branch);

        if (!branchFile.exists()) {
            message("No such branch exists.");
            return;
        }

        if (isCurrentBranch(branch)) {
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
    protected static void checkoutCommit(Commit currentCommit) {

        for (int i = 0; i < currentCommit.references.length; i++) {
            Reference currentRef = currentCommit.references[i];

            if (currentRef == null) {
                break;
            }

            File filePointer = Utils.join(CWD, currentRef.filename);
            overwriteFile(filePointer, currentRef);
        }
    }


    /** Clears the ArrayList stored in deleted_files. */
    protected static void clearDeleted() {
        File deletedFiles = Utils.join(GITLET_DIR, "Stage", "deleted_files");

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        ArrayList<String> deleted = readObject(deletedFiles, ArrayList.class);

        deleted.clear();
        writeObject(deletedFiles, deleted);
    }


    /** Creates the required folders to store serialized objects for Gitlet.
     * This system will automatically start with one commit: a commit that
     * contains no files and has the commit message initial commit.  */
    protected static void createFolders() {
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
            File deletedFiles = Utils.join(GITLET_DIR, "Stage", "deleted_files");
            deletedFiles.createNewFile();
            writeObject(deletedFiles, deleted);
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when "
            + "setting up the repository.");
        }
    }


    /** Compares the current branch files & given branch files, following
     * the seven steps given in the spec. */
    protected static void compareAndMerge(String branchName, Commit splitPoint,
        TreeMap currentBranchFiles, TreeMap givenBranchFiles) throws IOException {
        ArrayList<String> filesCompared = new ArrayList<>();

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        Set<String> currentBranchKeys = currentBranchFiles.keySet();
        @SuppressWarnings("unchecked")
        Set<String> givenBranchKeys = givenBranchFiles.keySet();

        int noOfConflicts = 0;

        // handles case 1, 2, 3, 4 & 8 (A Cases)
        for (String key: currentBranchKeys) {
            filesCompared.add(key);

            if (givenBranchFiles.containsKey(key)) {
                String currentBlob = (String) currentBranchFiles.get(key);
                String givenBlob = (String) givenBranchFiles.get(key);

                if (currentBlob.equals(givenBlob)) {
                    continue;
                } else {
                    File currentBlobFile = Utils.join(GITLET_DIR, "Blobs", currentBlob);
                    String currentContents = readContentsAsString(currentBlobFile);
                    File givenBlobFile = Utils.join(GITLET_DIR, "Blobs", givenBlob);
                    String givenContents = readContentsAsString(givenBlobFile);

                    String conflictContents = "<<<<<<< HEAD\n" + currentContents
                            + "=======\n" + givenContents + ">>>>>>>\n";

                    noOfConflicts++;

                    File userFile = Utils.join(CWD, key);
                    if (userFile.exists()) {
                        addFile(userFile, key);
                        writeToFile(userFile, conflictContents);
                        continue;
                    } else {
                        continue;
                    }
                }
            } else {
                String currentBlob = (String) currentBranchFiles.get(key);
                File currentBlobFile = Utils.join(GITLET_DIR, "Blobs", currentBlob);
                String currentContents = readContentsAsString(currentBlobFile);

                File userFile = Utils.join(CWD, key);
                if (userFile.exists()) {
                    addFile(userFile, key);
                    writeToFile(userFile, currentContents);
                    continue;
                } else {
                    continue;
                }
            }
        }

        // handles the remaining non-conflict cases (B Cases)
        mergeBCases(splitPoint, filesCompared,
            currentBranchFiles, givenBranchFiles, givenBranchKeys);

        File head = Utils.join(GITLET_DIR, "Commits", "HEAD");

        Repository.commit("Merged " + branchName + " into " + readContentsAsString(head) + '.');
        if (noOfConflicts > 0) {
            message("Encountered a merge conflict.");
        }
    }


    /** Finds & returns the Commit that matches all the characters in the
     * given Commit ID. Returns null if the given Commit ID cannot be
     * found. */
    protected static Commit findCommit(String commitID) {
        File commitsFolder = Utils.join(GITLET_DIR, "Commits");
        File[] commits = commitsFolder.listFiles();

        for (File x: commits) {
            String currentCommitID = x.getName();

            if (currentCommitID.contains(commitID)) {
                File commitFile =  Utils.join(commitsFolder, currentCommitID);
                Commit currentCommit = readObject(commitFile, Commit.class);
                return currentCommit;
            }
        }
        return null;
    }



    /** Finds the split point of the current & given branch. */
    protected static Commit findSplitPoint(String branchName) {
        String headHash = getHead();
        File headCommitFile = Utils.join(GITLET_DIR, "Commits", headHash);

        File branchFile = Utils.join(GITLET_DIR, "Commits", branchName);
        String branchHash = readContentsAsString(branchFile);
        File branchCommitFile = Utils.join(GITLET_DIR, "Commits", branchHash);

        // first case
        ArrayList<String> headCommits = new ArrayList<>();

        while (headCommitFile.exists()) {
            headCommits.add(headCommitFile.getName());

            Commit headCommit = readObject(headCommitFile, Commit.class);

            if (headCommit.myParent == null) {
                break;
            }

            headCommitFile = Utils.join(GITLET_DIR, "Commits", headCommit.myParent);
        }

        while (branchCommitFile.exists()) {
            Commit branchCommit = readObject(branchCommitFile, Commit.class);

            if (headCommits.contains(branchCommitFile.getName())) {
                return branchCommit;
            }

            if (branchCommit.myParent == null) {
                break;
            }

            branchCommitFile = Utils.join(GITLET_DIR, "Commits", branchCommit.myParent);
        }

        // second case
        headCommitFile = Utils.join(GITLET_DIR, "Commits", headHash);

        while (headCommitFile.exists()) {
            headHash = headCommitFile.getName();

            if (headHash.equals(branchHash)) {
                message("Given branch is an ancestor of the current branch.");
                return null;
            }

            Commit headCommit = readObject(headCommitFile, Commit.class);

            headCommitFile = Utils.join(GITLET_DIR, "Commits", headCommit.myParent);
        }

        // third & final case
        checkout3(branchName);
        message("Current branch fast-forwarded.");
        return null;
    }


    /** Returns the SHA-1 hash of the HEAD commit. */
    protected static String getHead() {
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


    /** Returns a TreeMap of all the files & their latest References from
     * the head to the split point of the current branch. */
    protected static TreeMap<String, String> getCurrentBranchFiles(Commit splitPoint) {
        String headHash = getHead();
        File commitFile = Utils.join(GITLET_DIR, "Commits", headHash);
        TreeMap<String, String> currentBranchFiles = new TreeMap<>();

        while (commitFile.exists()) {
            Commit commit = readObject(commitFile, Commit.class);

            if (commit.equals(splitPoint)) {
                break;
            }

            for (Reference x: commit.references) {
                if (x == null) {
                    break;
                }

                if (!currentBranchFiles.containsKey(x.filename)) {
                    currentBranchFiles.put(x.filename, x.blob);
                }
            }

            if (commit.myParent == null) {
                break;
            }

            commitFile = Utils.join(GITLET_DIR, "Commits", commit.myParent);
        }
        return currentBranchFiles;
    }


    /** Returns a TreeMap of all the files & their latest References from
     * the head to the split point of the current branch. */
    protected static TreeMap<String, String> getGivenBranchFiles(Commit splitPoint,
            String branchName) {
        File branch = Utils.join(GITLET_DIR, "Commits", branchName);
        File commitFile = Utils.join(GITLET_DIR, "Commits", readContentsAsString(branch));
        TreeMap<String, String> givenBranchFiles = new TreeMap<>();

        while (commitFile.exists()) {
            Commit commit = readObject(commitFile, Commit.class);

            if (commit.equals(splitPoint)) {
                break;
            }

            for (Reference x: commit.references) {
                if (x == null) {
                    break;
                }

                if (!givenBranchFiles.containsKey(x.filename)) {
                    givenBranchFiles.put(x.filename, x.blob);
                }
            }

            if (commit.myParent == null) {
                break;
            }

            commitFile = Utils.join(GITLET_DIR, "Commits", commit.myParent);
        }
        return givenBranchFiles;
    }


    /** Returns true if the branch is the current branch. */
    protected static boolean isCurrentBranch(String branch) {
        File head = Utils.join(GITLET_DIR, "Commits", "HEAD");
        String headBranch = readContentsAsString(head);

        return headBranch.equals(branch);
    }


    /** Returns true if the given string is a valid SHA-1 hash. */
    protected static boolean isSHA1(String hash) {
        return hash.matches("^[a-fA-F0-9]{40}$");
    }


    /** Handles cases 5, 6 & 7 according to the spec. */
    protected static void mergeBCases(Commit splitPoint,
        List<String> filesCompared, TreeMap currentBranchFiles,
        TreeMap givenBranchFiles, Set<String> givenBranchKeys)
        throws IOException {
        // handles case 5
        for (String key: givenBranchKeys) {
            if (!filesCompared.contains(key)) {

                if ((!currentBranchFiles.containsKey(key)) && (!splitPoint.hasFile(key))) {
                    checkout1(key);
                    continue;
                }

                String givenBlob = (String) givenBranchFiles.get(key);
                File givenBlobFile = Utils.join(GITLET_DIR, "Blobs", givenBlob);
                String currentContents = readContentsAsString(givenBlobFile);

                File userFile = Utils.join(CWD, key);
                if (userFile.exists()) {
                    addFile(userFile, key);
                    writeToFile(userFile, currentContents);
                    continue;
                } else {
                    continue;
                }
            }

            // handles case 6 & 7
            for (int i = 0; i < splitPoint.references.length; i++) {
                if (splitPoint.references[i] == null) {
                    break;
                }

                String filename = splitPoint.references[i].filename;

                if (currentBranchFiles.containsKey(filename)
                        && (!givenBranchFiles.containsKey(filename))) {
                    Repository.remove(filename);
                }

                if (givenBranchFiles.containsKey(filename)
                        && (!currentBranchFiles.containsKey(filename))) {
                    Repository.remove(filename);
                }
            }
        }
    }


    /** Merges the files between the current and given branches, following
     * the rules of merging from the spec. */
    protected static void mergeFiles(Commit splitPoint, String branchName) {
        try {
            TreeMap<String, String> currentBranchFiles = getCurrentBranchFiles(splitPoint);
            TreeMap<String, String> givenBranchFiles = getGivenBranchFiles(splitPoint, branchName);

            compareAndMerge(branchName, splitPoint, currentBranchFiles, givenBranchFiles);
        } catch (IOException e) {
            throw new GitletException("An IOException occured when merging " + branchName + '.');
        }
    }


    /** Overwrites the specified file contents if it exists. */
    protected static void overwriteFile(File filePointer, Reference currentRef) {
        if (filePointer.exists()) {
            File blob = Utils.join(GITLET_DIR, "Blobs", currentRef.blob);
            String contents = readContentsAsString(blob);

            writeToFile(filePointer, contents);
        } else {
            return;
        }
    }


    /** A helper method to add(). It deletes the file in the Stage directory
     * is the same as the file the user wishes to add. If the contents are
     * not the same, the contents of the staged file is overwritten. */
    protected static void overwriteStaged(File stageVer, File userFile) throws IOException {
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
    protected static void printCommitDetails(Commit currentCommit, String commitHash) {
        System.out.println("===");
        System.out.println("commit " + commitHash);
        System.out.println("Date: " + currentCommit.getDateAndTime());
        System.out.println(currentCommit.getMessage());
        System.out.println();
    }


    /** Prints the Branches. The current branch is marked with a '*'. */
    protected static void printBranches() {
        System.out.println("=== Branches ===");

        File commitsDirectory = Utils.join(GITLET_DIR, "Commits");
        File head = Utils.join(commitsDirectory, "HEAD");
        String currentBranch = readContentsAsString(head);

        System.out.println("*" + currentBranch);

        File[] commitsDirList = commitsDirectory.listFiles();

        for (File x: commitsDirList) {
            String filename = x.getName();

            if ((!isSHA1(filename)) && (!(filename).equals(currentBranch))
                    && (!(filename).equals("HEAD"))) {
                System.out.println(filename);
            }
        }

        System.out.println();
    }


    /** Prints the Removed Files. */
    protected static void printRemoved() {
        System.out.println("=== Removed Files ===");

        File deletedFiles = Utils.join(GITLET_DIR, "Stage", "deleted_files");

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        ArrayList<String> deleted = readObject(deletedFiles, ArrayList.class);

        for (String x: deleted) {
            System.out.println(x);
        }

        System.out.println();
    }


    /** Prints the Staged Files. */
    protected static void printStaged() {
        System.out.println("=== Staged Files ===");

        File stageAddFolder = Utils.join(GITLET_DIR, "Stage", "Add");
        File[] addDirectory = stageAddFolder.listFiles();

        if (addDirectory != null) {
            int totalFiles = addDirectory.length;
            for (int i = 0; i < totalFiles; i++) {
                String filename = addDirectory[i].getName();
                System.out.println(filename);
            }
        }

        System.out.println();
    }


    /** Removes the file from deleted_files. */
    protected static void removedFromDeleted(String filename) {
        File deletedFiles = Utils.join(GITLET_DIR, "Stage", "deleted_files");

        // prevents an "[unchecked] unchecked conversion" warning from occurring during compilation.
        @SuppressWarnings("unchecked")
        ArrayList<String> deleted = readObject(deletedFiles, ArrayList.class);

        deleted.remove(filename);
        writeObject(deletedFiles, deleted);
    }


    /** File is staged for removal & removed from the working directory. */
    protected static void stageForRemoval(String filename, String blobID) {
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
            throw new GitletException("An IOException error occured when "
            + "staging the file for removal.");
        }
    }


    /** The writeContents()/writeObject() provided by CS61B staff in Utils
     * did not work as intended as it adds random characters to files
     * when writing content to a file (possibly because it was deprecated).
     * Hence, this a working helper method I came up with which has the same
     * functionality but avoids adding these random characters to the file. */
    protected static void writeToFile(File filePointer, String contents) {
        try {
            FileWriter writer = new FileWriter(filePointer);
            writer.write(contents);
            writer.close();
        } catch (IOException e) {
            throw new GitletException("An IOException error occured when "
            + "writing content to files.");
        }
    }
}
