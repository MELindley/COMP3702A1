package model;

import java.util.ArrayList;
import java.util.HashSet;

/***
 * Model Class for the environment graph
 * A Graph contains all of the junctions (Vertices) and roads (Edges) described in the input. 
 * @author Marco Lindley
 *
 */
public class Graph {
	HashSet<Vertex>junctions;
	HashSet<Edge> roads;
	
	
	
	public Graph(){
		junctions = new HashSet<Vertex>();
		roads= new HashSet<Edge>();
	}
	
	public Graph(HashSet<Vertex> vertices,HashSet<Edge> edges) {
		this.junctions = vertices;
		this.roads = edges;
	}
	
	public void addJunction(Vertex loc){
		if(!junctions.contains(loc))
			junctions.add(loc);
	}
	
	public void removeJunction(Vertex loc){
		//remove the location from the graph
		junctions.remove(loc);
		//remove the edges connected to that location.
		for(Edge e: roads){
			if(e.contains(loc)){
				roads.remove(e);
			}
		}
	}
	
	public void addRoad(Edge e){
		if(!roads.contains(e)){
			this.roads.add(e);
		}
	}
	
	public HashSet<Vertex> getJunctions() {
		return junctions;
	}
	
	public HashSet<Edge> getRoads() {
		return roads;
	}

	public int getNumberOfJunctions() {
		return this.junctions.size();
	}

	public void setJunctions(HashSet<Vertex> junctions) {
		this.junctions = junctions;
	}

	public void setEdges(HashSet<Edge> edges) {
		this.roads = edges;
	}

	
	public Vertex getVertexById(int id){
		for(Vertex v: junctions){
			if(v.getId()==id){
				return v;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "Graph [Junctions=" + junctions + ", Roads=" + roads
				+ "]";
	}
	
}
