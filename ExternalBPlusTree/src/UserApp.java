/**
 * Author: Luke Margules
 * Date written: 4/9/2015
 * 
 * UserApp is a program that opens both the log file and the transaction file. Using a stream processing 
 * algorithm, (I.e. loop till done, only opening file once) do given trasactions by calling appropriate OOP
 * class methods
 * 
 */

import java.io.*;

public class UserApp {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		// create objects for the code idex, and data storage and open the files
		CodeIndex ci = new CodeIndex();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Log.txt", false)));	
		DataStorage ds = new DataStorage();
		
		BufferedReader transData = new BufferedReader(new FileReader("transData8.txt"));
		String line = transData.readLine();
		
		// while there's still transaction data to read loop
		while(line != null){
			// if the first character is an 'S' then it must be a Search
			if(line.charAt(0) == 'S'){
				// print out the line to the log and split the fields 
				out.printf("%s %n", line);
				String [] fields = line.split(" ");
				// given the country code, call appropriate object method 
				short ret = ci.selectByCode(fields[1]);
				//if -1 is returned then there was no country with that id
				if(ret == -1){
					out.printf("%s %d %s %d %s %s %n", ">> " , ci.numOfIOs, " I/O's, " , ci.numOfKeyComparisons,
							" key-comparisons >> " , "CODE NOT FOUND");
				}
				// else print out the country data and read next line
				else{
				String dsLine  = ds.findCountry(ret);
				String [] dsFields = dsLine.split(",");
				out.printf("%s %d %s %d %s %s %s %s %n", ">> " , ci.numOfIOs+1 , " I/O's, " , ci.numOfKeyComparisons,
							" key-comparisons >> " , dsFields[1] , dsFields[2] , dsFields[3]);
				}
				line = transData.readLine();
			}
			// if the first char is an A then it must be search all countries so call appropriate object method and read next line
			if(line.charAt(0) == 'A'){
				out.printf("%s %n", line);
				ci.SelectAll(out);
				line = transData.readLine();
			}
			
		}
		// finish ups and closes
		ci.finishUp();
		out.close();
		ds.finishUp();
		transData.close();
	}

}
