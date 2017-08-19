package model;
import model.Vertex;

/***
 * Model class for graph Edges 
 * An Edge describes a Road between two junction 
 * @author Marco Lindley
 *
 */
public class Edge {
	Vertex startOfRoad;
	Vertex endOfRoad;
	int weight;
	String name; 
	int numberOfPlots;
	
	
	public Edge() {
		startOfRoad = new Vertex();
		endOfRoad = new Vertex();
		weight = 0;
	}

	public Edge(Vertex v1, Vertex v2,String name, int roadLength, int numberOfPlots) {
		this.startOfRoad = v1;
		this.endOfRoad = v2;
		this.weight = roadLength;
		this.name = name;
		this.numberOfPlots = numberOfPlots;
	}
	
	public boolean contains(Vertex v){
		return (v==startOfRoad)||(v==endOfRoad);
	}
	
	public Vertex getOther(Vertex v){
		if(v == startOfRoad){
			return endOfRoad;
		}
		if(v == endOfRoad){
			return startOfRoad;
		}
		return new Vertex();
	}
	
	public Vertex getStartOfRoad() {
		return this.startOfRoad;
	}

	public Vertex getEndOfRoad() {
		return this.endOfRoad;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int weight){
		this.weight = weight;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfPlots() {
		return numberOfPlots;
	}

	public void setNumberOfPlots(int numberOfPlots) {
		this.numberOfPlots = numberOfPlots;
	}

	@Override
	public boolean equals(Object obj){
		boolean result = false;
		if(obj instanceof Edge){
			Edge e = (Edge) obj;
			result = (e.getStartOfRoad()==this.getStartOfRoad() && e.getEndOfRoad() == this.getEndOfRoad())
					||(e.getStartOfRoad()==this.getEndOfRoad() && e.getEndOfRoad() == this.getStartOfRoad());
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 7;
		int result = 1;
		result = prime * result + (startOfRoad.hashCode()+endOfRoad.hashCode());
		return result;
	}
	
	@Override
	public String toString(){
		return startOfRoad.getId()+"-"+endOfRoad.getId();
	}

	
}
