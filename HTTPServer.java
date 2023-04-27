import java.net.*;
import java.io.*;

public class HTTPServer {
    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            while(true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream();
                String[] current = in.readLine().split(" ");
                String request = current[0];
                String body = "";
                if (request.equals("POST") || request.equals("PUT")) {
                    StringBuffer result = new StringBuffer();
                    result = new StringBuffer();
                    while (in.ready()) {
                        result.append((char) in.read());
                    }
                    String[] lines = result.toString().split("\n");
                    String line = lines[lines.length - 1];
                    body = line.substring(10, line.length() - 2);
                }
                switch (request) {
                    case "GET":
                        get(current[1].substring(1, current[1].length()), out);
                        break;
                    case "POST":
                        post(current[1].substring(1, current[1].length()), body, out);
                        break;
                    case "PUT":
                        put(current[1].substring(1, current[1].length()), body, out);
                        break;
                    case "DELETE":
                        delete(current[1].substring(1, current[1].length()), out);
                        break;
                    case "OPTIONS":
                        options(current[1].substring(1, current[1].length()), out);
                        break;
                    case "HEAD":
                        head(current[1].substring(1, current[1].length()), out);
                        break;

                    default:
                        System.err.println("This HTTP server does support this request");
                }
                in.close();
                clientSocket.close();
                out.close();
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static void get(String fileName, OutputStream out) {
        try {
            File directory = new File("./materials");
            String[] files = directory.list();
            boolean check = false;
            for (String file : files) {
                if (file.equals(fileName)) {
                    check = true;
                }
            }
            if (check) {
                File file = new File("./materials/" + fileName);
                String mimeType = URLConnection.guessContentTypeFromName(fileName);
                String header_1 = "HTTP/1.1 200 OK\r\n";
                String header_2 = "Content-Type: " + mimeType + "\r\n";
                if (mimeType.equals("video/mp4")) {
                    byte[] buffer = new byte[4096];
                    FileInputStream fileInputStream = new FileInputStream(file);
                    String header_3 = "Content-Length: " + file.length() + "\r\n";
                    String response = header_1 + header_2 + header_3 + "\r\n";
                    out.write(response.getBytes());
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.flush();
                    fileInputStream.close();
                } else {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuffer result = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    String content = result.toString();
                    reader.close();
                    String header_3 = "Content-Length: " + file.length() + "\r\n";
                    String response = header_1 + header_2 + header_3 + "\r\n" + content;
        
                    out.write(response.getBytes());
                }
            } else {
                String cat_image_url = "https://http.cat/404";
                String error_cat = "<html><body><img src=" + cat_image_url + "></body></html>";
                String header_1 = "HTTP/1.1 404 Not Found\r\n";
                String header_2 = "Content-Type: text/html\r\n";
                String header_3 = "Content-Length: " + error_cat.length() + "\r\n\r\n";
                String response = header_1 + header_2 + header_3 + error_cat;
                System.out.print(response);
                out.write(response.getBytes());
            }
            out.flush();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static void post(String fileName, String body, OutputStream writer) {
        try {
            File directory = new File("./materials");
            String[] files = directory.list();
            boolean check = false;
            for (String file : files) {
                if (file.equals(fileName)) {
                    check = true;
                }
            }
            if (check) {
                File file = new File("./materials/" + fileName);
                FileWriter fileWriter = new FileWriter(file, true);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                bw.write(body);
                bw.close();
                String header_1 = "HTTP/1.1 200 OK\r\n";
                String header_2 = "Content-Type: application/json\r\n";
                String header_3 = "Content-Length: 42\r\n\r\n";
                String content = "{\"success\": true, \"message\": \"The data was successfully saved.\"}";
                String response = header_1 + header_2 + header_3 + content;
                writer.write(response.getBytes());
            } else {
                String cat_image_url = "https://http.cat/404";
                String error_cat = "<html><body><img src=" + cat_image_url + "></body></html>";
                String header_1 = "HTTP/1.1 404 Not Found\r\n";
                String header_2 = "Content-Type: text/html\r\n";
                String header_3 = "Content-Length: " + error_cat.length() + "\r\n\r\n";
                String response = header_1 + header_2 + header_3 + error_cat;
                writer.write(response.getBytes());
            }
        } catch(Exception error) {
            error.printStackTrace();
        }
    }

    public static void put(String fileName, String body, OutputStream writer) {
        try {
            File directory = new File("./materials");
            String[] files = directory.list();
            boolean check = false;
            for (String file : files) {
                if (file.equals(fileName)) {
                    check = true;
                }
            }
            File file = new File("./materials/" + fileName);
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(body);
            bw.close();
            if (check) {
                String header_1 = "HTTP/1.1 200 OK\r\n";
                String header_2 = "Content-Type: application/json\r\n";
                String content = "{\"success\": true, \"message\": \"File exist content has been overrided\"}";
                String header_3 = "Content-Length: " + content.length() + "\r\n\r\n";

                String response = header_1 + header_2 + header_3 + content;
                writer.write(response.getBytes());
            } else {
                String header_1 = "HTTP/1.1 200 OK\r\n";
                String header_2 = "Content-Type: application/json\r\n";
                String content = "{\"success\": true, \"message\": \"File doesn't exist, new file has been created.\"}";
                String header_3 = "Content-Length: " + content.length() + "\r\n\r\n";

                String response = header_1 + header_2 + header_3 + content;
                writer.write(response.getBytes());
            }
        } catch(Exception error) {
            error.printStackTrace();
        }
    }

    public static void delete(String fileName, OutputStream writer) throws IOException {
        File file = new File("./materials/" + fileName);
        String message;
        boolean check = false;
        if (file.delete()) {
            check = true;
            message = "File is sucessful deleted";
        } else {
            message = "Delete failed, files don't exist";
        }
        if (check) {
            String header_1 = "HTTP/1.1 200 OK\r\n";
            String header_2 = "Content-Type: text/plain\r\n";
            String header_3 = "Content-Length: " + message.length() + "\r\n\r\n";
            String response = header_1 + header_2 + header_3 + message;
            writer.write(response.getBytes());
        } else {
            String cat_image_url = "https://http.cat/404";
            String error_cat = "<html><body><img src=" + cat_image_url + "></body></html>";
            String header_1 = "HTTP/1.1 404 Not Found\r\n";
            String header_2 = "Content-Type: text/html\r\n";
            String header_3 = "Content-Length: " + error_cat.length() + "\r\n\r\n";
            String response = header_1 + header_2 + header_3 + error_cat;
            writer.write(response.getBytes());
        }
    }

    public static void options(String fileName, OutputStream writer) throws IOException {
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        String methods = "";
        if (methods.length() != 0 && mimeType.equals("text/plain")) {
            methods = " ,PUT, POST";
        }
        String header_1 = "HTTP/1.1 200 OK\r\n";
        String header_2 = "Allow: GET, DELETE, OPTIONS, HEAD" + methods + "\r\n";
        String header_3 = "Content-Length: 0";
        String response = header_1 + header_2 + header_3;
        writer.write(response.getBytes());
    }

    public static void head(String fileName, OutputStream writer) throws IOException {
        File file = new File("./materials/" + fileName);
        File directory = new File("./materials");
        String[] files = directory.list();
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        boolean check = false;
        for (String newFile : files) {
            if (newFile.equals(fileName)) {
                check = true;
            }
        }
        if (check) {
            String header_1 = "HTTP/1.1 200 OK\r\n";
            String header_2 = "Content-Type: " + mimeType + "\r\n";
            String header_3 = "Content-Length: " + file.length();
            String response = header_1 + header_2 + header_3;
            writer.write(response.getBytes());
        } else {
            String cat_image_url = "https://http.cat/404";
            String error_cat = "<html><body><img src=" + cat_image_url + "></body></html>";
            String header_1 = "HTTP/1.1 404 Not Found\r\n";
            String header_2 = "Content-Type: text/html\r\n";
            String header_3 = "Content-Length: " + error_cat.length() + "\r\n\r\n";
            String response = header_1 + header_2 + header_3 + error_cat;
            writer.write(response.getBytes());
        }
    }
} 