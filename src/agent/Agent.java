package agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import model.*;


public class Agent {
	
	
	//The MAp of Locations
	private Graph locMap;
	//List of queries to execute
	private List<Query>toExecute;
	//List of results
	private List<List<Vertex>>results;
	//path to output file
	private String outputFile;

	
	/**
	 * Agent constructor
	 * @param locMap the graph
	 * @param toExecute List OF queries
	 */
	
	public Agent(String graphFile, String queryFile, String outputFile){
		try{
			this.outputFile = outputFile;
			this.locMap= Reader.readMap(graphFile);
			this.toExecute = Reader.readQueries(queryFile);
			this.results = new ArrayList<List<Vertex>>(toExecute.size());
		}catch(Exception e){
			System.out.print(e.getMessage());
		}
	}
	public Agent(Graph locMap, List<Query> toExecute, String outputFile){
		this.outputFile = outputFile;
		this.locMap = locMap;
		this.toExecute = toExecute;
		this.results = new ArrayList<List<Vertex>>(toExecute.size());
	}
	
	/**
	 * Function to use for the Uniform cost search of optimal path
	 * @param initId Id of the root vertex for this path
	 * @param goalId Id of the goal vertex for this path
	 * @return ArrayList of vertices describing the optimal path found by the algorithm
	 */
	private ArrayList<Vertex> useUniform(int initId, int goalId){
		//Retrieve Start vertex
		Vertex start = locMap.getVertexById(initId);
		//Set Path Cost for start to 0
		start.setPathCost(0);
		//Retrieve goal vertex
		Vertex goal = locMap.getVertexById(goalId);
		//A map of vertex to vertex, eventually will contain the most efficient previous step.
		HashMap<Vertex,Vertex> path = new HashMap<Vertex,Vertex>();
		//Ensure that the start end goal vertices are valid
		if(start == null || goal == null){
			return buildPath(path,start);
		}
		//PQ holding veritces to explore
		PriorityQueue<Vertex>toExplore = new PriorityQueue<Vertex>(locMap.getNumberOfLocation(),
				 new PathCostComparator());
		//HashSet Holding vertices explored
		HashSet<Vertex>explored = new HashSet<Vertex>();
		//Add the starting node to the toExplore Queue
		toExplore.offer(start);
		//Loop
		while(!toExplore.isEmpty()){
			//remove first element from the PQ
			Vertex v = toExplore.remove();
			//add it to the explored set
			explored.add(v);
			//check that the current vertex isn't the goal vertex
			if( v.equals(goal)){
				//if it is return the path from v to the start
				return buildPath(path,v);
			}else{
				//Loop through the Vertices connected to the current vertex
				for(Edge e: v.getRoads()){
					//Retrieve the other vertex of the edge
					Vertex child= e.getOther(v);
					/*
					 * ===========UPDATED=============
					 * In order for elements in the pq to be updated the element must be reinserted in the PQ
					 * Hence we skip the check to see if child is in pq, as regardless of wether it is or not we will have to add it
					 * ===============================
					 */
					if(!explored.contains(child)){
						
						if(child.getPathCost()<e.getWeight()+v.getPathCost()){
								continue;
						}else{
							//We have found a better path to child, add or update child in the PQ
							child.setPathCost(e.getWeight()+v.getPathCost());
							toExplore.remove(child);
							toExplore.add(child);
							path.put(child, v);
						}
					}
				}	
			}
		}
		return buildPath(path,start);
	}
	
//	private ArrayList<Vertex> useA (int initId, int goalId){
//		//Retrieve Start vertex
//		Vertex start = locMap.getVertexById(initId);
//		//Set Path Cost for start to 0
//		start.setPathCost(0);
//		//Retrieve goal vertex
//		Vertex goal = locMap.getVertexById(goalId);
//		start.setH(calculateHeur(start, goal));
//		start.setF(start.getH());
//		//A map of vertex to vertex, eventually will contain the most efficient previous step.
//		HashMap<Vertex,Vertex> path = new HashMap<Vertex,Vertex>();
//		//Ensure tha the start end goal vertices are valid
//		if(start == null || goal == null){
//			return buildPath(path,start);
//		}
//		//PQ holding veritces to explore
//		PriorityQueue<Vertex>toExplore = new PriorityQueue<Vertex>(locMap.getNumberOfLocation(),
//				 new FComparator());
//		//HashSet Holding vertices explored
//		HashSet<Vertex>explored = new HashSet<Vertex>();
//		//Add the starting node to the toExplore Queue
//		toExplore.offer(start);
//		//Loop
//		while(!toExplore.isEmpty()){
//			//remove first element from the PQ
//			Vertex v = toExplore.remove();
//			//add it to the explored set
//			explored.add(v);
//			//check that the current vertex isn't the goal vertex
//			if( v.equals(goal)){
//				//if it is return the path from v to the start
//				return buildPath(path,v);
//			}else{
//				//Loop through the Vertices connected to the current vertex
//				for(Edge e: v.getRoads()){
//					//Retrieve the other vertex of the edge
//					Vertex child= e.getOther(v);
//					//if the child vertex is not in explored
//					/*
//					 * =========UPDATED=====
//					 * SAme as per Uniform skip the PQ.contains child check
//					 * =====================
//					 */
//					if(!explored.contains(child)){
//						if(child.getPathCost()<e.getWeight()+v.getPathCost()){
//								continue;
//						}else{
//							//We have found a better path to child, updated/add it ot the PQ 
//							child.setPathCost(e.getWeight()+v.getPathCost());
//							child.setH(calculateHeur(child,goal));
//							child.setF(child.getH()+child.getPathCost());
//							toExplore.remove(child);
//							toExplore.offer(child);
//							path.put(child, v);
//						}
//						
//					}
//				}
//			}	
//		}
//		return buildPath(path,start);
//	}
	/**
	 * Calculates the heuristic for Vertex v
	 * 
	 * h(v) = the SUM of the 2 next smallest edges
	 * @param v Vertex to estimate
	 * @param goal Vertex set as goal
	 * @return the value of h(v) 
	 */
	private float calculateHeur(Vertex v, Vertex goal){
		float h = Float.MAX_VALUE;
		Vertex lookahead = new Vertex();
		float smallest1 =Float.MAX_VALUE, smallest= Float.MAX_VALUE;
		
		for(Edge e: v.getRoads()){
			/*if(e.getOther(v).equals(goal)){
				return e.getWeight();
			}*/
			if(smallest>e.getWeight()){
				smallest = e.getWeight();
				lookahead =e.getOther(v);
			}
		}
		for(Edge e: lookahead.getRoads()){
			if(smallest1>e.getWeight())
				smallest1 = e.getWeight();
		}
		h = smallest+smallest1;//+calculateHeur(lookahead,goal);
		//System.out.println("Heuristic for: "+v+" = "+h);
		return h;//+calculateHeur(lookahead, goal);
	}
	/**
	 * A simple vertex comparator by pathCost.
	 * @author marco
	 */
	private class PathCostComparator implements Comparator<Vertex>{
		@Override
		public int compare(Vertex i, Vertex j) {
			//System.out.println("Using path cost comp");
			if(i.getPathCost()>j.getPathCost())
				return 1;
			if(i.getPathCost()<j.getPathCost())
				return -1;
			return 0;
			
        }
	}
	
