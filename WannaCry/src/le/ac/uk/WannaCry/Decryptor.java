package le.ac.uk.WannaCry;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.nio.file.Path;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;

public class Decryptor {
    public static void main(String[] args) throws Exception {
        String host = args[0];
        String port = args[1];
        String userid = args[2];

        System.out.println("Dear customer, thank you for purchasing this software.");
        System.out.println("We are here to help you recover your files from this horrible attack.");
        System.out.println("Trying to decrypt files...");

        byte[] decryptedAESKeyBytes = null;
        byte[] encryptedAESKey = null;

        try(FileInputStream fis = new FileInputStream("aes.key")){ // lab1
             encryptedAESKey = fis.readAllBytes();
        }

        File keyFile = new File(userid + ".prv");
        byte[] keyFileBytes = Files.readAllBytes(keyFile.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyFileBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey keyPrivate = keyFactory.generatePrivate(spec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPrivate);
        signature.update(userid.getBytes());
        signature.update(encryptedAESKey);
        byte[] signatureBytes = signature.sign();

        try (Socket socket = new Socket(host, Integer.parseInt(port))) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeUTF(userid);
            dos.writeInt(signatureBytes.length);
            dos.write(signatureBytes);
            dos.writeInt(encryptedAESKey.length);
            dos.write(encryptedAESKey);

            decryptedAESKeyBytes = dis.readNBytes(dis.readInt());
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid port number: " + e.getMessage());
            return;
        }

        if (decryptedAESKeyBytes == null) {
            System.out.println("Unfortunately we cannot verify your identity. Please try again, making sure that you have the correct signature key in place and have entered the correct userid.");
            return;
        }

        SecretKeySpec KEY_AES = new SecretKeySpec(decryptedAESKeyBytes, "AES");
        IvParameterSpec Zero_Padding_IV = new IvParameterSpec(new byte[16]);
        Cipher AES_Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        AES_Cipher.init(Cipher.DECRYPT_MODE, KEY_AES, Zero_Padding_IV);

        byte[] encryptedFile = Files.readAllBytes(Path.of("test.txt.cry"));
        byte[] decryptedFile = AES_Cipher.doFinal(encryptedFile);
        
        try(FileOutputStream fos = new FileOutputStream("test.txt")){
            fos.write(decryptedFile);
        }

        System.out.println("Success! Your files have now been recovered!");

    }

}
