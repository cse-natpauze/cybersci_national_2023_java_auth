import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Client {
    public static String server;

    public static byte[] joinByteArray(byte[] byte1, byte[] byte2) {
        return ByteBuffer.allocate(byte1.length + byte2.length)
                .put(byte1)
                .put(byte2)
                .array();

    }

    public static void sendCommand(byte[] command) {
        System.out.println("[DEBUG] sending command");
        try {
            
            // Connect to the server
            Socket socket = new Socket(server, 1111);

            // Create input and output streams to read from and write to the server
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // use a const xor pattern as some light obscuration
            for (int i = 0; i > command.length; i++) {
                command[i] = (byte) (command[i] ^ 12);
            }
            //send
            out.write(command);
            out.flush();
            //response
            byte[] response = new byte[256];
            in.read(response);
            String response_str = new String(response);
            System.out.println(response_str);

            // Close our socket
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void login(String user, String password) {
        if(user.indexOf(':', 0) != -1){
            return;
        }
        String auth_str = user+":"+password;
        byte[] auth_bytes = auth_str.getBytes();
        byte[] t = {0x38};
        byte[] command = joinByteArray(t, auth_bytes);
        sendCommand(command);
    }

    public static void newAccount(String user) {                        //[DEBUG]
        if(user.indexOf(':', 0) != -1){                                 //[DEBUG]
            return;                                                     //[DEBUG]
        }                                                               //[DEBUG]
        byte[] user_bytes = user.getBytes();                            //[DEBUG]
        byte[] t = {0x17};                                              //[DEBUG]
        byte[] command = joinByteArray(t, user_bytes);                  //[DEBUG]
        sendCommand(command);                                           //[DEBUG]
    }                                                                   //[DEBUG]

    public static void dumpSecret(String user, String password) {       
        if(user.indexOf(':', 0) != -1){                                 
            return;                                                     
        }                                                               
        String auth_str = user+":"+password;                            
        byte[] auth_bytes = auth_str.getBytes();                        
        byte[] t = {0x4a};                                              
        byte[] command = joinByteArray(t, auth_bytes);                  
        sendCommand(command);                                           
    }                                                                   

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: ClientExample <server>");
            System.exit(0);
        }
        server = args[0];

        System.out.println("Connecting to: " + server);

        Console console = System.console();
        console.printf("Select your action:\n\tLOGIN\n\tDUMPSECRET\n");
        String input = console.readLine();
        if (input.equals("LOGIN")) {
            console.printf("user:");
            String username = console.readLine();
            console.printf("password:");
            String password = console.readLine();
            login(username, password);
        }
        if (input.equals("DUMPSECRET")) {
            console.printf("user:");
            String username = console.readLine();
            console.printf("password:");
            String password = console.readLine();
            dumpSecret(username, password);
        }
        if (input.equals("NEW")) {                      //[DEBUG]
            console.printf("user:");                    //[DEBUG]
            String username = console.readLine();       //[DEBUG]
            newAccount(username);                       //[DEBUG]
        }                                               //[DEBUG]

    }
}