	/**
	 * A Simple vertex comparator by f(Vertex)
	 *
	 * @author marco
	 */
	private class FComparator implements Comparator<Vertex>{
		@Override
		public int compare(Vertex i, Vertex j) {
			//System.out.println("Using F cost comp");
			if(i.getF()>j.getF())
				return 1;
			if(i.getF()<j.getF())
				return -1;
			return 0;
        }
	}

	public Graph getLocMap() {
		return locMap;
	}
	public List<Query> getToExecute() {
		return toExecute;
	}
	public List<List<Vertex>> getResults() {
		return results;
	}
	public String getOutputFile() {
		return outputFile;
	}
	public void setLocMap(Graph locMap) {
		this.locMap = locMap;
	}
	public void setToExecute(List<Query> toExecute) {
		this.toExecute = toExecute;
	}
	public void setResults(List<List<Vertex>> results) {
		this.results = results;
	}
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
	/**
	 * Reconstruct optimal path from curr vertex to start vertex;
	 * @param path Hashmap mapping a Vertex to its best path ancestor.
	 * @param curr
	 * @return
	 */
	private ArrayList<Vertex>buildPath(HashMap<Vertex,Vertex> path,Vertex curr){
		ArrayList<Vertex> result = new ArrayList<Vertex>();
		result.add(curr);
		Vertex i = curr;
		//System.out.println("End at Vertex: "+i);
		int counter = 1;
		while(path.keySet().contains(i)){
			i = path.get(i);
			//System.out.println("At Depth "+(counter++)+" Take Vertex: "+i);
			result.add(i);
		}
		Collections.reverse(result);
		return result;
	}
	
	
	public void reInitMap(){
		Graph graph = this.getLocMap();
		for(Vertex v: graph.getJunctions()){
			v.setF(Integer.MAX_VALUE);
			v.setH(0);
			v.setPathCost(Integer.MAX_VALUE);
		}
	}
	
	public static void main(String [] args) throws Exception{
		String environmentFile =args[0];
		String queryFile = args[1];
		String outputFile = args[2];
		Agent agent = new Agent(environmentFile,queryFile,outputFile);
		for(Query q: agent.getToExecute()){
//			switch (q.getType()) {
//				case A:
//					agent.getResults().add(agent.useA(q.getInitID(), q.getGoalID()));
//				break;
//				case UNIFORM:
				agent.getResults().add(agent.useUniform(q.getInitID(), q.getGoalID()));
//				break;
				
			//}
			//Reinitialise the graph
			agent.reInitMap();
		}
		try{
		Reader.writeResult(agent.getResults(), agent.getOutputFile());
		}catch(Exception e){
			throw new Exception(e);
		}
	}
}
	
	

