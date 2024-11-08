package edu.seg2105.edu.server.backend;
import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;


import java.io.*;
import java.util.Scanner;



public class ServerConsole implements ChatIF {

        EchoServer server;
	
	Scanner fromConsole;
	
	final public static int DEFAULT_PORT = 5555;

    public ServerConsole(EchoServer server) 
	  {
	    this.server = server;
	    // Create scanner object to read from console
	    fromConsole = new Scanner(System.in); 
	  }

    //Instance methods ************************************************
	/**
	   * This method waits for input from the console.  Once it is 
	   * received, it sends it to the client's message handler.
	   */
	  public void accept() 
	  {
	    try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }


      /**
	   * This method overrides the method in the ChatIF interface.  It
	   * displays a message onto the screen.
	   *
	   * @param message The string to be displayed.
	   */
	  public void display(String message) 
	  {
	    System.out.println("> " + message);
	  }

	  
}
