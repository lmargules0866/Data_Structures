/**
 * Author: Luke Margules
 * Date written: 4/9/2015
 * 
 * CodeIndex is an OOP class used to read a single node of the external B+ tree (stored in CodeIndex.bin) and
 * search it for a given code, or display all the country data by sequentially calling all of the leaf nodes
 * 
 */

import java.io.*;

public class CodeIndex {
	
	// open the binary file for reading
	private String filePath = "CodeIndex.bin";
	private RandomAccessFile raf = new RandomAccessFile(filePath, "r");
	private int offset = 0;
	private short SIZE_OF_HEADER = 0;
	private short SIZE_OF_RECORD = 0;
	private node NODE = new node();
	short m = 0;
	short rootptr = 0;
	short firstLeaf = 0;
	short nextEmptyRRN = 0;
	short nKV = 0;
	int numOfIOs = 0;
	int numOfKeyComparisons = 0;
	
	// Constructor reads the header information into the appropriate variables and calculates the header size and record size
	CodeIndex() throws Exception{
		
		for(int i = 0; i<5; i++){
			switch(i){
			case(0):	m = raf.readShort();
						break;
			
			case(1):	rootptr = raf.readShort();
						break;
			
			case(2):	nextEmptyRRN = raf.readShort();
						break;
			
			case(3):	firstLeaf = raf.readShort();
						break;
			
			case(4):	nKV = raf.readShort();
						break;
			}
		}
		
		SIZE_OF_HEADER = (Short.SIZE*5)/8;
		SIZE_OF_RECORD = (short) (((Byte.SIZE *((m*3)+1)) + (Short.SIZE*(m+1)))/8);
	}
	
	// given a country code, find the DRP needed to find it in the country data file
	public short selectByCode(String country){
		short ret = 0;
		numOfIOs = 0;
		numOfKeyComparisons = 0;
		
		// start off by reading the root node and searching that for the wanted country
		readOneRec(setOffset(rootptr));
		ret = SearchOneNode(country);
		
		// while the node is not a leaf node and a 0 wasn't returned from the search, 
		// keep searching for the country
		while(NODE.NorL == 'N' & ret != 0){
			readOneRec(setOffset(ret));
			ret = SearchOneNode(country);
		}
		// if it's finally a leaf node, do one last search.
		// if 0 was returned, return -1 else return the drp
		if(NODE.NorL == 'L'){
			ret = SearchOneNode(country);
		}
		if(ret == 0){
			return -1;
		}
		else
		{
		return ret;
		}	
	}
	
	// SearchOneNode searches the current node for the specifed country
	public short SearchOneNode(String country){
		// i,j and k are used to name the index of the country char array
		// Each country takes up three indices of the array
		// convert the country char array into a string and compare to needed country
		int countryNumber = 0;
		int i = 0;
		int j = 1;
		int k = 2;
		char [] countryNode = {NODE.countryArray[i], NODE.countryArray[j], NODE.countryArray[k]};
		String nodeCountry = new String(countryNode);
		
		// if the need country is greater than the current country, make sure it's not a filler country
		// and then increment the i,j,k variables and move to the next country
		while(country.compareTo(nodeCountry) > 0){
			numOfKeyComparisons ++;
			if(nodeCountry.compareTo("^^^") == 0){
				numOfKeyComparisons ++;
				return 0;
			}
			else
			{
				for(int q = 0; q<3; q++){
					i++;
					j++;
					k++;
				}
				countryNode[0] = NODE.countryArray[i];
				countryNode[1] = NODE.countryArray[j];
				countryNode[2] = NODE.countryArray[k];
				countryNumber++;
				nodeCountry = new String(countryNode);	
			}
			
		}
		// if you're on a leaf and the country still doesn't match then it wasn't in the data
		if(NODE.NorL == 'L' & country.compareTo(nodeCountry) < 0 ){
			numOfKeyComparisons ++;
			return 0;
		}
		// else return the corresponding drp
		numOfKeyComparisons ++;
		return NODE.treeOrRecPointers[countryNumber];
	}
	
	// close the file
	public void finishUp() throws IOException{
		raf.close();
	}

	// given a drp calulate that offest
	public int setOffset(int i){
		offset = (SIZE_OF_HEADER) + (SIZE_OF_RECORD*(i-1));
		return offset;
	}
	
