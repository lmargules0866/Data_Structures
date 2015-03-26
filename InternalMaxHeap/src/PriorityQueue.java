/**
 * Author: Luke Margules
 * Date: 3/26/2015
 * 
 * The PriorityQueue class is used to create and modify a heap.
 */

import java.io.*;
import java.text.NumberFormat;

public class PriorityQueue {
	private final int MAX_N = 72;
	private int N;
	private node[] heap;
	
	// empty out entire heap by calling delete N times
	public void empty(PrintWriter pw){
		int i = 0;
		int total = N;
		pw.println("N is " + N);
		while(i < total){
			node d = delete(pw);
			int pop1=d.population;
			pw.printf("%02d %s %-29s %s %11s %n",i+1," > ",d.country," / ",NumberFormat.getNumberInstance().format(pop1));
			i++;
		}
		pw.println();
	}
	
	// initialize heap size to 0 and create array
	private void heapInitialize(){
		N = 0;
		heap = new node[MAX_N];
	}
	
	// constructor
	PriorityQueue(){
		heapInitialize();
	}
	
	// if the heap is empty, report else pop top node off and put n-1 node as new root and reheapify (walkDown)
	public node delete(PrintWriter pw){
		if(N==0){
			pw.println("Empty Heap");
			return null;
		}
		if(N==1){
			N--;
			return heap[0];
		}
		else
		{
		node temp = heap[0];
		heap[0] = heap[N-1];
		N--;
		walkDown(0);
		return temp;
		}
	}
	
	// add node to next available index and reheapify (walkUp)
	public void add(int pop, String cntry){
		node node = new node(pop, cntry);
		heap[N] = node;
		N++;
		walkUp(N-1);
	}
	
	// from the specified index, re-sort array to make it into a max heap going from the index upward. 
	private void walkUp(int startFrom){
		int i = startFrom;
		while(i>0){
			int p = (i-1)/2;
			if((heap[i].population > heap[p].population)){
			node temp = heap[p];
			heap[p]=heap[i];
			heap[i] = temp;
			}
			i=(i-1)/2;
		}
	}
	
	// from specified index, re-sort array to make it a max heap going from the index downward
	private void walkDown(int startFrom){
		int i = startFrom;
		int l = 2*i+1;
		while(l<N){
			int max = l;
			int r = l + 1;
			if(r<N-1){
				if(heap[r].population > heap[l].population){
					max++;
				}
			}
			if(heap[i].population < heap[max].population){
				node temp;
				temp = heap[i];
				heap[i] = heap[max];
				heap[max] = temp;
				i = max;
				l = 2*i+1;
			}
			else break;
		}	
	
	}

	// printout entire heap with a for loop
	public void SnapShot(PrintWriter pw){
		pw.println("N is " + N);
		for(int i = 0; i < N; i++){
			int pop1 = heap[i].population;
			pw.printf("%-5s %-29s %s %11s %n","["+i+"]",heap[i].country," / ",NumberFormat.getNumberInstance().format(pop1));
		}
		pw.println();
	}
	
	// simple node class to store data
	class node {	
		int population;
		String country;
		node(int pop, String country){
			this.population = pop;
			this.country = country;
		}
	}
}
