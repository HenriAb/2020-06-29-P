package it.polito.tdp.PremierLeague.model;

import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Match, DefaultWeightedEdge> grafo;
	private Map<Integer, Match> idMap;
	private Map<Adiacenza, Integer> connessioniMax;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
	}
	
	public void creaGrafo(Month mese, int min) {
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap = new HashMap<>();
		
		// aggiungo i vertici
		List<Match> vertici = new ArrayList<>(this.dao.getVertici(mese, idMap).values());
		Graphs.addAllVertices(this.grafo, vertici);
		
		// aggiungo gli archi
		for(Adiacenza a : this.dao.getArchi(mese, min, idMap)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getM1(), a.getM2(), a.getPeso());
		}
		
		this.connessioniMax = new HashMap<>();
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public String getConnMax(Month m, Integer min){
		
		List<Adiacenza> result = new ArrayList<>();
		Adiacenza best = null;
		Integer max = 0;//Integer.MIN_VALUE;
		
		Integer numGiocatori = 0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			numGiocatori = (int) this.grafo.getEdgeWeight(e);
			
			if(numGiocatori > max) {
				max = numGiocatori;
				Match m1 = this.grafo.getEdgeSource(e);
				Match m2 = this.grafo.getEdgeTarget(e);
				best = new Adiacenza(m1, m2, max);
			}	
		}
		
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			numGiocatori = (int) this.grafo.getEdgeWeight(e);
			
			if(this.grafo.getEdgeWeight(e) == max) {
				result.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), max));
				
			}	
		}
		
		String res = "";
		for(Adiacenza a : result) {
			res += a.toString() + "\n";
		}
		return res;
	} 
	
}
