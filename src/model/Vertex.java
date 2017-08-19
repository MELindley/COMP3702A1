package model;

import java.util.ArrayList;
import java.util.List;
/***
 * Model Class for graph vertices
 * A vertex describes a Junction
 * @author Marco Lindley
 *
 */
public class Vertex {
	int id; /* The course name */
	List<Edge> roads;
	float pathCost = Integer.MAX_VALUE;//Max value by default
//	float f = Integer.MAX_VALUE;
//	float h = 0;

	/**
	 * Creates a new vertex
	 */
	public Vertex(){
		this.id =-1;
		this.roads = new ArrayList<Edge>();
	}
	
	public Vertex(int id) {
		this.id = id;
		this.roads = new ArrayList<Edge>();
	}

	public Vertex(int id, ArrayList<Edge>edges){
		this.id =id;
		this.roads = edges;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setRoads(ArrayList<Edge>edges){
		this.roads = edges;
	}
	
	public void addRoad(Edge road){
		this.roads.add(road);
	}
	
	public List<Edge> getRoads(){
		return this.roads;	
	}
	
	public void setPathCost(float cost){
		this.pathCost = cost;
	}
	
	public float getPathCost(){
		return this.pathCost;
	}
	
//	public float getF() {
//		return f;
//	}
//	
//	public void setF(float f) {
//		this.f = f;
//	}
//	
//	public float getH() {
//		return h;
//	}
//	
//	public void setH(float h) {
//		this.h = h;
//	}
	
	public boolean isConnectedTo(Vertex v1){
		for(Edge e: roads){
			if(e.contains(v1)){
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 3;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = true;
		if (obj instanceof Vertex){
			Vertex other = (Vertex) obj;
			if (id != other.getId()) {
				result = false;
			}
		}
		return result;
	}

	public String toString() {
		return ""+getId();
	}
}
