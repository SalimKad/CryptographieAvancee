package projet_crypto.communication;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

import java.io.InputStream;

import projet_crypto.KeyPair;
import projet_crypto.SettingParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.util.io.Base64;



public class Client implements Serializable {
	public static ServerResponse sendInitialRequest(String email) {
	    ServerResponse serverResponse = null;

	    try {
	        URL url = new URL("http://localhost:8081/init");
	        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	        urlConn.setRequestMethod("POST");
	        urlConn.setDoOutput(true);

	        String requestData = "email=" + URLEncoder.encode(email, "UTF-8");
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
	            
	            System.out.println("PK RECU : " + pk);
	            System.out.println("SK RECU: " + skS);

	            // Convert received strings to appropriate data types
	            byte[] generatorBytes = Base64.decode(generatorS);
	            byte[] ppubBytes = Base64.decode(ppubS);
	            String pkString = pk; // No need to decode since it's already a string
	            byte[] skBytes = Base64.decode(skS);

	            // Create elements from the byte arrays
	            Pairing pairing = PairingFactory.getPairing("a.properties");
	            Element generator = pairing.getG1().newElementFromBytes(generatorBytes);
	            Element ppub = pairing.getG1().newElementFromBytes(ppubBytes);
	            Element sk = pairing.getZr().newElementFromBytes(skBytes);

	            // Create the ServerResponse object
	            serverResponse = new ServerResponse(generator, ppub, pkString, sk);

	            reader.close();
	        } else {
	            System.err.println("Failed to get data from server. Response code: " + responseCode);
	        }

	        urlConn.disconnect();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return serverResponse;
	}
   
 /*   public static ServerResponse sendInitialRequest(String email) {
        ServerResponse serverResponse = null; // Déclarer la variable à l'extérieur du bloc try-catch

        try {
            URL url = new URL("http://localhost:8081/init");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);

            String requestData = "email=" + URLEncoder.encode(email, "UTF-8");
            OutputStream out = urlConn.getOutputStream();
            out.write(requestData.getBytes());
            out.close();

            int responseCode = urlConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = new InputStream(urlConn.getInputStream());
               
             
             // Lire les chaînes de caractères du serveur
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String generatorS = reader.readLine();
                String ppubS = reader.readLine();
                String pk = reader.readLine();
                String skS = reader.readLine();
               
                System.out.println("le pk recu : " + pk);
                System.out.println("le sks recu : " + skS);
                //System.out.println(skS);
               
             // Convertir les chaînes de caractères en bytes
                byte[] generatorBytes = generatorS.getBytes();
                byte[] ppubBytes = ppubS.getBytes();
                byte[] skBytes = skS.getBytes();
               
               
             // Convertir les chaînes de caractères en éléments de la bibliothèque de couplage
                Pairing pairing = PairingFactory.getPairing("a.properties");
                Element generator = pairing.getG1().newElementFromBytes(generatorBytes);
                Element ppub = pairing.getG1().newElementFromBytes(ppubBytes);
               // Element pk = pairing.getG1().newElementFromBytes(Base64.decode(pkS));
                Element sk = pairing.getZr().newElementFromBytes(skBytes);
               
               
             // Afficher les données reçues depuis le serveur
                //System.out.println("Generator received: " + generator);
                //System.out.println("PPub received: " + ppub);
                System.out.println("PK received: " + pk);
                System.out.println("SK received: " + sk);
               
                // Créer une instance de ServerResponse avec les données reçues du serveur
                serverResponse = new ServerResponse(generator, ppub, pk, sk);
               
                // Créer une instance de ServerResponse avec les données reçues
               /* serverResponse = new ServerResponse();
                serverResponse.setGenerator(generator);
                serverResponse.setPpub(ppub);
                serverResponse.setPk(pk);
                serverResponse.setSk(sk);*/
                //System.out.println("PairingParameters received: " + pairingParams);

                // Créer une instance de Pairing avec les paramètres récupérés
              //  Pairing pairing = PairingFactory.getPairing(pairingParams);
               
                //System.out.println("object pairing from PairingParameters received: " + pairing);

                // Créer une instance de ServerResponse avec les objets récupérés
                //serverResponse = new ServerResponse(pairing, sp, keys);
               
            /*    System.out.println("instance serevrResponse: " +serverResponse);

                is.close();
            } else {
                System.err.println("Failed to get data from server. Response code: " + responseCode);
            }
            urlConn.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return serverResponse;
    } 
*/
    public static void main(String[] args) {
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
