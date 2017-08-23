package model;

public class Query {
	String initID;
	String goalID;
	public Query( String initID, String goalID){
		this.initID = initID;
		this.goalID =goalID;
	}

	public String getInitID() {
		return initID;
	}

	public String getGoalID() {
		return goalID;
	}

	public void setInitID(String initID) {
		this.initID = initID;
	}

	public void setGoalID(String goalID) {
		this.goalID = goalID;
	}

	@Override
	public String toString(){
		return "Finding optimal path from: "+initID+" to: "+goalID;
	}
}
