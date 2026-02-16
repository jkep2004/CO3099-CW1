package le.ac.uk.WannaCry;
import java.io.*; // uncessary? not sure. 
import java.net.Socket;
// Zara Imports
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
    public static void main(String[] args) throws Exception{
        String host = args[0];
        String port = args[1];
        String userid = args[2];

        byte[] decryptedAESKeyBytes = null; // what the server sends back
        byte[] Encrypted_AES_Key = null; // read from aes.key

        try(FileInputStream FIS = new FileInputStream("aes.key")){ // lab1
             Encrypted_AES_Key = FIS.readAllBytes();
        }

        // Lab 3. 
        File File_For_Key = new File(userid + ".prv");
        byte[] Byte_From_Key = Files.readAllBytes(File_For_Key.toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Byte_From_Key);
        KeyFactory Factory_Key = KeyFactory.getInstance("RSA");
        PrivateKey Key_Private = Factory_Key.generatePrivate(spec);

        // From Lab 4. Signature generation.
        Signature Our_Signature = Signature.getInstance("SHA256withRSA");
        Our_Signature.initSign(Key_Private);
        Our_Signature.update(userid.getBytes());
        Our_Signature.update(Encrypted_AES_Key);
        byte[] signature = Our_Signature.sign();

        try (Socket socket = new Socket(host, Integer.parseInt(port))) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // TODO - send userid, (payment ID? specified in server), encrypted AES key and signature to socket
            dos.writeUTF(userid);


            decryptedAESKeyBytes = dis.readAllBytes();
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid port number: " + e.getMessage());
            return;
        }

        if (decryptedAESKeyBytes == null) {
            String unsuccessfulMessage = """
                Dear customer, thank you for purchasing this software.
                We are here to help you recover your files from this horrible attack.
                Trying to decrypt files...
                Unfortunately we cannot verify your identity. Please try again, making sure that you have the correct signature key in place and have entered the correct userid.
                """;
            printMessageByLineWithDelay(unsuccessfulMessage);
            return;
        }

        //Decrypt test.txt.cry using the decrypted AES key and write to test.txt. 

        SecretKeySpec KEY_AES = new SecretKeySpec(decryptedAESKeyBytes, "AES");
        IvParameterSpec Zero_Padding_IV = new IvParameterSpec(new byte[16]);
        Cipher AES_Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        AES_Cipher.init(Cipher.DECRYPT_MODE, KEY_AES, Zero_Padding_IV);

        // Read encrypted file and decrypt.
        byte[] encryptedFile = Files.readAllBytes(Path.of("test.txt.cry"));
        byte[] decryptedFile = AES_Cipher.doFinal(encryptedFile);
        
        // write to test.txt
        try(FileOutputStream fos = new FileOutputStream("test.txt")){
            fos.write(decryptedFile);
        }



        String successMessage = """
                Dear customer, thank you for purchasing this software.
                We are here to help you recover your files from this horrible attack.
                Trying to decrypt files...
                Success! Your files have now been recovered!
                """;
        printMessageByLineWithDelay(successMessage);

    }

    static void printMessageByLineWithDelay(String message) {
        for (String line : message.split("\n")) {
            System.out.println(line);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
