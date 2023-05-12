
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Arrays;
import java.security.SecureRandom;

public class Server extends Thread {

  public Log logger;

  public Vector<String> auth_strings;

  private ServerSocket serverSocket;
  private int port;
  private boolean running = false;
  
  public Server(int port) {
    this.port = port;
    this.logger = new Log();
    this.auth_strings = new Vector<String>();
  }

  public void startServer() {
    try {
      serverSocket = new ServerSocket(port);
      this.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    running = true;
    while (running) {
      try {
        System.out.println("Listening for a connection");

        // Call accept() to receive the next connection
        Socket socket = serverSocket.accept();

        // Pass the socket to the RequestHandler thread for processing
        RequestHandler requestHandler = new RequestHandler(socket,this);
        requestHandler.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {

    try {
      Server server = new Server(1111);

      // setup
      // logger = new Log();
      // logger.Log("test");

      //tmp account for dev purposes
      server.auth_strings.add("test:password"); //DEBUG
      server.auth_strings.add("a:a"); //DEBUG

      server.startServer();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

class RequestHandler extends Thread {
  private Socket socket;
  public Server server;

  String handleCommand(byte[] command) {
    String tmp = new String(command);
    System.out.println("[DEBUG] command bytes:");
    System.out.println(tmp);                  //[DEBUG]
    int commandType = command[0];
    String command_str_t = new String(Arrays.copyOfRange(command,1,command.length));
    String command_str = command_str_t.replace("\0", "");
    System.out.println("[DEBUG] command str:");
    System.out.println(command_str);          //[DEBUG]
    System.out.println("[DEBUG] command str.length:");
    System.out.println(command_str.length()); //DEBUG
    if (commandType == 56) {
      System.out.println("command of type: log in");
      System.out.println("[DEBUG] Accounts:");
      for(int i =0; i<server.auth_strings.size();i++){
        System.out.println("-----------------------------");            //[DEBUG]
        System.out.println(this.server.auth_strings.get(i));            //[DEBUG]
        server.logger.LogPassword(this.server.auth_strings.get(i));
        System.out.println(this.server.auth_strings.get(i));            //[DEBUG]
      }
      
      if(this.server.auth_strings.contains(command_str)){
        System.out.println("Authed");  //[DEBUG]
        return "Authenticated";
      }else{
        System.out.println("Auth Failed"); //[DEBUG]
        return "Authentication failed";
      }

    } else if (commandType == 23) {
      System.out.println("[DEBUG] command of type: provision account");
      System.out.println("[DEBUG] provisioning account for user " + command_str);
      //
      SecureRandom random = new SecureRandom();
      byte r_bytes[] = new byte[64];
      String password = new String("");
      random.nextBytes(r_bytes);
      // 
      for(int i =0; i<r_bytes.length;i++){
        int v = r_bytes[i] + 128;
        char c = (char)('A' + (v%57));
        password = password.concat(String.valueOf(c));
      }
      System.out.println("[DEBUG] new password: " + password);
      String auth_string = command_str + ":" + password; 
      
      server.auth_strings.add(auth_string);
      return "Account provisioned, contact an administrator for your temporary password";



    } else if (commandType == 74) {
      System.out.println("[DEBUG] command of type: read secret");
      if(this.server.auth_strings.contains(command_str)){
        System.out.println("Authed");  //[DEBUG]
        return System.getenv("CTFKEY");
      }else{
        return "Bad creds";
      }
    } else {
      // System.out.println("unknown");
      // return "Error";
    }
    return "Error";

  }

  RequestHandler(Socket socket, Server server) {
    this.socket = socket;
    this.server = server;
  }

  @Override
  public void run() {
    try {
      System.out.println("Received a connection");
      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();


      byte[] command = new byte[256];
      in.read(command);
      // use a const xor pattern as some light obfuscation
      for (int i = 0; i > command.length; i++) {
        command[i] = (byte) (command[i] ^ 12);
      }
      String return_text = handleCommand(command);
      System.out.println("[DEBUG] return_text: " + return_text);
      out.write(return_text.getBytes());
      out.flush();
      System.out.println("[DEBUG] sent");
      // client will close the socket... good enough who cares is they dos their own box

      System.out.println("Connection closed");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
