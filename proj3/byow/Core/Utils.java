package byow.Core;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;


/** Assorted utilities.
 *
 * Give this file a good read as it provides several useful utility functions
 * to save you some time.
 *
 *  @author P. N. Hilfinger
 */
class Utils {

    /** Reads a String from a file. */
    protected static String readData(File filePointer) throws IOException {
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get("world_save.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }


    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }


    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /* SERIALIZATION UTILITIES */

    /** Returns a byte array containing the serialized contents of OBJ. */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            System.out.println("save1");
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);

            objectStream.writeObject(obj);

            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw new IllegalArgumentException("Internal error serializing commit.");
        }
    }

}
