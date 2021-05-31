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
	
	public Map<Adiacenza, Integer> getConnMax(Month m, Integer min){
		
		Adiacenza best = null;
		Integer max = Integer.MIN_VALUE;
		

//		for(Adiacenza a1 : this.dao.getArchi(m, min, idMap)) {
//			for(Adiacenza a2 : this.dao.getArchi(m, min, idMap)) {
//				Integer numGiocatori = 0;
//				if(!a1.equals(a2)) {
//					numGiocatori += a1.getPeso() + a2.getPeso();
//				}
//			}
//		}
		Integer numGiocatori = 0;
		for(Adiacenza a : this.dao.getArchi(m, min, idMap)) {
			numGiocatori += a.getPeso();  
		}
		return null;
	} 
	
}
