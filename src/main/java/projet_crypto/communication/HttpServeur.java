package projet_crypto.communication;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServeur {
    public static void main(String[] args) {
        try {
            InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 8081);
            HttpServer server = HttpServer.create(address, 0);
            server.createContext("/send", new SendMessageHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            System.out.println("Server started on port 8080.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class SendMessageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            System.out.println("Received message: " + requestBody);

            // Here you can process the message, e.g., save it to a database or file

            String response = "Message received.";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
