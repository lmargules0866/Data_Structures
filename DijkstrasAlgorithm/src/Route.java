/**
 * Author: Luke Margules
 * Date: 4/24/2015
 * 
 * 
 * 
 * route is responsible for determining the minimum cost path between two given cities. This class uses dijkstras algorithm to
 * find the path. 
 * 
 */

import java.io.*;
import java.util.*;

public class Route {
	private int [] distance, predecessor;
	private boolean [] included;
	private MapData mp;
	int n;
	PrintWriter out;
	private Stack<String> answerStack;
	
	// constructor that initializes arrays and sets appropriate variables
	Route(MapData mp, PrintWriter pw){
		this.mp = mp;
		this.n = mp.getN();
		this.out = pw;
		distance = new int[n];
		predecessor = new int[n];
		included = new boolean[n];
		answerStack = new Stack<String>();
	}
	
	// given two cities find the shortest route
	public void findShortestRoute(int cityA, int cityB) throws IOException{
		short findRoute = findRoute(cityA, cityB);
		if(findRoute == -1){
			out.println();
			out.println("ERROR - no path available");
		}
		else{
			showAnswers(cityA,cityB);	
		}
	}
	
	public short findRoute(int cityA, int cityB) throws IOException {
		initializeArrays(cityA); 
		int numOfCountries = 1;
		short target = Short.MAX_VALUE;
		short min = Short.MAX_VALUE;
		out.println();
		out.print("TRACE OF TARGETS: " + mp.getCityName(cityA));
		
		// while the destination is not included
		while(included[cityB] == false){
			
			target = Short.MAX_VALUE;
			min = Short.MAX_VALUE;
			// find the next best target
			for (int i = 0; i < n-1; i++) {
				if (included[i] == false) {
					if (distance[i] < min && distance[i] != 0) {
						target = (short) i;
						min = (short) distance[i];
					}
				}
			}
			// if there is no target edge then return -1
			if (target == Short.MAX_VALUE) {
				return -1;
			}
			
			numOfCountries ++;
			out.print(" " + mp.getCityName(target));
			included[target] = true;
			
			//for all elements
			for (short i = 0; i < n - 1; i++) {
				
				//if it's not included
				if (included[i] == false) {
					
					//if the road distance is valid
					if (mp.getRoadDistance(target, i) > 0 && mp.getRoadDistance(target, i) < Short.MAX_VALUE) {
						
						//if the target is less than the previous ceiling
						if (distance[target] + mp.getRoadDistance(target, i) < distance[i]) {
							
							//it's the new celing
							distance[i] = (short) (distance[target] + mp.getRoadDistance(target, i));
							predecessor[i] = target;
						}
					}
				}
			}
		}
		out.print("(" + numOfCountries + " cities)");
		out.println();
		return (short) cityB;
	}
	
	private void showAnswers(int cityA, int cityB) throws IOException {
		
		short numPathTargets = 0;
		// if it's maxval then the distance is 0
		if(distance[cityB] == Short.MAX_VALUE){
			distance[cityB] = 0;
		}
		// report distance and set predecessor of parent to -1 to know when to stop
		out.println("TOTAL DISTANCE: " + distance[cityB] + " miles");
		predecessor[cityA] = -1;
		// add cities on to a stack from the predecessor array to be able to read them back in correct order
		for (int i = cityB; predecessor[i] != -1; i = predecessor[i]) {
			String name = mp.getCityName(i);
			answerStack.push(name);
			numPathTargets++;
		}
		
		answerStack.push(mp.getCityName(cityA));
		
		out.println();
		out.print("SHORTEST ROUTE: ");
		// pop the stack to get the correct path
		while (numPathTargets >= 0) {
			if (numPathTargets > 0) {
				out.write(String.format("%s > ", answerStack.pop()));
			} else {
				out.write(String.format("%s", answerStack.pop()));
				out.write("\n");
			}
			numPathTargets--;
		}
	}
	
	private void initializeArrays(int cityA) throws IOException {	
		// obviously the start city is included
			included[cityA] = true;
		// set entire included array to false except for start
		// get distances for each city 
			for(int j = 0; j< n-1; j++){
				if(j != cityA){
					included[j] = false;
				}
				distance[j] = mp.getRoadDistance(cityA, j);
				if (distance[j] == Short.MAX_VALUE) {
					predecessor[j] = -1;
				} else {
					predecessor[j] = cityA;
				}
			}
			
		}
}
