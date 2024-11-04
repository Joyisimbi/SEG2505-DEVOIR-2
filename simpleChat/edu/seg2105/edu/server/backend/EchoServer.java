package edu.seg2105.edu.server.backend;

// This file contains material supporting section 3.7 of the textbook:

import java.io.IOException;

// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import com.lloseng.ocsf.server.*;

import edu.seg2105.client.common.ChatIF;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer {
  // Class variables *************************************************

  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

  /* The interface type variable.  It allows the implementation of 
  * the display method in the server.
  */
   ChatIF serverUI; 

  // Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) {
    super(port);
    
  }

  // Instance methods ************************************************

  /**
   * This method handles any messages received from the client.
   *
   * @param msg    The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    String message = (String)msg.toString().trim();
    if(message.startsWith("#login")){
    // existing login

        String loginId = message.substring("#login".length()).trim();
        if(client.getInfo(loginId)==null){
          System.out.println("Message received : #login " + loginId + " from null");
          client.setInfo("loginId", loginId);
          sendToAllClients(loginId + " logged on");
          System.out.println(loginId + " has logged on");

        }

        else{
          try {
              client.sendToClient("Error: You are logged in.");
              client.close();
            } catch(IOException e){
              e.printStackTrace();
          }
          
        }
    } else{

      // if the client is null

      if(client == null){

        System.out.println("Server message received: "+message);

        ConnectionToClient[] clients = (ConnectionToClient[]) getClientConnections();

        for(int i= 0 ; i < clients.length ; i++){
          ConnectionToClient otherClient = clients[i];

          if(otherClient != null){
            try{
              otherClient.sendToClient("SERVER MSG> "+ message);
            } catch(IOException e){
              System.out.println("Error sending message to client: "+ e.getMessage());
            }
          }

          
        }

        return;
      }

      // check if client is logged in

      String loginId = (String) client.getInfo("loginId");

      if(loginId == null){

        try{
          client.sendToClient("Error: You must log in first.");
          client.close();
        } catch(IOException e){
          e.printStackTrace();
        }
      }

      else{
        System.out.println("Message received: "+message + " from "+ loginId);
        sendToAllClients(loginId + ": "+ message);
      }
    }

	  
    
  }

  public void handleMessageFromServerUI(String message){
    try
    {
    	if(message.startsWith("#")){
    		handleCommands(message);
    	  } 
    	else 
    	  { 
    		serverUI.display(message);
    		sendToAllClients("SERVER MSG>" + message);}
    }
    catch(IOException e)
    {
      serverUI.display
        ("Could not send message.");
    }
  }
  

  // Method to process commands from server console

  private void handleCommands(String cmd) throws IOException {
	  if(cmd.equals("#quit")) {
      System.out.println("The server quits");
		  this.close();
	  }
	  else if(cmd.equals("#stop")){
		  this.stopListening();
	  }
	  else if(cmd.equals("#close")) {
      System.out.println("The server has shut down.");
		  this.stopListening();
		  this.close();
	  }
	  else if (cmd.startsWith("#setport")) {
		  //check if client is disconnected
		   
		  String[] instruction = cmd.split(" ");
		  try {
			  this.setPort(Integer.parseInt(instruction[1]));
		  }
		  catch (Exception e ) {
			  serverUI.display("Port value invalid");
		  }
		  
	  }
	  else if(cmd.equals("#start")) {
		  if(!this.isListening()) {
			  try {
				  this.listen();
			  } catch (IOException e) {
				  serverUI.display("Error occurred listening for clients");
			  }
		  } else {
			  serverUI.display("Already started.");
		  }
	  }
	  else if(cmd.equals("#getport")) {
		  serverUI.display("Port: " + Integer.toString(this.getPort()));
	  }
  }

 


  /**
   * This method overrides the one in the superclass. Called
   * when the server starts listening for connections.
   */
  protected void serverStarted() {
    System.out.println("Server listening for connections on port " + getPort());
  }

  /**
   * This method overrides the one in the superclass. Called
   * when the server stops listening for connections.
   */
  protected void serverStopped() {
    System.out.println("Server has stopped listening for connections.");
  }


  // Class methods ***************************************************
  
     /**
   * The implementation of the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) 
  {
	  System.out.println("New client connected: " + client);
  }

  /**
   * The implementation of the method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(
    ConnectionToClient client) 
  {
	  System.out.println("Client disconnected: " + client);		
  }
  /**
   * This method is responsible for the creation of
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on. Defaults to 5555
   *                if no argument is entered.
   */
        public static void main(String[] args) {
          int port = 0; // Port to listen on

          try {
            port = Integer.parseInt(args[0]); // Get port from command line
          } catch (Throwable t) {
            port = DEFAULT_PORT; // Set port to 5555
          }

          EchoServer sv = new EchoServer(port);
          ServerConsole sc = new ServerConsole(sv);

          try {
            sc.server.listen(); // Start listening for connections

          } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
            //ex.printStackTrace();
          }

          sc.accept();
        }
}
// End of EchoServer class
