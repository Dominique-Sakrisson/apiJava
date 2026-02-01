package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;

public class WebServer {
    public static void main(String[] args) throws IOException {
        int hostPort = Integer.parseInt( System.getenv("API_PORT"));
        HttpServer server = HttpServer.create(new InetSocketAddress(hostPort), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/" + System.getenv("TEST_ROUTE"), new TestHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("server start on http://localhost:" +  System.getenv("API_PORT"));
    }
    //handler for "/"
    static class RootHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "<h1>Successfully connected!</h1>";
            sendResponse(exchange, response);
        }
    }


    static class TestHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Simulate heavy data: 5MB of text
            int sizeMB = 15;
            StringBuilder sb = new StringBuilder(sizeMB * 1024 * 1024);
            for (int i = 0; i < sizeMB * 1024; i++) {
                sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
            }
            String response = "hello from java" + sb;
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
