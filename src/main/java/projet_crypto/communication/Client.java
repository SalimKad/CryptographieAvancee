package projet_crypto.communication;

import projet_crypto.gui.connexion;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.InputStream;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.util.io.Base64;

import javax.swing.*;

public class Client implements Serializable {

    public static ServerResponse sendInitialRequest(String email) {
        ServerResponse serverResponse = null;

        try {
            URL url = new URL("http://localhost:8081/init");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);


            String requestData = "email=" + URLEncoder.encode(email, "UTF-8");
            //System.out.println("l'email envoyé au serveur : " + email);
            OutputStream out = urlConn.getOutputStream();
            out.write(requestData.getBytes());
            out.close();

            int responseCode = urlConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                // Read the data from the server
                String generatorS = reader.readLine();
                String ppubS = reader.readLine();
                String pk = reader.readLine();
                String skS = reader.readLine();

                System.out.println("PK String RECU : " + pk);
                System.out.println("SK String RECU: " + skS);

                // Convert received strings to appropriate data types
                byte[] generatorBytes = Base64.decode(generatorS);
                byte[] ppubBytes = Base64.decode(ppubS);
                String pkString = pk; // No need to decode since it's already a string
                byte[] skBytes = Base64.decode(skS);

                // Create elements from the byte arrays
                Pairing pairing = PairingFactory.getPairing("a.properties");
                Element generator = pairing.getG1().newElementFromBytes(generatorBytes);
                Element ppub = pairing.getG1().newElementFromBytes(ppubBytes);
                Element sk = pairing.getG1().newElementFromBytes(skBytes);

                System.out.println("le sk element transforme recu " + sk);
                System.out.println("le generator element transforme " + generator);
                System.out.println("le ppub element transforme " + ppub);

                // Create the ServerResponse object
                serverResponse = new ServerResponse(generator, ppub, pkString, sk);

                reader.close();
            }


            urlConn.disconnect();
        } catch (
                IOException ex) {
            ex.printStackTrace();
        }

        return serverResponse;
    }

    public static void main(String[] args) {
        /*try {
            // UI natif du système d'exploitation
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // ou pour le look and feel cross-platform de Java (Metal)
            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            // Appliquer le look and feel à toutes les fenêtres ouvertes
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //Locale.setDefault(new Locale("fr", "FR")); // Pour le français, par exemple

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create an instance of the connexion class
                connexion loginWindow = new connexion();
                // Set the window visible
                loginWindow.setVisible(true);
            }
        });


        try {
            URL url = new URL("http://localhost:8081/send");

            while (true) {
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("POST");
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);

                //sendInitialRequest("cryptoprojet4A@outlook.com");


                OutputStream out = urlConn.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter message to send to server (type 'exit' to quit): ");
                String message = reader.readLine();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                out.write(message.getBytes());
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String response = in.readLine();
                System.out.println("Response from server: " + response);

                in.close();
                urlConn.disconnect();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}