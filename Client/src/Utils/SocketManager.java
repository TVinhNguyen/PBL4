package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import Controller.Client;
import Controller.UserService;
import Manager.NewManager;
import Manager.ProductManager;
import Model.New;
import Model.Product;
import Model.UserAccount;

public class SocketManager extends Thread {
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    public static String responseServer = "";
    public UserService userService;
    public ProductManager productManager;
    private volatile boolean running = true; 
    private Client client;
		
    public SocketManager() {
        try {
            socket = new Socket("localhost", 12345);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userService = new UserService(output , input);
    		client = Client.getInstance();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String response;
        try {
            while (running && (response = input.readLine()) != null) {
                System.out.println("Server response: " + response);
                responseServer = response;
                handleCommand(responseServer);
                
            }
        } catch (IOException e) {
            System.out.println("Error reading from server: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    public void sendMessage(String message) {
        if (output != null) {
            output.println(message);
        }
    }

    public void close() {
        running = false; 
        try {
            if (socket != null) socket.close();
            System.out.println("Closed connection");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void handleCommand(String command) {
        String[] parts = command.split("-");
        String action = parts[0];

        switch (action) {
        case "Account" : 
        	String user = parts[1];
		    client.setUser(UserAccount.fromString(user));
		    break;
        case "LIST_PRODUCT":
            	 String jsonProduct = parts[1];
                 parseJsonToProducts(jsonProduct);
            
             break;
        case "LIST_NEW":
        	String jsonNew = parts[1];
        	
        	break;
        }
    }
    public void  parseJsonToProducts(String json) {
      

        json = json.trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1); // Loại bỏ dấu [ ]
            String[] productJsons = json.split("\\},\\{");

            for (String productJson : productJsons) {
                productJson = productJson.replaceAll("^\\{", "").replaceAll("\\}$", ""); // Loại bỏ dấu { }
                String[] fields = productJson.split(",");

                int idProduct = 0;
                String nameProduct = "";
                double priceProduct = 0.0;
                byte[] imageProduct = null;
                int quantityProduct = 0;
                boolean statusProduct = false;
                int idCategory = 0;

                for (String field : fields) {
                    String[] keyValue = field.split(":", 2);
                    if (keyValue.length != 2) continue;

                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim();

                    switch (key) {
                        case "idProduct":
                            idProduct = Integer.parseInt(value);
                            break;
                        case "nameProduct":
                            nameProduct = value.replaceAll("^\"|\"$", "").replace("\\\"", "\"");
                            break;
                        case "priceProduct":
                            priceProduct = Double.parseDouble(value);
                            break;
                        case "imageProduct":
                            if (!value.equals("null")) {
                                value = value.replaceAll("^\"|\"$", ""); // Loại bỏ dấu "
                                imageProduct = Base64.getDecoder().decode(value);
                            }
                            break;
                        case "quantityProduct":
                            quantityProduct = Integer.parseInt(value);
                            break;
                        case "statusProduct":
                            statusProduct = Boolean.parseBoolean(value);
                            break;
                        case "idCategory":
                            idCategory = Integer.parseInt(value);
                            break;
                    }
                }

                Product product = new Product(idProduct, nameProduct, priceProduct, imageProduct, quantityProduct, statusProduct, idCategory);
                ProductManager.getInstance().addProduct(product);
            }
        }
    }    
    public void parseJsonToNews(String json) {
        json = json.trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1); // Loại bỏ dấu [ ]
            String[] newsJsonArray = json.split("\\},\\{");

            for (String newsJson : newsJsonArray) {
                newsJson = newsJson.replaceAll("^\\{", "").replaceAll("\\}$", ""); // Loại bỏ dấu { }
                String[] fields = newsJson.split(",");

                int idNew = 0;
                String title = "";
                String content = "";
                LocalDateTime date = null;
                byte[] imageNew = null;

                for (String field : fields) {
                    String[] keyValue = field.split(":", 2);
                    if (keyValue.length != 2) continue;

                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("^\"|\"$", ""); // Loại bỏ dấu "

                    switch (key) {
                        case "idNew":
                            idNew = Integer.parseInt(value);
                            break;
                        case "titleNew":
                            title = value.replace("\\\"", "\"");
                            break;
                        case "contentNew":
                            content = value.replace("\\\"", "\"");
                            break;
                        case "dateNew":
                            if (!value.equals("null")) {
                                date = LocalDateTime.parse(value);
                            }
                            break;
                        case "imageNew":
                            if (!value.equals("null")) {
                                imageNew = Base64.getDecoder().decode(value);
                            }
                            break;
                    }
                }

                New news = new New(idNew, title, content, date, imageNew);
                NewManager.getInstance().addNew(news);
              
            }
        }
    }

    private void cleanup() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
