import java.net.InetSocketAddress;
import java.io.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HTTPServer {
    public static void main(String[] args) throws Exception {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/get", new HandleGet());
            server.start();
    }

    static class HandleGet implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "weifan is very handsome!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}