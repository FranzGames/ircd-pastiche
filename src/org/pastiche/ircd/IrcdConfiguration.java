package org.pastiche.ircd;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.pastiche.ircd.rfc1459.UnregisteredClient;
import org.w3c.dom.*;

/**
 * <p>
 * A Singleton that encapsulates the configuration of the IRC server.
 * Eventually, this will be done via an XML document, but for the purposes of
 * testing, the getInstance() method returns a subclass that contains
 * compiled-in defaults.
 */
public class IrcdConfiguration {

   private static IrcdConfiguration instance = null;

   private Command connectedDefaultCommand;
   private java.util.Map connectedMap = new java.util.HashMap();
   private Command unregisteredDefaultCommand;
   private java.util.Map unregisteredMap = new java.util.HashMap();
   protected Authenticator authenticator = null;
   protected int deadClientTimeout = 120;
   protected int unregisteredClientTimeout = 0;
   private Listener[] listeners = null;
   private Map<String, KeyStoreConfiguration> keystores = new HashMap<String, KeyStoreConfiguration>();
   private String serverName;
   private String networkName = "pastiche";
   private String description;
   private NameNormalizer serverNormalizer;
   private NameNormalizer channelNormalizer;
   private NameNormalizer userNormalizer;
   private Class initialConnectionClass;
   private Class channelClass;
   private ArrayList operators = new ArrayList();

   public static IrcdConfiguration getInstance() {
      if (instance == null) {
         instance = new IrcdConfiguration();
      }

      return instance;
   }

   protected IrcdConfiguration() {
      // Allow the reloading of the authentication list every 5 minutes
      Scheduler.addTask(new ReloadAuthenticator(), 30000, 30000);
   }

   public void checkConfiguration() throws ConfigurationException {
      if (userNormalizer == null) {
         throw new ConfigurationException("Nick Normalizer is not defined.");
      }

      if (channelNormalizer == null) {
         throw new ConfigurationException("Channel Normalizer is not defined.");
      }

      if (initialConnectionClass == null) {
         throw new ConfigurationException("Initial Connection Class is not defined.");
      }

      if (serverName == null) {
         throw new ConfigurationException("Server Name is not defined.");
      }

      if (listeners == null || listeners.length == 0) {
         throw new ConfigurationException("No listeners have been defined.");
      }

      if (connectedDefaultCommand == null) {
         throw new ConfigurationException("No Default Command for connected user has been defined.");
      }

      if (unregisteredDefaultCommand == null) {
         throw new ConfigurationException("No Default Command for unregistered user has been defined.");
      }

      if (connectedMap.size() == 0) {
         throw new ConfigurationException("No Commands have been defined for connected user.");
      }

      if (unregisteredMap.size() == 0) {
         throw new ConfigurationException("No Commands have been defined for unregistered user.");
      }
   }

   public Listener[] getListeners() {
      return listeners;
   }
   
   public KeyStoreConfiguration getKeystore (String name) {
      return keystores.get(name);
   }

