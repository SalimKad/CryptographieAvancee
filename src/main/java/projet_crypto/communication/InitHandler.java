package projet_crypto.communication;


import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;


import java.io.OutputStream;

import java.io.PrintWriter;

import java.io.Serializable;
import java.net.URLDecoder;


import com.sun.net.httpserver.HttpHandler;


import projet_crypto.IBEBasicIdent;

import projet_crypto.KeyPair;

import projet_crypto.SettingParameters;

import it.unisa.dia.gas.jpbc.Element;

import it.unisa.dia.gas.jpbc.Pairing;

import it.unisa.dia.gas.jpbc.PairingParameters;

import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import it.unisa.dia.gas.plaf.jpbc.util.io.Base64;


import com.sun.net.httpserver.HttpExchange;


class InitHandler implements HttpHandler, Serializable {

    // Déclarer des variables statiques pour stocker generator et ppub
    private static Element generator;
    private static Element ppub;
    static Pairing pairing = PairingFactory.getPairing("a.properties");
    static SettingParameters sp = IBEBasicIdent.setup(pairing);


    // Méthode pour initialiser generator et ppub
    public static void initializeGeneratorAndPpub() {
        try {


            // Assigner les valeurs de generator et ppub
            generator = sp.getP();
            ppub = sp.getP_pub();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override

    public void handle(HttpExchange exchange) throws IOException {

        Element generator = InitHandler.generator;
        Element ppub = InitHandler.ppub;

        // Lire les données de la requête pour extraire l'e-mail du client

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));

        StringBuilder requestData = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {

            requestData.append(line);

        }

        reader.close();


        // Extraire l'e-mail des données de la requête

        String[] requestDataParts = requestData.toString().split("&");

        String email = "";

        for (String part : requestDataParts) {

            String[] keyValue = part.split("=");

            if (keyValue.length == 2 && keyValue[0].equals("email")) {

                email = URLDecoder.decode(keyValue[1], "UTF-8");

                System.out.println("l'email recu 2 : " + email);

                break;


            }

        }


        try {

            KeyPair keys = IBEBasicIdent.keygen(pairing, sp.getMsk(), email);

            System.out.println("l'email utilisé " + email);

            // Envoyer les objets sérialisés et les paramètres de Pairing

            exchange.sendResponseHeaders(200, 0); // Définir les en-têtes de la réponse

          /*  Element generator = sp.getP();

            Element ppub= sp.getP_pub();*/

            String pk = keys.getPk();

            Element sk = keys.getSk();

            System.out.println("PK sent: " + pk);
            System.out.println("SK sent: " + sk);

            System.out.println("generator sent: " + generator);
            System.out.println("ppub sent: " + ppub);


            String generatorS = Base64.encodeBytes(generator.toBytes());

            String ppubS = Base64.encodeBytes(ppub.toBytes());

            String skS = Base64.encodeBytes(sk.toBytes());


            System.out.println("PK sent 2: " + pk);
            System.out.println("SK string sent: " + skS);


            // Envoyer les chaînes de caractères dans le corps de la réponse

            OutputStream responseBody = exchange.getResponseBody();

            PrintWriter writer = new PrintWriter(responseBody);

            writer.println(generatorS); // Envoyer generator

            writer.println(ppubS); // Envoyer ppub

            writer.println(pk); // Envoyer pk

            writer.println(skS); // Envoyer sk

            writer.flush();

            responseBody.close();

        } catch (Exception e) {

            e.printStackTrace();

        }


    }

    public static void main(String[] args) {


    }

}