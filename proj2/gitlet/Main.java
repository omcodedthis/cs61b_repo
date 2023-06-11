package gitlet;

import java.io.File;

import static gitlet.Utils.join;
import static gitlet.Utils.message;

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
                validateNumArgs(args, 1);
                Repository.setUpPersistence();
                break;

            case "add":
                validateNumArgs(args, 2);
                gitletExists();
                Repository.add(args[1]);
                break;
                
            case "commit":
                validateNumArgs(args, 2);
                gitletExists();
                Repository.commit(args[1]);
                break;

            case "rm":
                validateNumArgs(args, 2);
                gitletExists();
                Repository.remove(args[1]);
                break;

            case "log":
                validateNumArgs(args, 1);
                gitletExists();
                Repository.log();
                break;

            case "global-log":
                validateNumArgs(args, 1);
                gitletExists();
                Repository.glblog();
                break;

            case "find":
                validateNumArgs(args, 2);
                gitletExists();
                Repository.find(args[1]);
                break;

            case "status":
                validateNumArgs(args, 1);
                gitletExists();
                Repository.status();
                break;

            case "checkout":
                validateNumArgs(args, 4);
                gitletExists();
                Repository.checkout(args);
                break;

            case "branch":
                validateNumArgs(args, 2);
                gitletExists();
                Repository.branch(args[1]);
                break;

            case "rm-branch":
                validateNumArgs(args, 2);
                gitletExists();
                Repository.removeBranch(args[1]);
                break;

            case "reset":
                validateNumArgs(args, 2);
                gitletExists();
                Repository.reset(args[1]);
                break;

            case "merge":
                validateNumArgs(args, 2);
                gitletExists();
                break;

            default:
                Utils.message("No command with that name exists.");
                break;
        }
    }

    /** Checks that the number of arguments is not zero, throws
     * an exception if so. */
    public static void checkNotEmpty(String[] args) {
        if (args.length <= 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }
    }


    /** Checks the that the number of arguments entered is
     * valid (n arguments, git + command), throws an exception
     * if it is not. */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length > n) {
            Utils.message("Incorrect operands.");
            System.exit(0);
        }
    }


    /** Checks that a Gitlet Repository exists, exits if it does not exist. */
    private static void gitletExists() {
        File cwd = new File(System.getProperty("user.dir"));
        File gitletDir = join(cwd, ".gitlet");
        if (gitletDir.exists()) {
            return;
        } else {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
