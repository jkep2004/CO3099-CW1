package le.ac.uk.WannaCry;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Server {
    public static void main(String[] args) {
        String port = args[0];

        String masterPrivateKeyBase64 = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCpb1KSHnrdZnLI2dc7PeQrxBmm5lnesjCcWk+qjKYwiIeUsF+8MqUfPDXFYVOohIEuSCM4KsOcUPL8tmjyFMVZ1ejGhIe9mj+3M2RU05uO6Wd5BPLxNd21x0/EcNrkQqNdROOx6WM8HyuCy1T9yIU9v+wVNFZHQE9vRKPQ8M8T/Mdb6aNV8r/eANP5E0B1mlCvrDXpiJCq25GtA5AurCq23UCT87Pj1BdorviY77pAa3J8gLbxmkhe/m/Fhnp9ydvWpuZqi5/pHG9UsAehfV5aL+zZiS6vYDSOGjjUYZe3QdXERCi4LGGjjo8REJRBPUsTV1l618nhZPcvtnpDoFi5AgMBAAECggEAJG8MPeBxAh/QCfGR7yrBYEnkTDfXVmRRwKVI4T8qU2BgazA7/d+UEzHizCVAFIodryKS9DZ55qiff4kH6xpdT5KIYfvIppQ/JDlYV0dDeYkmF2dhiE3RdY+8ztyHrTLJJzqzjaPGk3yOYiswBPxz7lHRRcQB04N4MVB8u2a02LKV1m1U98NTguDJM7btwlILT77+b/aH4w4AbCpF+W42Px8VgXdZYT8R6OBHIW4TX0ollg7IU3xLpordfLzQxkpnTHZp2MsEuJomYsEWHE7E3hk5o5SetxLOiYH+4qGTgpPPctaw1m884cxqM7v6lw8c+4Km8rKaeicltzZk9xBZiQKBgQDpG4XsGjEBCISW0ckgz2HiD2rgksaCkobThSEeXdWBsdKiwPrLJ8x7Wj5MNdBkLTK4WnBFh0IorR8TuLaZNTS6tlihzq0TugX9T/sRV2u8ymv8WjPom7qNGaDVx3X7jO1yOgahZ+nS3Pi8p8p4KopRBCAIA7UMX8eiGiySRc1uVQKBgQC6EwbWI//1PIir/c5J4UfaN1Ma6ze8MYdjn8Gig6h/fKNsiihjUJlHBhbvS/drtCtVBze8zqcpBp17LNLU63rqKMRgLj3oe0uLPkGcxU6ehW+/CSbISO+M1iaGq8xV0OA++pViPC0rw1GxBR0x12WnXfGZW9vmZILJHGdAftlc1QKBgFzjVctOaIaT4VnEANRtLwEIybrxYd74CnfRDfBuzbxfoqKuKYG4uzfQLxDpRMAWbqadiy15h3hz+/U83q5QUFGyhcD8fWVl9CsLqu0vG8RgGQW24AZpv72oH7nwQGhbsOaajrfUk40t9oQejISz1ugPr7/h0kw7Am7q5SXoYML9AoGAM6yFAYMN2fTtn5Xka+/8HEWw9pDL8Z9vkeMTyyX07TKXz9SuUqZXarnuNJZMJ8TpI7vVZsGc+m+z3Dkec7LRd6XKo0s0NhPHbuvjHL5OjwdPeSX5dzWKEPqHyG59WHehTtwYECfWA4lCbn6VFPpUaJ5WMdiowNq411Dj+60+f+ECgYAfyiB9/IWx04DGWgANM3QYkxqwwRnIZ8+WQ4eo+PbXWaoujyYHMLgjvQjnSFLndetUiYKzslXhEf2TyXJxclCouH3GfuXOGbr07pu2z7WItIrY1zwlGECRPjs+MOnTY4VhUa8AR478Rc3Vnw90B2FuWq/U0m6dMteu25JGSOC1nw==";
        // TODO - Construct private key from masterPrivateKeyBase64

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))) {
            Socket socket = serverSocket.accept();
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            while (true) {
                // TODO - receive userid, paymentid (CHECK THIS IS CORRECT AS DECRYPTER DOESNT SPECIFY?), encrypted AES key and signature from client
                String userID = dis.readUTF();
                String paymentID = dis.readUTF();


                System.out.println(userID + ": User connected");

                // TODO - Verify signature using userid and encrypted AES key as content with SHA256withRSA (and public key that identifies user?)

                // TODO - If signature is valid, send decrypted AES key bytes to client and log "UserID: Signature verified. Key decrypted and sent." to console

                // TODO - Else if signature is invalid log "UserID: Signature verification failed." to console and close connection to this client
            }

        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("Error connecting to client: " + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid port number: " + e.getMessage());
            return;
        }
    }
}
