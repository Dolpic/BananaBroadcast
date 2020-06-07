package ch.frequenceBanane.bananaBroadcast.GPIO;

import java.net.*;

import ch.frequenceBanane.bananaBroadcast.utils.Log;

import java.io.*;

/** 
 * Handle connection between software and Axia GPIO driver.
 * Enable user functions to be called when a GPIO pin is triggered.
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
*/
public class GPIOInterface extends Thread{
	
	private final int NB_GPIO     = 16;
	private final int PIN_PER_GPIO = 5;
	
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
		this.functions = new Runnable [NB_GPIO][PIN_PER_GPIO];
		
		// Populate the callable functions array with dummy functions
		for(int i=0; i<NB_GPIO; i++) {
			for(int j=0; j<PIN_PER_GPIO; j++) {
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
					processReceivedLine(input.readLine());
				}
			}
			socket.close();
		} catch (IOException e) {
			Log.error("Error in GPIO connexion : "+e.getMessage());
		}
	}
	
	private void processReceivedLine(final String line) {
		String[] parts = line.split(" ");
		final String header = parts[0];
		final int GPIO_Id   = Integer.parseInt(parts[1]);
		final String pins   = parts[2];
		
		if(header.equals("GPO")) {
			
			for(int pin=0; pin<PIN_PER_GPIO; pin++) {
				// 'L' means a signal changed from high to low, meaning a key is released
				if(pins.charAt(pin) == 'L')
					triggerFunction(GPIO_Id, pin);
			}
		}
	}

	/** Terminate the connection */
	public void close() {
		running = false;
	}
	
	/** @return The number of emulated GPIO handled */
	public int getNbGPIO() {
		return NB_GPIO;
	}
	
	/** @return The number of handled pin per GPIO */
	public int getPinPerGPIO() {
		return PIN_PER_GPIO;
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
		checkValidGPIONumerAndPinId(GPIONumber, pinID);
		if(func == null)
			throw new IllegalArgumentException("given function is null");
		
		functions[GPIONumber-1][pinID] = func;
	}
	
	
	private void triggerFunction(final int GPIONumber, final int pinID) {
		checkValidGPIONumerAndPinId(GPIONumber, pinID);
		functions[GPIONumber-1][pinID].run();
	}
	
	private void checkValidGPIONumerAndPinId(final int GPIONumber, final int pinID) {
		if(GPIONumber < 1 || GPIONumber > NB_GPIO)
			throw new IllegalArgumentException("GPIO number is invalid : "+GPIONumber);
		if(pinID < 0 || pinID >= PIN_PER_GPIO)
			throw new IllegalArgumentException("pinID number is invalid : "+GPIONumber);
	}
}
