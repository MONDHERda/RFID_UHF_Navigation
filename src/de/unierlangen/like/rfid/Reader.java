package de.unierlangen.like.rfid;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import android.content.SharedPreferences;
import de.unierlangen.like.serialport.SerialPort;

public class Reader {

	SerialPort readerSerialPort;
	public static enum Configuration {DEFAULT, LOW_POWER, HIGH_POWER};
	/**
	 * Constructor opens serial port and performs handshake
	 * @param sp
	 * @throws InvalidParameterException
	 * @throws SecurityException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Reader(SharedPreferences sp) throws InvalidParameterException, SecurityException, IOException, InterruptedException{
		String path = sp.getString("DEVICE", "");
		int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));
		readerSerialPort = new SerialPort(path, baudrate);
		readerSerialPort.writeString("PREVED");
		String response = readerSerialPort.readString();
		
		if (response.contains("MEDVED")){
			//everything is fine
			return;
		} else if (response.contains("error")){
			throw new IOException("Reader MCU reported error:" + response);
		} else if (response.length()!=0){
			throw new IOException("Reader MCU behaves odd, check baudrate: " + response);
		} else {
			throw new IOException("No response from reader");
		}
	}
	
	public void initialize(Configuration configuration) throws IOException{
		//XXX choose configuration from enum
		switch (configuration){
			case LOW_POWER:
				break;
			case HIGH_POWER:
				break;
			case DEFAULT:
			default:
				readerSerialPort.writeString("a");
		}
		
	}
	
	public ArrayList<GenericTag> performRound() throws IOException {
		//Create container
		ArrayList<GenericTag> tags = new ArrayList<GenericTag>();
		//Tell reader MCU to start inventory round
		//readerSerialPort.writeString("z");
		//XXX read the entire message from serialport
		String tagsString = "tags:3;EBA123,14;FA894,1;BEEF666,30;endtags";//readerSerialPort.readString();
		if (tagsString.contains("error")) {
			throw new IOException("Reader MCU reported error:" + tagsString);
		}
		//TODO do something to this string to get tags from it
	
		
		return tags;
	}
	
	public String displayRegisters() throws IOException, InterruptedException{
		readerSerialPort.writeString("q");
		//FIXME this is not the way it should be done, use listener
		String registersString = readerSerialPort.readString();
		return registersString;
	}
}