	// readOneRec reads one AND ONLY ONE record into memory for searching and reporting
	public void readOneRec(int offset){
		// clear node and ios
		NODE.clearNode();
		numOfIOs ++;
		// seek to the needed spot and add data to appropriate node fields
		try {
			
			raf.seek(offset);
			NODE.setNorL((char)raf.readByte());
			NODE.setNextLeaf(raf.readShort());
			
			for(int j = 0; j<m; j++){
				for(int k = 0; k<3; k++){
					NODE.insertCountry(raf.readByte());
				}
			}
			
			for(int z = 0; z < m; z++){
				NODE.insertTreeOrRecPointer(raf.readShort());
			}
		}
		
		catch (Exception e) {
			System.out.println("file error " + e);
		}	
	}
	
	// Select All reads all of the country data into the log file
	// calls all of the leaf nodes like you would a linked list
	public void SelectAll(PrintWriter pw) throws Exception{
			// calculate the offset to the first leaf and read that record
			int offset = setOffset(firstLeaf);	
			readOneRec(offset);
			DataStorage ds = new DataStorage();
			int num = 0;
			// create the first country in the first leaf node
			int countryNumber = 0;
			int i = 0;
			int j = 1;
			int k = 2;
			char [] countryNode = {NODE.countryArray[i], NODE.countryArray[j], NODE.countryArray[k]};
			String nodeCountry = new String(countryNode);
			
			// while there's still another leaf and the tree or rec pointer isn't 0 (meaning the country isn't "^^^")
			// read that country's drp and use a DataStorage object to find the country information
			while(NODE.nextLeaf !=0){
				i = 0;
				j = 1;
				k = 2;
				countryNumber = 0;
				
				while(NODE.treeOrRecPointers[countryNumber]!= 0){
					String line =  ds.findCountry(NODE.treeOrRecPointers[countryNumber]);
					String[] fields = line.split(",");
					pw.printf("%s %s %s %s %n", fields[1],fields[2],fields[3],fields[0]);
					countryNumber++;
					countryNode[0] = NODE.countryArray[i];
					countryNode[1] = NODE.countryArray[j];
					countryNode[2] = NODE.countryArray[k];
					num ++;
					nodeCountry = new String(countryNode);	
				}
				offset = setOffset(NODE.nextLeaf);	
				readOneRec(offset);
			}
			
			// do one more iteration of the above loop because even though there's no next leaf,
			// the data from this node still needs to be accounted for
			i = 0;
			j = 1;
			k = 2;
			countryNumber = 0;
			while(NODE.treeOrRecPointers[countryNumber]!= 0){
				String line =  ds.findCountry(NODE.treeOrRecPointers[countryNumber]);
				String[] fields = line.split(",");
				pw.printf("%s %s %s %s %n", fields[1],fields[2],fields[3],fields[0]);
				num++;
				countryNumber++;
				countryNode[0] = NODE.countryArray[i];
				countryNode[1] = NODE.countryArray[j];
				countryNode[2] = NODE.countryArray[k];
				nodeCountry = new String(countryNode);	
			}
			pw.printf("%s%d %s", "++++++++++++++++++++++++++ END OF DATA (",num,"countries)");
			
	
	}

	// internal node class used for reading in a single node
	class node{
		private char NorL;
		private short nextLeaf;
		int MAX_M = 10;
		char [] countryArray = new char[MAX_M*3];
		int nextEmptyCountry = 0;
		short [] treeOrRecPointers = new short[MAX_M];
		int nextEmptyTreeOrRec =0;

		// Insert country into the countryArray. 
		// Each country code will take up 3 indices in the array (that's why 
		// the size of the array is m*3)
		// increment the next empty
		public void insertCountry(byte countryByte){
			countryArray[nextEmptyCountry] = (char)countryByte;
			nextEmptyCountry++;	
		}
		
		// Insert the tree or record pointer into the array and increment
		public void insertTreeOrRecPointer(short tOrR){
			treeOrRecPointers[nextEmptyTreeOrRec] = tOrR;
			nextEmptyTreeOrRec++;	
		}
		
		public void setNextLeaf(short next){
			nextLeaf = next;
		}
		
		public void setNorL(char norl){
			NorL= norl;
		}
		
		// set the next empties to zero so new data is placed correctly in the array
		public void clearNode(){
			nextEmptyCountry = 0;
			nextEmptyTreeOrRec = 0;
		}
		
	}
	
	
	
}
