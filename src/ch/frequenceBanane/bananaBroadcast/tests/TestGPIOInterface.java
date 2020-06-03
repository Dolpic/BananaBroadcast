package ch.frequenceBanane.bananaBroadcast.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.frequenceBanane.bananaBroadcast.GPIO.GPIOInterface;

public class TestGPIOInterface {
	
	@Test
	public void GPIODoesNotAcceptInvalidParameters() {
		byte[] invalid1 = {0,0,0};
		byte[] invalid2 = {100,100,100,100,100};
		byte[] valid = {10,20,30,40};
		assertThrows(IllegalArgumentException.class, () -> new GPIOInterface(invalid1, 93) );
		assertThrows(IllegalArgumentException.class, () -> new GPIOInterface(invalid2, 93) );
		assertThrows(IllegalArgumentException.class, () -> new GPIOInterface(valid, -2) );
	}
	
	@Test
	public void GPIOCanBeClosedBeforeRunned() {
		byte[] valid = {10,20,30,40};
		GPIOInterface gpio = new GPIOInterface(valid, 93);
		gpio.close();
	}
	
	@Test
	public void bindGPIODoesNotAcceptInvalidParameters() {
		byte[] valid = {10,20,30,40};
		GPIOInterface gpio = new GPIOInterface(valid, 93);
		assertThrows(IllegalArgumentException.class, 
					 () -> gpio.bindGPIOFunction(-1, 0, ()->{}) );
		assertThrows(IllegalArgumentException.class, 
				     () -> gpio.bindGPIOFunction(0, 0, ()->{}) );
		assertThrows(IllegalArgumentException.class, 
			     () -> gpio.bindGPIOFunction(99999999, 0, ()->{}) );
		assertThrows(IllegalArgumentException.class, 
				 () -> gpio.bindGPIOFunction(1, -1, ()->{}) );
		assertThrows(IllegalArgumentException.class, 
				 () -> gpio.bindGPIOFunction(1, 9999999, ()->{}) );
		assertThrows(IllegalArgumentException.class, 
				 () -> gpio.bindGPIOFunction(1, 1, null) );
	}
}
