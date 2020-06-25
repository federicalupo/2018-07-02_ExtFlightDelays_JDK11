package it.polito.tdp.extflightdelays.model;

public class Rotta implements Comparable<Rotta>{

	private Airport a1;
	private Airport a2;
	private Double peso;
	public Rotta(Airport a1, Airport a2, Double peso) {
		super();
		this.a1 = a1;
		this.a2 = a2;
		this.peso = peso;
	}
	public Airport getA1() {
		return a1;
	}
	public Airport getA2() {
		return a2;
	}
	public Double getPeso() {
		return peso;
	}
	
	
	@Override
	public String toString() {
		return   a2 + " " + peso ;
	}
	@Override
	public int compareTo(Rotta o) {
		
		return -this.peso.compareTo(o.getPeso());
	}
	
	
}
