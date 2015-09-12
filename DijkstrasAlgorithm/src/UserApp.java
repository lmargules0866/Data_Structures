/**
 * Author: Luke Margules
 * Date: 4/24/2015
 * 
 * 
 * UserApp deals opens the transaction file for processing requests. Opens the log file for status messages and 
 * calls appropriate mapdata methods.
 */
import java.io.*;

public class UserApp {
	
	//declare objects and readers
	private static MapData md;
	private static PrintWriter out;
	private static BufferedReader transStream;
	private static Route r;
	
	public static void main(String[] args) throws IOException {
		md = new MapData();
		out = new PrintWriter(new BufferedWriter(new FileWriter("Log.txt", false)));
		transStream = new BufferedReader(new FileReader("CityPairsTestPlan.txt"));
		r = new Route(md, out);
		String [] fields;
		String cityA;
		String cityB;
		int aNum;
		int bNum;
		String aPen;
		String bPen;
		
		// while there is still something to read in the transaction stream
		String line = transStream.readLine();
		while(line != null){
			// if the line doesn't start with % split the line and assign appropriate info
			if(line.startsWith("%") == false){
				fields = line.split(" ");
				cityA = fields[0];
				cityB = fields[1];
				aNum = md.getCityNum(cityA);
				bNum = md.getCityNum(cityB);
				aPen = md.getPeninsula(cityA);
				bPen = md.getPeninsula(cityB);
				
				// if either of the cities are the bridge make sure they are on the same peninsula
				if(cityA.compareTo("theBridge") == 0){
					aPen = bPen;
				}
				if(cityB.compareTo("theBridge") == 0){
					bPen = aPen;
				}
				
				// report appropriately
				out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				// if there's no start city then error message
				if(aNum == -1){
					out.println("START CITY: " + cityA);
					out.println("ERROR: not in Map Data");
				}
				// else report first city info and check if there's a second city
				else{
					out.println("START CITY: " + cityA + " (" + md.getCityNum(cityA) + ") " + aPen);
					if(bNum == -1){
						out.println("DESTINATION: " + cityB);
						out.println("ERROR: not in Map Data");
					}
					// if there's a second city report info 
					else{
						out.println("DESTINATION: " + cityB + " (" + md.getCityNum(cityB) + ") " + bPen);
						// if they are on the same peninsula then just find the shortest cost path
						if(aPen == bPen){	
							r.findShortestRoute(aNum, bNum);	
						}
						// if they're not on the same peninsula then divide it up into two parts
						else{	
							out.println("*** different peninsulas, so 2 partial routes shown ***");											
							r.findShortestRoute(aNum, 14);	
							r.findShortestRoute(14, bNum);	
						}
					}	
				}	
			}	
			// increment the line 
			line = transStream.readLine();
		}
		finishUp();
				
	}
	
	public static void finishUp() throws IOException{
		md.finishUp();
		out.close();
		transStream.close();
	}

}
