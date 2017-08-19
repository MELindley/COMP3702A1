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
	 * Reads a text file called fileName that the environment map
	 * </p>
	 * 
	 * <p>
	 * The first line of the file contains a single positive integer, denoting
	 * the number n of rows in the matrix. The rest of the file contains the matrix
	 * of n rows, the element [i,j] of the matrix defines the weight of the edge between
	 * I&J
	 * </p>
	 * 
	 * 
	 * @param fileName
	 *            the file to read from.
	 * @return the Graph defined by the matrix read from the file.
	 * @throws IOException
	 *             if there is an error reading from the input file.
	 */
	public static Graph readMap(String fileName) throws IOException{
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
			String roadName = lineArray[0];
			int firstVertexId = Integer.parseInt(lineArray[1].split("J")[1]);
			int secondVertexId = Integer.parseInt(lineArray[2].split("J")[1]);;
			int roadLength = Integer.parseInt(lineArray[3]);
			int nLots = Integer.parseInt(lineArray[4]);
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
		}
		reader.close();
		return result;
		}catch(Exception e1){
			reader.close();
			throw new IOException("Error reading graph: " +e1.getMessage());
		}
	}
	
	public static List<Query> readQueries(String fileName)throws IOException{
		ArrayList<Query>result = new ArrayList<Query>();
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		try{
			
			String line =  reader.readLine();
			for(int i =0 ;i<numberOfQuery;i++){
				line = reader.readLine();
				String[] toProcess = line.split(" ");
				Query.Type t;
				if(toProcess[0].equals("Uniform")){
					t = Query.Type.UNIFORM;
				}else if(toProcess[0].equals("A*")){
					t = Query.Type.A;
				}else{
					reader.close();
					throw new Exception(" The algorithm type must be defined as \" Uniform\" "
							+ "Or \" A*\"");
				}
				 
				Query q = new Query(t,Integer.parseInt(toProcess[1]),Integer.parseInt(toProcess[2]));
				result.add(q);
			}
			
		}catch(Exception e){
			reader.close();
			throw new IOException("Error reading queries: "+e.getMessage());
		}
		
		
		reader.close();
		return result;
	}
	
	public static void writeResult(List<List<Vertex>> results, String fileName) throws Exception{
		try{
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			for(int i =0;i<results.size();i++){
				String toprint ="";
				List<Vertex>r = results.get(i);
				
				for(Vertex v:r){
					toprint = toprint+v.toString()+"-";
				}
				toprint = toprint.substring(0, toprint.length()-1);
				writer.println(toprint);
			}
			writer.close();
		}catch(Exception e){
			throw new Exception(e);
		}
	}
	
	/**
	 * @require The parameter must be non empty line containing only a number
	 * 
	 * @ensure returns a positive number
	 *
	 * @param str
	 *            the line from the file containing the number of rows
	 * @return The number of rows in the matrix
	 * @throws Exception
	 *             if the session is not a strictly positive number
	 */
	private static int rowNumber(String str) throws Exception {
		try {
			int sessionNumber = Integer.parseInt(str.trim());
			if (sessionNumber > 0)
				return sessionNumber;
			else
				throw new Exception(
						"ERROR AT LINE 1: The row number must be a positive integer");
		} catch (Exception e1) {
			throw new Exception(
					"ERROR AT LINE 1: The first line must be a positive"
							+ " integer defining the number of Row");
		}

	}

	
}

