package org.pastiche.ircd.http;

import org.pastiche.ircd.http.endpoint.Connect;
import org.pastiche.ircd.http.endpoint.Disconnect;
import org.pastiche.ircd.http.endpoint.SendCommand;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import org.pastiche.ircd.IrcdConfiguration;
import org.pastiche.ircd.http.endpoint.GetMsg;
import org.pastiche.ircd.http.endpoint.SendMsg;

public class Server extends Thread {

   org.pastiche.ircd.Server ircServer;
   static boolean debug = false;
   
   static {
      debug = System.getProperty("http.debug", "false").equals("true");
   }

   public static void main(String argv[]) {
      new Server(null, new ServerConfiguration(Integer.valueOf(argv[0]).intValue())).start();

      while (true) {
      }
   }

//the constructor method
//the parameters it takes is what port to bind to, the default tcp port
//for a httpserver is port 80. the other parameter is a reference to
//the gui, this is to pass messages to our nice interface
   public Server(org.pastiche.ircd.Server server, ServerConfiguration conf) {
      configuration = conf;
      ircServer = server;

      endPointMapping.put("/connect", new Connect(this));
      endPointMapping.put("/disconnect", new Disconnect(this));
      endPointMapping.put("/sendcmd", new SendCommand(this));
      endPointMapping.put("/sendmsg", new SendMsg(this));
      endPointMapping.put("/get", new GetMsg(this));
   }

   public org.pastiche.ircd.Server getIrcServer() {
      return ircServer;
   }

   private void showStatus(String s2) { //an alias to avoid typing so much!
      if (debug) {
         System.out.println(s2);
      }
   }

   private int maxMessageSize = 100000;
   private ServerConfiguration configuration; //port we are going to listen to
   Map<String, EndPointProcessor> endPointMapping = new HashMap();

   protected ServerSocket createServerSocket() throws NoSuchAlgorithmException, KeyManagementException, IOException {
      ServerSocket sock = null;
      String sslType = configuration.getSecureType();
      int backlog = 50;

      if (sslType == null || "false".equals(sslType)) {
         if (configuration.getBindHost() == null) {
            sock = new ServerSocket(configuration.getPort(), backlog);
         } else {
            sock = new ServerSocket(configuration.getPort(), backlog, java.net.InetAddress.getByName(configuration.getBindHost()));
         }
      } else {
         if (sslType.equals("true")) {
            sslType = "TLS";
         }

         KeyManagerFactory keyFactory = IrcdConfiguration.getInstance().getKeystore(configuration.getKeystore()).getKeyManagerFactory();

         SSLContext sc = SSLContext.getInstance(sslType);
         sc.init(keyFactory.getKeyManagers(), null, null);
         SSLServerSocketFactory ssf = sc.getServerSocketFactory();

         if (configuration.getBindHost() == null) {
            sock = ssf.createServerSocket(configuration.getPort(), backlog);
         } else {
            sock = ssf.createServerSocket(configuration.getPort(), backlog, java.net.InetAddress.getByName(configuration.getBindHost()));
         }
      }
      
      return sock;
   }

//this is a overridden method from the Thread class we extended from
   @Override
   public void run() {
      //we are now inside our own thread separated from the gui.
      ServerSocket serversocket = null;
      //To easily pick up lots of girls, change this to your name!!!
      showStatus("IRC Web Service");

      try {
         //print/send message to the guiwindow
         showStatus("Bind socket for server configuration:"+configuration);
         this.setName("Http Server "+configuration.toString());
         //make a ServerSocket and bind it to given port,
         serversocket = createServerSocket();
         System.out.println (configuration.getBindHost()+":"+configuration.getPort()+"...done.");
      } catch (Exception e) { //catch any errors and print errors to gui
         showStatus("\nFatal Error:" + e.getMessage());
         return;
      }
      //go in a infinite loop, wait for connections, process request, send response
      while (true) {
         showStatus("\nReady, Waiting for requests...\n");
         try {
            Socket connectionsocket = serversocket.accept();
            InetAddress client = connectionsocket.getInetAddress();
            showStatus(client.getHostName() + " connected to server.\n");
            DataOutputStream output = new DataOutputStream(connectionsocket.getOutputStream());

            processRequest(connectionsocket.getInputStream(), output);
         } catch (Exception e) { //catch any errors, and print them
            showStatus("\nError:" + e.getMessage());
         }

      } //go back in loop, wait for next request
   }

//our implementation of the hypertext transfer protocol
//its very basic and stripped down
   private void processRequest(InputStream input, DataOutputStream output) {
      int method = 0; //1 get, 2 head, 0 not supported
      String http = new String(); //a bunch of strings to hold
      String uri = null;
      String file = new String(); //what file
      String user_agent = new String(); //what user_agent
      byte[] bigBuffer = new byte[maxMessageSize];
      int bufOffset = 0;
      try {
         //This is the two types of request we can handle
         //GET /index.html HTTP/1.0
         //HEAD /index.html HTTP/1.0

         try {
            int b;
            while (input.available() > 0 && (b = input.read()) >= 0) {
               bigBuffer[bufOffset++] = (byte) b;

               if (bufOffset >= maxMessageSize) {
                  output.writeBytes(getHttpResultHeader(400, null));
                  output.close();
                  return;
               }

               if (((byte) b) == '\n') {
                  break;
               }
            }
         } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
         }

         String line = new String(bigBuffer, 0, bufOffset);
//         System.out.println("Request: " + line);
         if (line.startsWith("GET ")) { //compare it is it GET
            method = 1;
         } //if we set it to method 1
         else if (line.startsWith("HEAD ")) { //same here is it HEAD
            method = 2;
         }
         if (line.startsWith("POST ")) { //compare it is it GET
            method = 3;
         } //if we set it to method 1
         if (line.startsWith("PUT ")) { //compare it is it GET
            method = 4;
         } //if we set it to method 1

         if (method == 0) { // not supported
            try {
               output.writeBytes(getHttpResultHeader(501, null));
               output.close();
               return;
            } catch (Exception e3) { //if some error happened catch it
               showStatus("error:" + e3.getMessage());
            } //and display error //and display error //and display error //and display error
         }
         //}

         //tmp contains "GET /index.html HTTP/1.0 ......."
         //find first space
         //find next space
         //copy whats between minus slash, then you get "index.html"
         //it's a bit of dirty code, but bear with me...
         int start = line.indexOf(' ');

         if (start > 0) {
            int end = line.indexOf(' ', start + 1);

            if (end > 0) {
               uri = line.substring(start + 1, end);
            }
         }

         if (uri == null) {
            output.writeBytes(getHttpResultHeader(400, null));
            output.close();
            return;
         }

      } catch (Exception e) {
         showStatus("error : " + e.getMessage());
      } //catch any exception //catch any exception //catch any exception //catch any exception

