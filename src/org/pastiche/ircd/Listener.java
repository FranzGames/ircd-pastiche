package org.pastiche.ircd;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * <p>
 * A Listener is responsible for holding open a ServerSocket, and redirecting
 * any incoming connections to the right objects. There will be one Listener
 * thread for each port that the server listens on.
 */
public class Listener implements Runnable {

   private String bindHost = null;
   private int port = 6667;
   private int backlog = 50;
   private String secureType = null;
   private String keystore = null;
   public long connNo = 0;

   public String toString() {
      return ((bindHost != null) ? bindHost : "*") + ":" + port;
   }

   public int getPort() {
      return port;
   }

   public void run() {
      ServerSocket sock = null;
      String sslType = secureType;

      try {
         if (sslType == null || "false".equals(sslType)) {
            if (bindHost == null) {
               sock = new ServerSocket(port, backlog);
            } else {
               sock = new ServerSocket(port, backlog, java.net.InetAddress.getByName(bindHost));
            }
         } else {
            if (sslType.equals("true")) {
               sslType = "TLS";
            }

            KeyManagerFactory keyFactory = IrcdConfiguration.getInstance().getKeystore(this.keystore).getKeyManagerFactory();

            SSLContext sc = SSLContext.getInstance(sslType);
            sc.init(keyFactory.getKeyManagers(), null, null);
            SSLServerSocketFactory ssf = sc.getServerSocketFactory();

            if (bindHost == null) {
               sock = ssf.createServerSocket(port, backlog);
            } else {
               sock = ssf.createServerSocket(port, backlog, java.net.InetAddress.getByName(bindHost));
            }
         }
      } catch (java.net.UnknownHostException uhe) {
         // FIXME: Alert scheme, remove Listener from configuration.
         System.out.println("Could not find bindHost for Listener: " + bindHost);
      } catch (java.io.IOException ioe) {
         // FIXME: Alert scheme, remove Listener from configuration.
         System.out.println("Could not bind Listener to address: " + this);
         System.out.println(ioe);
      } catch (KeyManagementException ex) {
         ex.printStackTrace();
      } catch (NoSuchAlgorithmException ex) {
         System.out.println("No Such Algorithm: " + sslType);
      }

      while (sock != null) {
         try {
            Socket conn = sock.accept();
            connNo++;
            ConnectedTarget client = ConnectedTarget.newConnectedClient(server, conn);
            new Thread((SocketConnection)client.getConnection(), "Connection #" + connNo).start();
         } catch (java.io.IOException ioe) {
            // FIXME: Alert scheme, remove Listener from configuration.
            System.out.println("Could not accept connection on Listener " + this);
            System.out.println(ioe);
            break;
         }

      }
   }

   private static final int DEFAULT_BACKLOG = 50;
   private Server server = null;

   public Listener(Server server, int port) {
      this(server, port, null, null, null);
   }

   public Listener(Server server, int port, int backlog, String bindHost, String secureType, String keystoreName) {
      this.port = port;
      this.bindHost = bindHost;
      this.backlog = backlog;
      this.server = server;
      this.secureType = secureType;
      this.keystore = keystoreName;
   }

   public Listener(Server server, int port, String bindHost, String secureType, String keystoreName) {
      this(server, port, DEFAULT_BACKLOG, bindHost, secureType, keystoreName);
   }
}
