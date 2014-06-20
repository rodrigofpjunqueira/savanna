package br.com.rodrigojunqueira.savanna.TravellingSalesman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import br.com.rodrigojunqueira.savanna.core.Dna;

public class TravellingSalesman implements Dna {

	private String[] route;
	private int totalDistance;
	private static int goal;
	private TravellingSalesmanMap map;
	private HashMap<String, Integer> moves;
	
	public TravellingSalesman(TravellingSalesmanMap newMap) {
		this.map = newMap;
	}
	
	public void mutate() {
		if (Math.random() > 0.5) {
			// (3) Needs refactoring. It needs to contemplate any number of cities
			this.setRoute(new String[]{
					this.route[0], 
					this.route[4], 
					this.route[1], 
					this.route[3], 
					this.route[6],
					this.route[5],											
					this.route[2],
					this.route[7]
			});
		} else {
			this.setRoute(new String[]{
					this.route[0], 
					this.route[6], 
					this.route[2], 
					this.route[3], 
					this.route[4],
					this.route[5],											
					this.route[1],
					this.route[7]
			});			
		}
	}
	
	public Dna crossover(Dna dna) {
		TravellingSalesman travelToCrossover = (TravellingSalesman) dna;
		HashMap<String, Integer> movesOnThisTravel = this.getMoves();
		boolean moveFound = false;
		String shortestMove = new String();
		while (!moveFound) {	
			int shortestMoveCost = Collections.max(movesOnThisTravel.values());
			for (Entry<String, Integer> move : movesOnThisTravel.entrySet()) {
				if (move.getValue() <= shortestMoveCost) {
					shortestMove = move.getKey();
					shortestMoveCost = move.getValue();	
				}
			}
			
			if (travelToCrossover.getMoves().containsKey(shortestMove)) {
				movesOnThisTravel.remove(shortestMove);
			} else {
				moveFound = true;
			}
			if (movesOnThisTravel.isEmpty()) {
				moveFound = true; // they are the same
			}
		}

		String newMoveStart = Character.toString(shortestMove.charAt(0));
		String newMoveEnd = Character.toString(shortestMove.charAt(1));

		String[] newRoute = travelToCrossover.getRoute();	
		
		int indexCitySwapA = 0;
		int indexCitySwapB = 0;
		for (int i = 0; i < newRoute.length; i++) {
			if (newRoute[i].equals(newMoveStart) && i < newRoute.length - 1) indexCitySwapA = i + 1;
			if (newRoute[i].equals(newMoveEnd)) indexCitySwapB = i;			
		}
		newRoute[indexCitySwapB] = newRoute[indexCitySwapA];
		newRoute[indexCitySwapA] = newMoveEnd;
		
		TravellingSalesman newTravel = new TravellingSalesman(this.map);
		newTravel.setRoute(newRoute);
		return newTravel;
	}	

	private String[] getRoute() {
		return this.route;
	}
	
	private HashMap<String, Integer> getMoves() {
		return this.moves;
	}
	
	public boolean moreFitThan(Dna defiant) {
		TravellingSalesman d = (TravellingSalesman) defiant;
		if (this.getTotalDistance() < d.getTotalDistance()) {
			return true;
		} else return false;
	}

	public boolean lessFitThan(Dna defiant) {
		TravellingSalesman d = (TravellingSalesman) defiant;
		if (this.getTotalDistance() > d.getTotalDistance()) {
			return true;
		} else return false;
	}

	public boolean asFitAs(Dna defiant) {
		TravellingSalesman d = (TravellingSalesman) defiant;
		if (this.getTotalDistance() == d.getTotalDistance()) {
			return true;
		} else return false;
	}	

	public void evaluate() {
		int totalDistance = 0;
		for (int i = 0; i < (this.route.length - 1); i++) {
			totalDistance += this.map.getDistance(this.route[i], this.route[i+1]);
		}
		this.totalDistance = totalDistance;		
	}

	public boolean isGoodEnough() {
		if (this.getTotalDistance() <= TravellingSalesman.goal) {
			return true;
		} else return false;
	}

	public void setRoute(String[] newRoute) {
		this.route = newRoute;
		this.moves = new HashMap<String, Integer>();
		for (int i = 0; i < this.route.length - 1; i++) {
			this.moves.put(this.route[i].concat(this.route[i+1]), this.map.getDistance(this.route[i], this.route[i+1]));
		}
	}

	public int getTotalDistance() {
		return this.totalDistance;
	}

	public static void setGoal(int newGoal) {
		TravellingSalesman.goal = newGoal;
	}

	public boolean checkRoute() {
		if (!this.routeStartsAndEndsAtCityA()) return false;
		if (!this.routeHasCityAOnlyOnTheEdges()) return false;
		if (!this.routeHasNoRepeatedCities()) return false;
		if (!this.routeVisitedAllCities()) return false;
		return true;
	}
	
	private boolean routeStartsAndEndsAtCityA() {
		if (this.route[0] != "A" || this.route[this.route.length-1] != "A") {
			return false;
		} else return true;
	}
	
	private boolean routeHasCityAOnlyOnTheEdges() {
		for (int i = 1; i < this.route.length - 1; i++) {
			if (this.route[i] == "A") {
				return false;
			}
		}
		return true;
	}
	
	private boolean routeHasNoRepeatedCities() {
		for (int i = 1; i < this.route.length - 1; i++) {
			for (int j = 1; j < this.route.length - 1; j++) {
				if (i != j) {
					if (this.route[i] == this.route[j]) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean routeVisitedAllCities() {
		ArrayList<String> cities = this.map.getCities();
		Boolean visited;
		for (String cityToVisit : cities) {
			visited = false;
			for (int i = 0; i < this.route.length; i++) {
				visited = visited || (this.route[i] == cityToVisit);
			}
			if (!visited) {
				return false;
			}
		}
		return true;
	}
	
	public void setMap(TravellingSalesmanMap newMap) {
		this.map = newMap;		
	}

	public boolean hasMove(String cityA, String cityB) {
		if (this.moves.containsKey(cityA.concat(cityB))) return true;
		else return false;
	}
	
	public void show() {
		System.out.println(this.getTotalDistance() + " - " + this.route[0] + ", " + this.route[1] + ", " + this.route[2] + ", " + this.route[3] + ", " + this.route[4] + ", " + this.route[5] + ", " + this.route[6] + ", " + this.route[6]);
	}

	public String shortestMove() {
		String shortestMove = new String();
		int shortestMoveCost = Collections.max(this.moves.values());
		for (Entry<String, Integer> move : this.moves.entrySet()) {
			if (move.getValue() <= shortestMoveCost) {
				shortestMove = move.getKey();
				shortestMoveCost = move.getValue();
			}
		}
		return shortestMove;
	}
	

}
