package gitlet;

import java.io.Serializable;

/** Keeps track of all Commit objects.
 *  master: Points to the default branch when the repository was initialised.
 *  head: Points to the most recent commit.
 *  noOfCommits: Total number of Commit objects.
 *  @author om
 */


public class CommitTracker implements Serializable {

    Commit master;
    Commit head;



}
