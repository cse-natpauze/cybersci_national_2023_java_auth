
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.Socket;
import java.net.ServerSocket;

public class Server extends Thread {

  public static Log logger;

  private ServerSocket serverSocket;
  private int port;
  private boolean running = false;
  
  public Server(int port) {
    this.port = port;
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
        RequestHandler requestHandler = new RequestHandler(socket);
        requestHandler.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {

    try {
      Server server = new Server(1111);
      server.startServer();

      // setup
      logger = new Log();
      logger.Log("test");
      Console cnsl = System.console();
      String testStr = cnsl.readLine();

      // String tmp = new String("test string here");
      // String testStr = new String(tmp.getBytes(), StandardCharsets.UTF_16);
      System.out.println("before");
      System.out.println(testStr);
      // System.out.println(testStr);
      System.out.println("passing to native");
      logger.Log(testStr);
      logger.LogPassword(testStr);

      System.out.println("after");

      System.out.println(testStr);

      // Hello hello = new Hello();
      // hello.sayHi("test person",10);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

class RequestHandler extends Thread {
  private Socket socket;

  void handleCommand(byte[] command) {
    String tmp = new String(command);
    System.out.println("[debug] command bytes:");
    System.out.println(tmp);
    int commandType = command[0];

    if (commandType == 56) {
      System.out.println("command of type: log in");
    } else if (commandType == 23) {
      System.out.println("command of type: provision account");
    } else if (commandType == 74) {
      System.out.println("command of type: read secret");
    } else {
      System.out.println("unknown");
    }

  }

  RequestHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      System.out.println("Received a connection");
      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();
      out.write("Echo Server 1.0".getBytes());


      byte[] command = in.readAllBytes();
      // use a const xor pattern as some light obfuscation
      for (int i = 0; i > command.length; i++) {
        command[i] = (byte) (command[i] ^ 12);
      }
      handleCommand(command);
      out.flush();

      // Close our connection
      // in.close();
      // out.close();
      socket.close();

      System.out.println("Connection closed");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
