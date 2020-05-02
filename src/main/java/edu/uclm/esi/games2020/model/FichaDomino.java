package edu.uclm.esi.games2020.model;

import org.json.JSONObject;

public class FichaDomino {
	private int numero1;
	private int numero2;
	
	public FichaDomino(int numero1, int numero2) {
		this.numero1 = numero1;
		this.numero2 = numero2;
	}
	
	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("numero1", this.numero1);
		jso.put("numero2", this.numero2);
		return jso;
	}

	public int getNumero1() {
		return numero1;
	}

	public int getNumero2() {
		return numero2;
	}
	
	
}
