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
        System.out.println("sending command");
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

            out.write(command);

            // Close our streams
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void login(String user, String password) {
        if(user.indexOf(':', 0) != -1){
            return;
        }
        // System.out.println(
        String auth_str = user+":"+password;
        byte[] auth_bytes = auth_str.getBytes();
        byte[] t = {0x38};
        byte[] command = joinByteArray(t, auth_bytes);
        sendCommand(command);
    }
    public static void dumpSecret(String user, String password) {
        if(user.indexOf(':', 0) != -1){
            return;
        }
        // System.out.println(
        String auth_str = user+":"+password;
        byte[] auth_bytes = auth_str.getBytes();
        byte[] t = {0x17};
        byte[] command = joinByteArray(t, auth_bytes);
        sendCommand(command);
    }

    // public static void provisionAccount(String user) {
    //     if(user.indexOf(':', 0) != -1){
    //         return;
    //     }
    //     // System.out.println(
    //     byte[] user_b = user.getBytes();
    //     byte[] t = {0x4a};
    //     byte[] command = joinByteArray(t, user_b);
    //     sendCommand(command);
    // }

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
            login(username, password);
        }

    }
}