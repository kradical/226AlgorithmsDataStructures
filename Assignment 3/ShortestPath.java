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
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import java.io.File;
import java.util.PriorityQueue;
import java.util.ArrayList;

//Do not change the name of the ShortestPath class
public class ShortestPath{

	/* ShortestPath(G)
		Given an adjacency matrix for graph G, return the total weight
		of a minimum weight path from vertex 0 to vertex 1.
		
		If G[i][j] == 0, there is no edge between vertex i and vertex j
		If G[i][j] > 0, there is an edge between vertices i and j, and the
		value of G[i][j] gives the weight of the edge.
		No entries of G will be negative.
	*/
	public static class Vertex implements Comparable<Vertex> {
		public final int vertex_name;
		public Edge[] adjacencies;
		public double minimum_distance = Double.POSITIVE_INFINITY;
		public Vertex previous = null;

		public Vertex(int vertexID) {
			vertex_name = vertexID;
			if(vertexID == 0){
				minimum_distance = 0; //set source
			}
		}

		public void print_info() {
			System.out.println("Vertex: " + vertex_name + " dist: " + minimum_distance);
			System.out.printf("Adjacencies: \n");
			for(int i = 0; i<adjacencies.length; i++)
				System.out.printf("%d, weight=%d\n", adjacencies[i].target.vertex_name, adjacencies[i].weight);
			if(previous != null)
				System.out.println("previous: "+previous.vertex_name);
		}

		public int compareTo(Vertex other_vertex) {
			return (int)minimum_distance - (int)other_vertex.minimum_distance;
		}
	}

	public static class Edge{
		public final Vertex target;
		public final double weight;

		public Edge(Vertex in_target, double in_weight){
			target = in_target;
			weight = in_weight;
		}
	}

	static int ShortestPath(int[][] G){
		int numVerts = G.length;
		int totalWeight = -1;
		Vertex[] all_vertices = new Vertex[numVerts];
		ArrayList<Edge> temp_edges = new ArrayList<Edge>();
		PriorityQueue<Vertex> vertices = new PriorityQueue<Vertex>(numVerts);

		for(int ndx = 0; ndx < numVerts; ndx++) all_vertices[ndx] = new Vertex(ndx);

		for(int ndx = 0; ndx < numVerts; ndx++){
			for(int i = 0; i<numVerts; i++) {
				if (G[ndx][i] != 0) {
					temp_edges.add(new Edge(all_vertices[i], G[ndx][i]));
				}
			}
			all_vertices[ndx].adjacencies = new Edge[temp_edges.size()];
			all_vertices[ndx].adjacencies = temp_edges.toArray(all_vertices[ndx].adjacencies);
			temp_edges.clear();
			vertices.add(all_vertices[ndx]);
		}

		while(!vertices.isEmpty()){
			Vertex u = vertices.poll();
			if(u.vertex_name == 1)return (int)u.minimum_distance;
			for(Edge e: u.adjacencies){
				Vertex v = e.target;
				double weight = e.weight;
				double alt_distance = u.minimum_distance + weight;
				if(alt_distance < v.minimum_distance){
					vertices.remove(v);
					v.minimum_distance = alt_distance;
					vertices.add(v);
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