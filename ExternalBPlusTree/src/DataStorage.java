/**
 * Author: Luke Margules
 * Date written: 4/9/2015
 * 
 * Data Storage is an OOP class used to find country data within the CountraData.csv file. 
 * Searching for each country will be direct address given an RRN found from some other class (codeIndex)
 * 
 */

import java.io.*;

public class DataStorage {
	
	// Open the file for reading and create the record size variable
	String filePath = "CountryData.csv";
	private RandomAccessFile raf = new RandomAccessFile(filePath, "r");
	int recSize = 28;
	
	// empty constructor 
	DataStorage() throws Exception{

	}
	
	//given the rrn, calculate the offset and seek to that point and return the given line
	public String findCountry(short rrn) throws IOException{
		raf.seek((rrn-1) * recSize);
		String line =  raf.readLine();
		return line;
		
	}
	
	//close that file
	public void finishUp() throws IOException{
		raf.close();
	}

}
