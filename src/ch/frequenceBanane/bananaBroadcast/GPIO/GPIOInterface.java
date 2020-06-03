package ch.frequenceBanane.bananaBroadcast.GPIO;

import java.net.*;
import java.io.*;

/** 
 * Handle connection between software and Axia GPIO driver.
 * Enable user functions to be called when a GPIO pin is triggered.
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
*/
public class GPIOInterface extends Thread{
	
	private final int nbGPIO     = 16;
	private final int pinPerGPIO = 5;
	
	private boolean running = true;
	
	private byte[] ip;
	private int port;
	
	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;
	private Runnable[][] functions;

	/**
	 * Create a new GPIO interface on a specified IP and port
	 * @param ip The IP address to listen to, as an array of unsigned bytes. 
	 *           Example : {127, 0, 10, 235}
	 * @param port The port to listen to.
	 * @throws IllegalArgumentException if the array is not of size 4, or the port is negative
	 */
	public GPIOInterface(final byte[] ip, final int port){
		if(ip.length != 4) 
			throw new IllegalArgumentException("ip parameter has a size of "+ip.length+" and not 4");
		if(port < 0)
			throw new IllegalArgumentException("port parameter is negative");

		this.ip        = ip;
		this.port      = port;
		this.functions = new Runnable [nbGPIO][pinPerGPIO];
		
		// Populate the callable functions array with dummy functions
		for(int i=0; i<nbGPIO; i++) {
			for(int j=0; j<pinPerGPIO; j++) {
				functions[i][j] = () -> {};
			}
		}
	}
	
	/** Start the connection with Axia audio driver */
	public void run() {
		try {
			socket = new Socket(InetAddress.getByAddress(ip), port);
			output = new PrintWriter(socket.getOutputStream(), true);
			input  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//The connection is instanced, as specified by the protocol
			output.println();
			output.println("ADD GPO");
			
			while(running) {
				if(input.ready()) {
					String[] parts = input.readLine().split(" ");
					//If a change in GPOs is received
					if(parts[0].equals("GPO")) {
						
						int id = Integer.parseInt(parts[1]);
						String pins = parts[2];
						
						for(int i=0; i<pinPerGPIO; i++) {
							// 'L' means a signal changed from high to low, meaning a key is released
							if(pins.charAt(i) == 'L') {
								triggerFunction(id, i);
							}
						}
					}
				}
			}
			socket.close();
		} catch (IOException e) {
			System.err.println("Error in GPIO connexion : "+e.getMessage());
		}
	}

	/** Terminate the connection */
	public void close() {
		running = false;
	}
	
	/** @return The number of emulated GPIO handled */
	public int getNbGPIO() {
		return nbGPIO;
	}
	
	/** @return The number of handled pin per GPIO */
	public int getPinPerGPIO() {
		return pinPerGPIO;
	}
	
	/**
	 * Bind a given runnable function to a specified pin on a specified GPIO
	 * When the given pin on the given GPIO is triggered, the function is called
	 * @param GPIONumber The GPIO on which to bind the function
	 * @param pinID The pin on which to bind the function
	 * @param func The function to call, not null
	 * @throws IllegalArgumentException if GPIONumber or pinId is invalid, or func is null
	 */
	public void bindGPIOFunction(final int GPIONumber, final int pinID, Runnable func) {
		if(GPIONumber < 1 || GPIONumber > nbGPIO)
			throw new IllegalArgumentException("GPIO number is invalid : "+GPIONumber);
		if(pinID < 0 || pinID >= pinPerGPIO)
			throw new IllegalArgumentException("pinID number is invalid : "+GPIONumber);
		if(func == null)
			throw new IllegalArgumentException("given function is null");
		
		functions[GPIONumber-1][pinID] = func;
	}
	
	
	private void triggerFunction(final int GPIONumber, final int pinID) {
		if(GPIONumber < 1 || GPIONumber > nbGPIO)
			throw new IllegalArgumentException("GPIO number is invalid : "+GPIONumber);
		if(pinID < 0 || pinID >= pinPerGPIO)
			throw new IllegalArgumentException("pinID number is invalid : "+GPIONumber);
		
		functions[GPIONumber-1][pinID].run();
	}
}
