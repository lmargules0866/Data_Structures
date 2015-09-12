/**
 * Author: Luke Margules
 * Date: 4/24/2015
 * 
 * 
 * PrettyPrint displays the binary matrix and the city names in the correct order for a pleasent view of everything
 * 
 */

import java.io.*;

public class PrettyPrint {
	
	public static void main(String[] args) throws IOException {
		// open files and such
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Log.txt", true)));
		BufferedReader cityNames = new BufferedReader(new FileReader("CityNames.txt"));
		String filePath = "MapData_BigEndian.bin";
		RandomAccessFile raf = new RandomAccessFile(filePath, "r");
		MapData md = new MapData();
		int n = raf.readShort();
		int upN = raf.readShort();
		// report the header info
		out.println("MapData_BigEndian.bin & CityNames.txt FILES – N: " + n + ", UP-N:  " + upN);
		out.println();
		// create the tables
		for (int i = 0; i < n; i++) {
			out.write(String.format("   %4d", i));
		}
		out.println();
		out.print("     -----------------------------------------------------------------------------------" + 
		"-------------------------------------------------------------------------------------------------------" +
		"--------------------------------------------------------------------------------------------------------" +
		"-----------------------------------------------------------------------");
		for (int i = 0; i <n; i++) {
			out.write(String.format("\n%02d |", i));
			for(int j = 0; j<n; j++){
				if(i==j){
					out.write(String.format("%6s", "XXX"));
				}
				if(i>j){
					final int SIZE_OF_HEADER = (Short.SIZE*2)/8;
					int offset = (((n*(n - 1)/2) - ((n - i)*((n - i)-1)/2) - i - 1 + j)*2)+SIZE_OF_HEADER;
					raf.seek(offset);
					short s = raf.readShort();
					if(s == Short.MAX_VALUE){
						out.write(String.format("%7s", ",,,"));
					}
					else{
						out.write(String.format("%7d", s));
						}
				}
			}
		}
		
		out.println();
		out.println();
		// now create the city list. Start with upper pen and then the bridge and then lower
		out.println("----------------UP CITIES");
		for(int i = 0; i <upN; i++){
			out.println(String.format("%02d", i) + ") " + md.getCityName(i));
		}
		
		out.println("----------------THE BRIDGE");
		out.println(String.format("%02d", upN) + ") " + md.getCityName(upN));
		
		out.println("----------------LP CITIES");
		for(int j = upN+1; j<n; j++){
			out.println(String.format("%02d", j) + ") " + md.getCityName(j));
		}
		// close everything
		raf.close();
		cityNames.close();
		out.close();
	}


}
