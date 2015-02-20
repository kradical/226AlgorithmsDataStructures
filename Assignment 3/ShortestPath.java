/* ShortestPath.java

   ###
   Konrad Schultz
   V00761540
   ###
   notes
   The ShortestPath method returns the shortest path between nodes 0 and 1,
   if no such path exists it returns -1
   ###

   CSC 226 - Fall 2014
   Assignment 3 - Template for Dijkstra's Algorithm
   
   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java ShortestPath
	
   To conveniently test the algorithm with a large input, create a text file
   containing one or more test graphs (in the format described below) and run
   the program with
	java ShortestPath file.txt
   where file.txt is replaced by the name of the text file.
   
   The input consists of a series of graphs in the following format:
   
    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>
	
   Entry A[i][j] of the adjacency matrix gives the weight of the edge from 
   vertex i to vertex j (if A[i][j] is 0, then the edge does not exist).
   Note that since the graph is undirected, it is assumed that A[i][j]
   is always equal to A[j][i].
	
   An input file can contain an unlimited number of graphs; each will be 
   processed separately.


   B. Bird - 08/02/2014
*/

import java.lang.Override;
import java.io.File;
import java.lang.System;
import java.util.Scanner;

//Do not change the name of the ShortestPath class
//returns -1 if 1 is not reachable from 0
public class ShortestPath{
	/* ShortestPath(G)
		Given an adjacency matrix for graph G, return the total weight
		of a minimum weight path from vertex 0 to vertex 1.
		
		If G[i][j] == 0, there is no edge between vertex i and vertex j
		If G[i][j] > 0, there is an edge between vertices i and j, and the
		value of G[i][j] gives the weight of the edge.
		No entries of G will be negative.
	*/
	public static class Heap {
		Node[] heap;
		public int current_heapsize;

		public Heap(int number_nodes){
			current_heapsize = 0;
			heap = new Node[number_nodes+1];
		}

		//ADD in heap order use bubble up to maintain heap property
		public void add_in_heap_order(Node node_to_add){
			++current_heapsize;
			heap[current_heapsize] = node_to_add;
			bubble_up(current_heapsize);

		}

		public void swap(int child, int parent){
			Node temp = heap[child];
			heap[child] = heap[parent];
			heap[parent] = temp;
			heap[child].heapindex = child;
			heap[parent].heapindex = parent;
		}

		//auxilliary method to get the node to the right place in the heap
		public void bubble_up(int index){
			//we made it to the min value! return now!
			heap[index].heapindex = index;
			if(index == 1){
				return;
			}
			if(heap[index].distance < heap[index/2].distance) {
				swap(index, index / 2);
				bubble_up(index / 2);
			}
		}

		//DELETE use bubble down to maintain heap property
		public Node delete_min(){
			Node minimum = heap[1];
			heap[1] = heap[current_heapsize];
			heap[1].heapindex = 1;
			heap[current_heapsize] = null;
			bubble_down(1);
			--current_heapsize;
			return minimum;
		}

		//auxilliary method to get the node back down to its rightful place
		public void bubble_down(int index){
			int mindex;
			if(index > (current_heapsize-1)/2){
				return;
			}
			if(heap[index*2+1] == null){
				if(heap[index*2] == null){
					return;
				}
				mindex = index*2;
			}else{
				mindex = (heap[index*2].distance < heap[index*2+1].distance) ? index*2 : index*2+1;
			}

			if(heap[mindex].distance < heap[index].distance){
				swap(mindex, index);
				bubble_down(mindex);
			}
		}

		//adjust_priority bubble up or down to maintain heap property
		public void adjust_priority(int node_to_adjust){
			bubble_down(node_to_adjust);
			bubble_up(node_to_adjust);
		}
	}

	//nodes of the input graph. represented by
	//Identification
	//heapindex (its important to keep track of each nodes position for an O(logn) update priority operation)
	//adjacencies
	//distance from source
	public static class Node {
		public final int nodeID;
		//adjacencies -> a pair of: target node and weight
		public int heapindex;
		public Adjacencies[] adjacent;
		public double distance;

