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

	// The MAp of Locations
	private Graph locMap;
	// List of queries to execute
	private List<Query> toExecute;
	// List of results
	private List<List<Vertex>> results;
	// path to output file
	private String outputFile;

	/**
	 * Agent constructor
	 * 
	 * @param locMap
	 *            the graph
	 * @param toExecute
	 *            List OF queries
	 */

	public Agent(String graphFile, String queryFile, String outputFile) {
		try {
			this.outputFile = outputFile;
			this.locMap = Reader.readMap(graphFile);
			this.toExecute = Reader.readQueries(queryFile);
			this.results = new ArrayList<List<Vertex>>(toExecute.size());
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

	public Agent(Graph locMap, List<Query> toExecute, String outputFile) {
		this.outputFile = outputFile;
		this.locMap = locMap;
		this.toExecute = toExecute;
		this.results = new ArrayList<List<Vertex>>(toExecute.size());
	}

	/**
	 * Function to use for the Uniform cost search of optimal path
	 * 
	 * @param initId
	 *            Id of the root vertex for this path
	 * @param goalId
	 *            Id of the goal vertex for this path
	 * @return ArrayList of vertices describing the optimal path found by the
	 *         algorithm
	 */
	private ArrayList<Vertex> useUniform(int initId, int goalId) {
		// Start by creating two new vertices for the graph
		// Retrieve Start vertex
		Vertex start = locMap.getVertexById(initId);
		
		// Set Path Cost for start to 0
		start.setPathCost(0);
		// Retrieve goal vertex
		Vertex goal = locMap.getVertexById(goalId);
		
		// A map of vertex to vertex, eventually will contain the most efficient
		// previous step.
		HashMap<Vertex, Vertex> path = new HashMap<Vertex, Vertex>();
		// Ensure that the start end goal vertices are valid
		if (start == null || goal == null) {
			System.out.println("Invalid start or end vertices !");
			return buildPath(path, start);
		}
		// PQ holding veritces to explore
		PriorityQueue<Vertex> toExplore = new PriorityQueue<Vertex>(locMap.getNumberOfJunctions(),
				new PathCostComparator());
		// HashSet Holding vertices explored
		HashSet<Vertex> explored = new HashSet<Vertex>();
		// Add the starting node to the toExplore Queue
		toExplore.offer(start);
		// Loop
		while (!toExplore.isEmpty()) {
			// remove first element from the PQ
			Vertex v = toExplore.remove();
			// add it to the explored set
			explored.add(v);
			// check that the current vertex isn't the goal vertex
			if (v.equals(goal)) {
				// if it is return the path from v to the start
				return buildPath(path, v);
			} else {
				// Loop through the Vertices connected to the current vertex
				for (Edge e : v.getRoads()) {
					// Retrieve the other vertex of the edge
					Vertex child = e.getOther(v);
					if (!explored.contains(child)) {
						if (child.getPathCost() < e.getWeight() + v.getPathCost()) {
							continue;
						} else {
							// We have found a better path to child, add or
							// update child in the PQ
							child.setPathCost(e.getWeight() + v.getPathCost());
							toExplore.remove(child);
							toExplore.add(child);
							path.put(child, v);
						}
					}
				}
			}
		}
		return buildPath(path, start);
	}

	
	

	/**
	 * A simple vertex comparator by pathCost.
	 * 
	 * @author marco
	 */
	private class PathCostComparator implements Comparator<Vertex> {
		@Override
		public int compare(Vertex i, Vertex j) {
			// System.out.println("Using path cost comp");
			if (i.getPathCost() > j.getPathCost())
				return 1;
			if (i.getPathCost() < j.getPathCost())
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
	 * 
	 * @param path
	 *            Hashmap mapping a Vertex to its best path ancestor.
	 * @param curr
	 * @return
	 */
	private ArrayList<Vertex> buildPath(HashMap<Vertex, Vertex> path, Vertex curr) {
		ArrayList<Vertex> result = new ArrayList<Vertex>();
		result.add(curr);
		Vertex i = curr;
	
		int counter = 1;
		while (path.keySet().contains(i)) {
			i = path.get(i);
			// System.out.println("At Depth "+(counter++)+" Take Vertex: "+i);
			result.add(i);
		}
		Collections.reverse(result);
		return result;
	}

	public void reInitMap() {
		Graph graph = this.getLocMap();
		// Remove the starting and ending location
		graph.removeJunction(graph.getVertexById(-1));
		graph.removeJunction(graph.getVertexById(-2));
		for (Vertex v : graph.getJunctions()) {
			v.setPathCost(Integer.MAX_VALUE);
		}
	}

	public static void main(String[] args) throws Exception {
		final long startTime = System.currentTimeMillis();
		String environmentFile = args[0];
		String queryFile = args[1];
		String outputFile = args[2];
		Agent agent = new Agent(environmentFile, queryFile, outputFile);
		Graph graph = agent.locMap;
		// Create new junctions for the start and goal plot to link them with
		// existing junctions
		
		int startHouseNumber, goalHouseNumber;
		ArrayList<String> startRoadName = new ArrayList<String>() , goalRoadName= new ArrayList<String>();
		for (int i = 0; i<agent.getToExecute().size();i++) {
			Query q = agent.getToExecute().get(i);
			Vertex start = new Vertex(-1);
			Vertex goal = new Vertex(-2);
			System.out.println(q.toString()+"\n");
			
			// Calculate edges to closest junctions for start and goal
			// the distance to move to the next door neighbor is equivalent to
			// (2*roadLength/nLots).
			// Retrieve start house number from query by splitting the string at
			// any character first occurence
			startHouseNumber = Integer.parseInt(q.getInitID().split("[a-zA-Z]+", 2)[0]);
			
			
			
			// Retrieve start road name from query by splitting the string at any
			// digit first occurence
			startRoadName.add(q.getInitID().split("[0-9]+", 2)[1].trim());
			
			
			// Repeat process for the goalhouse
			goalHouseNumber = Integer.parseInt(q.getGoalID().split("[a-zA-Z]+", 2)[0].trim());
			goalRoadName.add( q.getGoalID().split("[0-9]+", 2)[1].trim());
			

			
			// Retrieve start edge, connect start vertex to its junctions and
			// calculate the distance;
			Edge startRoad = graph.getRoadByName(startRoadName.get(i));
			
			// !!!!!!!!!!!!!! Here need to add the half distance from the BLOCK to the JUNCTION !!!!!!!!!11
			// Calculate distance from start to startRoad.StartOfRoad and from start to startRoad.EndOfRoad
			float startToStartOfRoadDistance = (float) startHouseNumber
					* (2 * (float)startRoad.getWeight() / startRoad.getNumberOfPlots());
			float startToEndOfRoadDistance = (float)(startRoad.getNumberOfPlots()-startHouseNumber)
					* (2 * (float)startRoad.getWeight() / startRoad.getNumberOfPlots());

			// create first edge from startRoad.StartOfRoad to start Vertex ie
			// the vertes from start to StartRoad.StartOfRoad with distance
			// calculated above and number of house = goalHouseNumber
			Edge startFirstEdge = new Edge(start, startRoad.getStartOfRoad(), "startToStartEdge", startToStartOfRoadDistance,
					startHouseNumber);
			
			//create second edge from startRoad.EndOfRoad to start vertex
			Edge startSecondEdge = new Edge(start,startRoad.getEndOfRoad(),"startToEndEdge",startToEndOfRoadDistance,(startRoad.getNumberOfPlots()-startHouseNumber));
			
			//add the first and second edge to start vertex
			start.addRoad(startFirstEdge);
			start.addRoad(startSecondEdge);
			//add the first and second edge to the other vertex
			startRoad.getStartOfRoad().addRoad(startFirstEdge);
			startRoad.getEndOfRoad().addRoad(startSecondEdge);
			
			
			//repeat process for goalRoad
			Edge goalRoad = graph.getRoadByName(goalRoadName.get(i));
			float goalToStartOfRoadDistance =goalHouseNumber
					* (2 * (float)goalRoad.getWeight() / goalRoad.getNumberOfPlots());
			float goalToEndOfRoadDistance = (goalRoad.getNumberOfPlots()-goalHouseNumber)
					* (2 * (float)goalRoad.getWeight() / goalRoad.getNumberOfPlots());
			
			Edge goalFirstEdge = new Edge(goal, goalRoad.getStartOfRoad(), "goalToStartEdge", goalToStartOfRoadDistance,
					goalHouseNumber);
			Edge goalSecondEdge = new Edge(goal,goalRoad.getEndOfRoad(),"goalToEndEdge",goalToEndOfRoadDistance,(goalRoad.getNumberOfPlots()-goalHouseNumber));
			goal.addRoad(goalFirstEdge);
			goal.addRoad(goalSecondEdge);
			goalRoad.getStartOfRoad().addRoad(goalFirstEdge);
			goalRoad.getEndOfRoad().addRoad(goalSecondEdge);
			
			//add junctions to graph
			graph.addJunction(start);
			graph.addJunction(goal);
			
			
			agent.getResults().add(agent.useUniform(-1, -2));
			//remove the start and end junctions from the graph
			agent.reInitMap();
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("Done finding the best paths !") ;
		System.out.println("That took "+ (endTime - startTime) + " milliseconds for "+ agent.getToExecute().size()+" queries\n" );
		try {
			Reader.writeResult(agent.getResults(), agent.getOutputFile(),startRoadName, goalRoadName);
		} catch (Exception e) {
			throw e;
		}
	}
}
