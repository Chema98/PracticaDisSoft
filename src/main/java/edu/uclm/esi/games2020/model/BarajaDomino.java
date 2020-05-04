package edu.uclm.esi.games2020.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class BarajaDomino {
	private List<FichaDomino> fichas;

	public BarajaDomino() {
		this.fichas = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			for (int j = i; j < 7; j++) {
				FichaDomino ficha = new FichaDomino(i, j);
				this.fichas.add(ficha);
			}
		}
	}

	public void remover() {
		SecureRandom dado = new SecureRandom();
		for (int i = 0; i < 200; i++) {
			int a = dado.nextInt(28);
			int b = dado.nextInt(28);
			FichaDomino auxiliar = this.fichas.get(a);
			this.fichas.set(a, this.fichas.get(b));
			this.fichas.set(b, auxiliar);
		}
	}
	
	public FichaDomino getFichaDomino() {
		return this.fichas.remove(0);
	}
	
	public boolean vacio() {
		return fichas.isEmpty();
	}
}
