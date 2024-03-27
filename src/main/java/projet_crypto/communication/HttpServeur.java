package projet_crypto.communication;

import com.sun.net.httpserver.HttpExchange;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;


public class HttpServeur implements Serializable {

	
    private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws IOException {
    	InitHandler.initializeGeneratorAndPpub();
    	
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/send", new MyHandler());
        server.createContext("/init", new InitHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8081.");
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Lire le message envoyé par le client
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String clientMessage = reader.readLine();
            System.out.println("Message received from client: " + clientMessage);

            // Envoyer un message de réponse au client
            String response = "Message received by server: " + clientMessage;
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
