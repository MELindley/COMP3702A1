package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;




public class Reader {

	/**
	 * <p>
	 * Reads a text file and extracts the environment map
	 * </p>
	 * 
	 * 
	 * @param fileName
	 *            the file to read from.
	 * @return the location graph defined by the file.
	 * @throws IOException
	 *             if there is an error reading from the input file.
	 */
	public static Graph readMap(String fileName) throws Exception{
		HashSet<Vertex> locations = new HashSet<Vertex>();
		HashSet<Edge> edges = new HashSet<Edge>();
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		try{
		//Create the graph to be returned
		Graph result = new Graph(locations,edges);
		//create the vertices(locations) and add them to the graph
		String line = reader.readLine() ;
		while(line != null){
			//parse the line
			String[] lineArray = line.split(";");
			String roadName = lineArray[0].trim();
			int firstVertexId = Integer.parseInt(lineArray[1].split("J")[1].trim());
			int secondVertexId = Integer.parseInt(lineArray[2].split("J")[1].trim());;
			int roadLength = Integer.parseInt(lineArray[3].trim());
			int nLots = Integer.parseInt(lineArray[4].trim());
			//Generate or retrieve the vertices and edge
			Vertex firstVertex = result.getVertexById(firstVertexId) ;
			Vertex secondVertex =result.getVertexById(secondVertexId); 
			if( firstVertex ==null){
				 firstVertex = new Vertex(firstVertexId);
			}
			if(secondVertex == null){
				secondVertex = new Vertex(secondVertexId);
			}
			Edge  road= new Edge(firstVertex, secondVertex,roadName,roadLength, nLots);
			firstVertex.addRoad(road);
			secondVertex.addRoad(road);
			//Add the vertices to the set of locations
			locations.add(firstVertex);
			locations.add(secondVertex);
			//Add the edge to the set of edges
			edges.add(road);
			line = reader.readLine();
		}
		reader.close();
		return result;
		}catch(Exception e1){
			reader.close();
			throw e1;
		}
	}
	/***
	 * Reads a text file and extract the queries
	 * @param fileName text file containing the queries to be solved
	 * @return a List of Query objects
	 * @throws IOException
	 */
	public static List<Query> readQueries(String fileName)throws IOException{
		ArrayList<Query>result = new ArrayList<Query>();
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		try{
			String line =  reader.readLine();
			while(line != null){
				String[] lineArray = line.split(";");
				Query q = new Query(lineArray[0],lineArray[1]);
				result.add(q);
				line = reader.readLine();
			}
			
		}catch(Exception e){
			reader.close();
			throw new IOException("Error reading queries: "+e.getMessage());
		}
		reader.close();
		return result;
	}
	
	public static void writeResult(List<List<Vertex>> results, String fileName, ArrayList<String> startRoad, ArrayList<String> goalRoad) throws Exception{
		/***
		 * 	The	 first component is the path length.
		 * The second component is the path itself, written as a sequence of road and junctions	travelled
		 * separated by	a dash (“-“) sign. 
		 * This	sequence starts	with the initial road and ends	
		 *	with the goal road.	
		 *	If there is	no	path that solves the query,	the	written	solution should	be no-path.
		 */
		System.out.println("Writing result to file");
		try{
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			for(int i =0;i<results.size();i++){
				//For each query answer
				String toprint ="";
				List<Vertex>r = results.get(i);
				float distance = r.get(r.size()-1).getPathCost();
				toprint += distance+ " ; ";
				//Start by adding the first road name;
				toprint+= startRoad.get(i);
				//iterate over r[1,r.size()-2] first and last vertex are not true vertices (have been manually added)
				for(int j = 1; j<r.size()-1; j++){
					Vertex v = r.get(j);
					List<Edge>roads = v.getRoads();
					String toTake= null;
					for(Edge road: roads){
						if(road.contains(r.get(j+1))){
							toTake = road.getName();
						}
					}
					toprint += " - "+"J"+v.getId()+" - "+toTake;
				}
				
				toprint = toprint.replace("null", goalRoad.get(i));
				
				writer.println(toprint);
			}
			writer.close();
			System.out.println("Done !");
		}catch(Exception e){
			throw new Exception(e);
		}
	}
}

