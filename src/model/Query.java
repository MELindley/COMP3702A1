package model;

public class Query {
	Type type;
	int initID;
	int goalID;
	public Query(Type type, int initID, int goalID){
		this.type = type;
		this.initID = initID;
		this.goalID =goalID;
	}
	
	public Type getType() {
		return type;
	}

	public int getInitID() {
		return initID;
	}

	public int getGoalID() {
		return goalID;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setInitID(int initID) {
		this.initID = initID;
	}

	public void setGoalID(int goalID) {
		this.goalID = goalID;
	}

	public static enum Type{
		UNIFORM,A;
	}
	
	@Override
	public String toString(){
		return "Fing optimal path from: "+initID+" to: "+goalID+ " using: "+type;	}
}