      //path do now have the filename to what to the file it wants to open
      showStatus("URI requested: " + uri + "\n");
      try {
         String tmp = null;
         bufOffset = 0;
         try {
            int b;
            byte prevByte = 0;
            while (input.available() > 0 && (b = input.read()) >= 0) {
               byte by = (byte) b;

               if (prevByte == '\n' && by == '\r') {
                  break;
               }
               bigBuffer[bufOffset++] = by;

               if (bufOffset >= maxMessageSize) {
                  output.writeBytes(getHttpResultHeader(400, null));
                  output.close();
                  return;
               }

               prevByte = by;
            }
         } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
         }
         tmp = new String(bigBuffer, 0, bufOffset);

//         System.out.println("Request Header: " + tmp);

         int b;
         bufOffset = 0;
         while (input.available() > 0 && (b = input.read()) >= 0) {
            byte by = (byte) b;
            bigBuffer[bufOffset++] = by;

            if (bufOffset >= maxMessageSize) {
               output.writeBytes(getHttpResultHeader(400, null));
               output.close();
               return;
            }

         }

         String data = new String(bigBuffer, 0, bufOffset);

//         System.out.println("data: " + data);
//         System.out.println();

         if (!endPointMapping.containsKey(uri)) {
            output.writeBytes(getHttpResultHeader(404, null));
            output.close();
         } else {
            HttpResult result = endPointMapping.get(uri).process(uri, data);

            output.writeBytes(getHttpResultHeader(result.getCode(), result.getContentType()));
            if (result.getData() != null) {
               output.writeChars(result.getData());
            }
            output.close();
         }

      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   //this method makes the HTTP header for the response
   //the headers job is to tell the browser the result of the request
   //among if it was successful or not.
   private String getHttpResultHeader(int resultCode, String resultType) {
      StringBuilder s = new StringBuilder("HTTP/1.0 ");
      //you probably have seen these if you have been surfing the web a while
      switch (resultCode) {
         case 200:
            s.append("200 OK");
            break;
         case 400:
            s.append("400 Bad Request");
            break;
         case 403:
            s.append("403 Forbidden");
            break;
         case 404:
            s.append("404 Not Found");
            break;
         case 500:
            s.append("500 Internal Server Error");
            break;
         case 501:
            s.append("501 Not Implemented");
            break;
      }

      s.append("\r\n"); //other header fields,
      s.append("Connection: close\r\n"); //we can't handle persistent connections
      s.append("Server: Pastiche Server\r\n"); //server name

      if (resultType != null && !resultType.isEmpty()) {
         s.append("Content-Type: ");
         s.append(resultType);
         s.append("\r\n");
      }
      ////so on and so on......
      s.append("\r\n"); //this marks the end of the httpheader
      //and the start of the body
      //ok return our newly created header!
      return s.toString();
   }

} //class phhew caffeine yes please!