		public Node(int node, double initial_distance, int number_nodes) {
			nodeID = node;
			if (nodeID == 0) {
				distance = 0;
			} else {
				distance = initial_distance;
			}
			adjacent = new Adjacencies[number_nodes];
		}

		//prints relevant information about a node, great for debugging
		public void print_info() {
			System.out.printf("ID: %d distance: %f heapindex: %d\n", nodeID, distance, heapindex);
			for (Adjacencies e : adjacent) {
				if (e != null) {
					System.out.printf("ADJACENT||ID: %d weight: %f heapindex %d\n", e.target_node.nodeID, e.weight, e.target_node.heapindex);
				}
			}
		}

		//tuple containing a target node and the weight to get there (an edge)
		public static class Adjacencies {
			public Node target_node;
			public double weight;

			public Adjacencies(Node out_target, double edge_weight) {
				target_node = out_target;
				weight = edge_weight;
			}
		}
	}

	static int ShortestPath(int[][] G){
		int numVerts = G.length;
		int totalWeight = -1;
		Heap nodeHeap = new Heap(numVerts);
		Node[] all_nodes = new Node[numVerts];

		//set up the heap with nodes with all their information
		//source is 0 in this case and the distance to source starts at 0
		//distance to the rest of the nodes starts at infinity
		for(int i = 0; i < numVerts; i++){all_nodes[i] = new Node(i, Double.POSITIVE_INFINITY, numVerts);}
		for(int i = 0; i < numVerts; i++){
			for(int j = 0; j < numVerts; j++) {
				if(G[i][j] != 0){
					all_nodes[i].adjacent[j] = new Node.Adjacencies(all_nodes[j], G[i][j]);
				}
			}
			nodeHeap.add_in_heap_order(all_nodes[i]);
		}

		//Dijkstra's! Take the minimum node from the heap, check if its the destination
		//(destination is 1 in this case)
		//if not check its adjacencies for lower alternative distances.
		//update its adjacencies priorities and repeat
		while(nodeHeap.current_heapsize > 0){
			Node temp = nodeHeap.delete_min();
			if(temp.nodeID == 1){
				return (int)temp.distance;
			}
			for(Node.Adjacencies e:temp.adjacent){
				if(e != null) {
					double alt_dist = temp.distance + e.weight;
					if (alt_dist < e.target_node.distance) {
						e.target_node.distance = alt_dist;
						nodeHeap.adjust_priority(e.target_node.heapindex);
					}
				}
			}
		}
		return totalWeight;
	}
	
		
	/* main()
	   Contains code to test the ShortestPath function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
	public static void main(String[] args){
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}
		
		int graphNum = 0;
		double totalTimeSeconds = 0;
		
		//Read graphs until EOF is encountered (or an error occurs)
		while(true){
			graphNum++;
			if(graphNum != 1 && !s.hasNextInt())
				break;
			System.out.printf("Reading graph %d\n",graphNum);
			int n = s.nextInt();
			int[][] G = new int[n][n];
			int valuesRead = 0;
			for (int i = 0; i < n && s.hasNextInt(); i++){
				for (int j = 0; j < n && s.hasNextInt(); j++){
					G[i][j] = s.nextInt();
					valuesRead++;
				}
			}
			if (valuesRead < n*n){
				System.out.printf("Adjacency matrix for graph %d contains too few values.\n",graphNum);
				break;
			}
			long startTime = System.currentTimeMillis();
			
			int totalWeight = ShortestPath(G);
			long endTime = System.currentTimeMillis();
			totalTimeSeconds += (endTime-startTime)/1000.0;
			
			System.out.printf("Graph %d: Minimum weight of a 0-1 path is %d\n",graphNum,totalWeight);
		}
		graphNum--;
		System.out.printf("Processed %d graph%s.\nAverage Time (seconds): %.2f\n",graphNum,(graphNum != 1)?"s":"",(graphNum>0)?totalTimeSeconds/graphNum:0);
	}
}