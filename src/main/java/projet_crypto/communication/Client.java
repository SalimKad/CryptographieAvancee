package projet_crypto.communication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://192.168.1.50:8080/sendmessage");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "text/plain");

            String message = "Email content goes here...";
            byte[] messageBytes = message.getBytes("UTF-8");

            OutputStream out = urlConn.getOutputStream();
            out.write(messageBytes);
            out.flush();
            out.close();

            int responseCode = urlConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent successfully!");
            } else {
                System.err.println("Failed to send message. Response code: " + responseCode);
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
