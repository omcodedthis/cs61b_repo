package gitlet;

import gitlet.*;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author om
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        checkNotEmpty(args);
        String firstArg = args[0];

        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs(args, 1);
                Repository.setUpPersistence();
                break;

            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs(args, 2);
                Repository.add(args[1]);
                break;

            // TODO: FILL THE REST IN
            case "commit":
                validateNumArgs(args, 2);
                Repository.commit(args[1]);
                break;

            case "rm":
                validateNumArgs(args, 2);
                Repository.remove(args[1]);
                break;

            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;

            case "global-log":
                break;

            case "find":
                break;

            case "status":
                break;

            case "checkout":
                validateNumArgs(args, 4);
                Repository.checkout(args);
                break;

            case "branch":
                break;

            case "rm-branch":
                break;

            case "reset":
                break;

            case "merge":
                break;
        }
    }

    /** Checks that the number of arguments is not zero, throws
     * an exception if so. */
    public static void checkNotEmpty(String[] args) {
        if (args.length <= 0) {
            throw new GitletException("Please enter a command.");
        }
    }


    /** Checks the that the number of arguments entered is
     * valid (2 arguments, git + command), throws an exception
     * if it is not. */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length > n) {
            throw new GitletException("Incorrect operands.");
        }
    }
}
