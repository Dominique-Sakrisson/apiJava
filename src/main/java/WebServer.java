import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WebServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new RootHandler());
        server.createContext("/hello", new HelloHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("server start on http://localhost:8080");
    }
    //handler for "/"
    static class RootHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<h1>Welcome to my Java Web Server!</h1>";
            sendResponse(exchange, response);
        }
    }

    //handler for "/hello"
    static class HelloHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "hello from java";
            sendResponse(exchange, response);
        }
    }

    //helper to send a response
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
