package edu.uclm.esi.games2020.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class DominoState implements IState {
	private User user;
	private List<FichaDomino> fichas;
	
	@Override
	public void setUser(User user) {
		this.user = user;
		this.fichas = new ArrayList<>();
	}

	public void addFicha(FichaDomino ficha) {
		this.fichas.add(ficha);
	}

	public JSONArray getJSONArrayFichas() {
		JSONArray jsa = new JSONArray();
		for (FichaDomino ficha : this.fichas) 
			jsa.put(ficha.toJSON());
		return jsa;
	}

	public List<FichaDomino> getFichas() {
		return this.fichas;
	}

}
