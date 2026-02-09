package le.ac.uk.WannaCry;

import java.io.*;
import java.net.Socket;

public class Decryptor {
    public static void main(String[] args) {
        String host = args[0];
        String port = args[1];
        String userid = args[2];

        // TODO - Generate signature using userid and AES key as content with SHA256withRSA (and public or private key that identifies user?)
        byte[] decryptedAESKeyBytes = null;
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

        // TODO - Decrypt test.txt.cry using the decrypted AES key and write to test.txt


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