   public void loadServerConfiguration(Document doc) {
      NodeList list;
      Vector vec = new Vector();
      Server server;
      String channelName = getNodeValue(doc, "channel-class");

      authenticator = createAuthenticator(getNodeValue(doc, "authenticator"));
      server = createServer(getNodeValue(doc, "server-class"));
      
      try {
         if (channelName == null) {
            channelClass = org.pastiche.ircd.rfc1459.Channel.class;
         } else {
            channelClass = Class.forName(channelName);
         }
      } catch (Throwable t) {
         System.err.println("Channel class " + channelName + " is missing.");
         
         t.printStackTrace();
      }

      list = doc.getElementsByTagName("listener");

      for (int i = 0; i < list.getLength(); i++) {
         Node node = list.item(i);

         if (node.getAttributes().getNamedItem("address") != null
            && node.getAttributes().getNamedItem("port") != null) {
            try {
               int port = Integer.valueOf(node.getAttributes().getNamedItem("port").getNodeValue()).intValue();
               String secure = null;
               String keyStore = "default";
               
               if (node.getAttributes().getNamedItem("secure") != null) {
                  secure = node.getAttributes().getNamedItem("secure").getNodeValue();
               }
               
               if (node.getAttributes().getNamedItem("keystore_name") != null) {
                  keyStore = node.getAttributes().getNamedItem("keystore_name").getNodeValue();
               }
               

               vec.addElement(new Listener(server, port, 
                  node.getAttributes().getNamedItem("address").getNodeValue(), 
                  secure, keyStore));
            } catch (Throwable t) {
               t.printStackTrace();
            }
         }
      }

      listeners = new Listener[vec.size()];
      vec.copyInto(listeners);

      list = doc.getElementsByTagName("operator");

      for (int i = 0; i < list.getLength(); i++) {
         Node node = list.item(i);

         if (node.getAttributes().getNamedItem("name") != null) {
            try {
               operators.add(node.getAttributes().getNamedItem("name").getNodeValue());
            } catch (Throwable t) {
               t.printStackTrace();
            }
         }
      }

      serverName = getNodeValue(doc, "name");
      if (getNodeValue(doc, "network") != null) {
         networkName = getNodeValue(doc, "network");
      }
      
      description = getNodeValue(doc, "description");
      String value = getNodeValue(doc, "dead_client_timeout");

      if (value != null) {
         try {
            deadClientTimeout = Integer.valueOf(value).intValue();
         } catch (Throwable t) {
            System.err.println("Error interpretting dead client timeout setting (" + t + ")");
         }
      }

      value = getNodeValue(doc, "unregistered_client_timeout");

      if (value != null) {
         try {
            unregisteredClientTimeout = Integer.valueOf(value).intValue();
         } catch (Throwable t) {
            System.err.println("Error interpretting unregistered client timeout setting (" + t + ")");
         }
      }
      
      list = doc.getElementsByTagName("truststore");

      for (int i = 0; i < list.getLength(); i++) {
         Node node = list.item(i);

         if (node.getAttributes().getNamedItem("keystore") != null
            && node.getAttributes().getNamedItem("password") != null
            && node.getAttributes().getNamedItem("key_password") != null) {
            try {
               String keystore = node.getAttributes().getNamedItem("keystore").getNodeValue();
               String password = node.getAttributes().getNamedItem("password").getNodeValue();
               String keyPassword = node.getAttributes().getNamedItem("key_password").getNodeValue();
               String storetype = node.getAttributes().getNamedItem("type") == null ?
                  "JKS" : node.getAttributes().getNamedItem("type").getNodeValue();
               String name = node.getAttributes().getNamedItem("name") == null ?
                  "default" : node.getAttributes().getNamedItem("name").getNodeValue();
              
	       File keyFile = new File (keystore);

	       if (keyFile.exists()) { 
                  keystores.put (name, new KeyStoreConfiguration (keystore, password, keyPassword, storetype));
	       } else {
		  System.err.println ("Keystore "+keystore+" is missing. Skipped.");
	       }
               
            } catch (Throwable t) {
               t.printStackTrace();
            }
         }
      }      

   }

   public void loadCommandConfiguration(Document doc) {
      NodeList list, commands;

      userNormalizer = createNormalizer(getNodeValue(doc, "nick_normalizer"));
      channelNormalizer = createNormalizer(getNodeValue(doc, "channel_normalizer"));
      serverNormalizer = createNormalizer(getNodeValue(doc, "server_normalizer"));

      try {
         initialConnectionClass = Class.forName(getNodeValue(doc, "initial_connection"));
      } catch (Throwable t) {
         t.printStackTrace();
      }

      list = doc.getElementsByTagName("commandset");

      for (int i = 0; i < list.getLength(); i++) {
         Node node = list.item(i);
         NodeList children = node.getChildNodes();
         String context = null;

         if (node.getAttributes().getNamedItem("context") != null) {
            context = node.getAttributes().getNamedItem("context").getNodeValue();
         } else {
            continue;
         }

         for (int j = 0; j < children.getLength(); j++) {
            Node child = children.item(j);

            if ("default".equals(child.getNodeName())) {
               String defaultHandler = child.getFirstChild().getNodeValue();

               try {
                  Class handlerClass = Class.forName(defaultHandler);
                  Command commandObj = (Command) handlerClass.newInstance();

                  if ("unregistered".equals(context)) {
                     unregisteredDefaultCommand = commandObj;
                  } else if ("connected".equals(context)) {
                     connectedDefaultCommand = commandObj;
                  }
               } catch (Throwable t) {
                  t.printStackTrace();
               }
            } else if ("command".equals(child.getNodeName())) {
               NodeList commandSettings = child.getChildNodes();
               String name = null, handler = null;

               for (int k = 0; k < commandSettings.getLength(); k++) {
                  Node setting = commandSettings.item(k);

                  if ("name".equals(setting.getNodeName())) {
                     try {
                        name = setting.getFirstChild().getNodeValue();
                     } catch (Throwable t) {
                        t.printStackTrace();
                     }
                  } else if ("handler".equals(setting.getNodeName())) {
                     try {
                        handler = setting.getFirstChild().getNodeValue();
                     } catch (Throwable t) {
                        t.printStackTrace();
                     }
                  }
               }

               if (name != null && handler != null) {
                  if ("unregistered".equals(context)) {
                     addUnregisteredCommand(name, handler);
                  } else if ("connected".equals(context)) {
                     addConnectedCommand(name, handler);
                  }
               }
            }
         }
      }
   }

