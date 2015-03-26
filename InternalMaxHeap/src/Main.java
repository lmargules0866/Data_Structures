/**
 * Author: Luke J. Margules
 * Date: 3/26/2015
 * 
 * The main class of my InternalMaxHeap program controls the opening, closing and reading of the filestreams in order to add, 
 * delete, and empty the heap.
 */

import java.io.*;
import java.text.NumberFormat;

public class Main {

	public static void main(String[] args) throws IOException {
		// Open the InputStream, TaskList files and create a log file
		BufferedReader inputStream = new BufferedReader(new FileReader("InputStream.csv"));
		String line = inputStream.readLine();
		
		BufferedReader taskList = new BufferedReader(new FileReader("TaskList.csv"));
		String tl = taskList.readLine();
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Log.txt", false)));	
		
		// Create a new priority queue to build the heap
		PriorityQueue pq = new PriorityQueue();
		
		String [] fields;
		String name;
		String continent;
		String reg;
		int pop;
		int count = 0;
		
		// while the task list has things to read, loop
		while(tl != null){
			
			// if the first character is an 'r' then it must be a removal
			if(tl.charAt(0) == 'r'){
				out.println(tl);
				String [] rfields;
				int num;
				rfields = tl.split(",");
				num = Integer.parseInt(rfields[1]);
				
				// given the specified number of removals, loop, delete and report to log
				for(int i = 0; i < num; i++){
					PriorityQueue.node node = pq.delete(out);
					int pop1 = node.population;
					out.printf("%02d %s %-29s %s %11s %n",i+1," > ",node.country," / ",NumberFormat.getNumberInstance().format(pop1));
				}
				out.println();						
			}
			
			// if first char is a 'b' then it must be a heap built by continent name
			if(tl.charAt(0) == 'b'){
				//parse the task 
				String [] bfields;
				String nameOfContinent;
				bfields = tl.split(",");
				nameOfContinent = bfields[2];
				count = 0;
				//parse the input stream line
				fields = line.split(",");
				name = fields[0];
				continent = fields[1];
				reg = fields[2];
				pop = Integer.parseInt(fields[3]);
				
				// while the continent names match each other, add it to the heap
				while(continent.compareTo(nameOfContinent) == 0){	
					pq.add(pop, name);
					line = inputStream.readLine();
					fields = line.split(",");
					name = fields[0];
					continent = fields[1];
					reg = fields[2];
					pop = Integer.parseInt(fields[3]);
					count ++;
					
				}
				//report to the log file
				out.println(tl);
				out.println(">> OK, " + count + " countries stored in PQ");
				out.println();
				
			}
			
			// if the second char is a 'd' then it must be an add based on region
			if(tl.charAt(1) == 'd'){
				
				// parse task
				String [] afields= tl.split(",");
				String region = afields[2];
				count = 0;
				//parse input stream line
				fields = line.split(",");
				name = fields[0];
				continent = fields[1];
				reg = fields[2];
				pop = Integer.parseInt(fields[3]);
				
				// while the regions match, add it to the heap
				while(reg.compareTo(region) == 0){	
					pq.add(pop, name);
					line = inputStream.readLine();
					// the line is returned null, there is no next region so fill reg with garbage
					if(line != null){
					fields = line.split(",");
					name = fields[0];
					continent = fields[1];
					reg = fields[2];
					pop = Integer.parseInt(fields[3]);
					count ++;
					} else{
						reg = "poop";
						count++;
					}
				}
				// report to log file
				out.println(tl);
				out.println(">> OK, " + count + " countries added in PQ");
				out.println();

			}
			
			// if the first char is an 'e' then it must be empty
			if(tl.charAt(0) == 'e'){
				// call empty and write to log
				out.println("Empty");
				pq.empty(out);
			}
			
			// if the second char is an 'r' then it must be a snapshot. Call snapshot and report to log
			if(tl.charAt(1) == 'r'){
				out.println("ArraySnapshot");
				pq.SnapShot(out);
			}
			
			tl = taskList.readLine();
		
		}
		
		// close everything
		taskList.close();
		inputStream.close();
		out.close();
	}
}
