package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap;
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private List<Airport> migliore;
	private Double distanzaMigliore;

	public Model() {
		dao = new ExtFlightDelaysDAO();
	}

	public void creaGrafo(Double distanza) {
		idMap = new HashMap<>();
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		// popolare idMap
		dao.loadAllAirports(idMap);

		Graphs.addAllVertices(this.grafo, dao.getVertici(idMap, distanza));
	

		// archi : controllo ci siano vertici e controllo media
		for (Rotta r : dao.getArchi(idMap)) {
			if (this.grafo.containsVertex(r.getA1()) && this.grafo.containsVertex(r.getA2())) {
				if (!this.grafo.containsEdge(r.getA1(), r.getA2())) {
					if (r.getPeso() > distanza) {
						Graphs.addEdge(this.grafo, r.getA1(), r.getA2(), r.getPeso());
					}
				} else {
					Double somma = this.grafo.getEdgeWeight(this.grafo.getEdge(r.getA1(), r.getA2())) + r.getPeso();
					Double media = somma / 2.0;
					if (media > distanza) {
						this.grafo.setEdgeWeight(r.getA1(), r.getA2(), media);
					}
				}
			}
		}

	}

	public Integer nVertici() {
		return grafo.vertexSet().size();

	}

	public Integer nArchi() {
		return grafo.edgeSet().size();
	}

	public List<Airport> vertici(){
		List<Airport> vertici = new ArrayList<>(this.grafo.vertexSet());
		
		return vertici;
	}
	
	public List<Rotta> adiacenti(Airport a){
		
		List<Airport> adiacenti = Graphs.neighborListOf(this.grafo, a);
		
		List<Rotta> rotte = new ArrayList<>();
		
		for(Airport temp : adiacenti) {
			rotte.add(new Rotta(a, temp, this.grafo.getEdgeWeight(this.grafo.getEdge(a, temp))));
		}
		
		Collections.sort(rotte);
		
		return rotte;
	}
	
	/**RICORSIONE: 
	 * 
	 * Airport di partenza, metto in parziale
	 * distanzaPercorsa = 0
	 * listaMax
	 * 
	 * chiamo ricorsione(parziale, distanzaPercorsa, nodo, migliaMax)
	 * 
	 * vicini del nodo, per ogni vicino se non è inserito e la distanza fino ad allora+ il peso<= passata, aggiungo
	 * e aggiorno distanza
	 * 
	 * backtracking
	 * 
	 * termino: 
	 * size > max e la distanza <= passata
	 * aggiorno
	 * 
	 * @param miglia
	 * @param a
	 */
	
	public List<Airport> cerca(Integer miglia, Airport a) {
		migliore = new ArrayList<>();
		Double distanzaPercorsa = 0.0;
		
		
		List<Airport> parziale = new ArrayList<>();
		parziale.add(a);
		
		ricorsiva(parziale, distanzaPercorsa, miglia, a);
		return migliore;
	}
	
	private void ricorsiva(List<Airport> parziale, Double distanzaPercorsa, Integer migliaMax, Airport a) {

		if(parziale.size()>migliore.size() && distanzaPercorsa<=migliaMax) {
			migliore = new ArrayList<>(parziale);
			distanzaMigliore = distanzaPercorsa;
			//non metto return
		}
		
		List<Airport> vicini = Graphs.neighborListOf(this.grafo, a);
		for(Airport air : vicini) {
			Double peso = distanzaPercorsa + this.grafo.getEdgeWeight(this.grafo.getEdge(a, air));
			if((!parziale.contains(air)) && peso<=migliaMax) {
				parziale.add(air);
				ricorsiva(parziale, peso, migliaMax, air);
				parziale.remove(air);
				
			}
			
		}

	}
	
	public Double getDistanzaTot() {
		return this.distanzaMigliore;
	}
}