   protected String getNodeValue(Document doc, String name) {
      NodeList list;

      list = doc.getElementsByTagName(name);

      if (list == null) {
         return null;
      }

      for (int i = 0; i < list.getLength(); i++) {
         Node node = list.item(i);

         if (node.getFirstChild() != null) {
            return node.getFirstChild().getNodeValue();
         }
      }

      return null;
   }

   protected void addUnregisteredCommand(String command, String handler) {
      try {
         Class handlerClass = Class.forName(handler);
         Command commandObj = (Command) handlerClass.newInstance();

         unregisteredMap.put(command.toUpperCase(), commandObj);
      } catch (Throwable t) {
         t.printStackTrace();
      }
   }

   protected void addConnectedCommand(String command, String handler) {
      try {
         Class handlerClass = Class.forName(handler);
         Command commandObj = (Command) handlerClass.newInstance();

         connectedMap.put(command.toUpperCase(), commandObj);
      } catch (Throwable t) {
         System.err.println("Unable instantiate " + handler + " class for command " + command);
         t.printStackTrace();
      }
   }

   protected NameNormalizer createNormalizer(String handler) {
      NameNormalizer normalizerObj = null;

      if ("".equals(handler) || handler == null) {
         return null;
      }

      try {
         Class handlerClass = Class.forName(handler);
         normalizerObj = (NameNormalizer) handlerClass.newInstance();
      } catch (Throwable t) {
         t.printStackTrace();
      }

      return normalizerObj;
   }

   protected Authenticator createAuthenticator(String handler) {
      Authenticator authObj = null;

      if ("".equals(handler) || handler == null) {
         return null;
      }

      try {
         Class handlerClass = Class.forName(handler);
         authObj = (Authenticator) handlerClass.newInstance();
      } catch (Throwable t) {
         t.printStackTrace();
      }

      return authObj;
   }
   
   protected Server createServer(String serverClassName) {
      Server serverObj = null;

      if ("".equals(serverClassName) || serverClassName == null) {
         serverClassName = "org.pastiche.ircd.Server";
      }

      try {
         Class serverClass = Class.forName(serverClassName);
         serverObj = (Server) serverClass.newInstance();
      } catch (Throwable t) {
         System.err.println("Server class " + serverClassName + " is missing.");
         
         t.printStackTrace();
      }

      return serverObj;
   }
   

   public java.util.Map getCommandMap(String clientContext) {
      if (clientContext.equals(UnregisteredClient.COMMAND_FACTORY_ID)) {
         return unregisteredMap;
      } else if (clientContext.equals(org.pastiche.ircd.rfc1459.RegisteredUser.COMMAND_FACTORY_ID)) {
         return connectedMap;
      }
      return null;
   }

   public Command getDefaultCommand(String clientContext) {
      if (clientContext.equals(UnregisteredClient.COMMAND_FACTORY_ID)) {
         return unregisteredDefaultCommand;
      } else if (clientContext.equals(org.pastiche.ircd.rfc1459.RegisteredUser.COMMAND_FACTORY_ID)) {
         return connectedDefaultCommand;
      }

      return null;
   }

   public Class getInitialConnectionClass() {
      return initialConnectionClass;
   }
   
   public Class getChannelClass() {
      return channelClass;
   }

   public NameNormalizer getChannelNormalizer() {
      return channelNormalizer;
   }

   public NameNormalizer getServerNormalizer() {
      return serverNormalizer;
   }

   public NameNormalizer getUserNormalizer() {
      return userNormalizer;
   }

   public String getFullServerName() {
      return description;
   }

   public String getServerName() {
      return serverName;
   }
   
   public String getNetworkName() {
      return networkName;
   }

   public int getUnregisteredClientTimeout() {
      return unregisteredClientTimeout;
   }

   // Return the amount of time in seconds
   public int getDeadClientTimeout() {
      return deadClientTimeout;
   }

   public boolean isOperator(String nick) {
      if (nick == null) {
         return false;
      }

      return operators.contains(nick);
   }

   /* If the result of the method call is null then
    there is no authenticator and therefore disabled */
   public Authenticator getNickPasswordAuthenticator() {
      return authenticator;
   }

   class ReloadAuthenticator extends java.util.TimerTask {

      public void run() {
         if (authenticator != null) {
            authenticator.reload();
         }
      }
   }
}
