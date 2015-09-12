/**
 * Author: Luke Margules
 * Date: 4/24/2015
 * 
 * 
 * MapData is in charge of opening the citynames.txt file along with the mapdata binary file (supplied by Daniel)
 * service methods return requested information about cities.
 *
 */

import java.io.*;
import java.util.*;

public class MapData {

	private String filePath;
	private RandomAccessFile raf;
	private BufferedReader cityNames;
	private ArrayList <String> cities;
	short n;
	short upN;
	
	// constructor which opens files and reads in header information
	MapData() throws IOException{
		filePath = "MapData_BigEndian.bin";
		raf = new RandomAccessFile(filePath, "r");
		cityNames = new BufferedReader(new FileReader("CityNames.txt"));
		
		n = raf.readShort();
		upN = raf.readShort();
		String line = cityNames.readLine();
		cities = new ArrayList<>();
		
		//add cities to the array list
		while(line != null){
				cities.add(line);
				line = cityNames.readLine();
		}	
		
	}
	
	// given a city name, traverse the array list and find the corresponding index number for that city
	public int getCityNum(String cityName){
		boolean found = false;
		String c = null;
		int index = 0;
		
		while(found != true){
			for(int i = 0; i < cities.size(); i++){
				c = cities.get(i);
				if(cityName.compareTo(c) == 0){
					index = i;
					found = true;
				}
			}
			if(found == false){
				index = -1;
				found = true;
			}	
		}
		return index;	
	}
	
	// determine what peninsula the city is on my getting the city number and checking to see if it is above the bridge or below
	public String getPeninsula(String cityName){
		
		int num = getCityNum(cityName);
		String ret = null;
		if(num == -1){
			ret = null;
		}
		else if(num<=14){
			ret = "up";
		}
		else if(num > 14){
			ret = "lp";
		}
		
		return ret;
	}
	
	// return city name given city number
	public String getCityName(int cityNum){
		return cities.get(cityNum);
	}
	
	// return the distance between two given cities
	public short getRoadDistance(int cityA, int cityB) throws IOException{
		int offset = 0;
		// if the first city is bigger than the second city number switch them
		if(cityA > cityB){
			int temp = cityA;
			cityA = cityB;
			cityB = temp;
		}
		int a = cityA;
		int b = cityB;
		// calculate offset, seek, and return that short
		final int SIZE_OF_HEADER = (Short.SIZE*2)/8;
		offset = (((n*(n - 1)/2) - ((n - a)*((n - a)-1)/2) - a - 1 + b)*2)+SIZE_OF_HEADER;
		raf.seek(offset);
		
		return raf.readShort();
	}
	
	public int getN(){
		return n;
	}
	
	public void finishUp() throws IOException{
		raf.close();
		cityNames.close();	
	}
	
	
	
}
