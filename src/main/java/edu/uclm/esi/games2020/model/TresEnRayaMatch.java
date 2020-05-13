package edu.uclm.esi.games2020.model;

import java.io.IOException;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

public class TresEnRayaMatch extends Match {
	private String[] fichas;
	private int contador = 0;
	private boolean empate = false;
	private static final String POSICION = "posicion";

	public TresEnRayaMatch() {
		super();
		this.fichas = new String[9];
		for (int i = 0; i < 9; i++)
			this.fichas[i] = "";
	}

	@Override
	protected JSONObject startData(User player) {
		JSONObject jso = new JSONObject();
		JSONArray squaremesa = new JSONArray();
		for (int i = 0; i < 9; i++) {
			squaremesa.put(this.fichas[i]);
		}
		jso.put("fichas", squaremesa);
		return jso;
	}

	@Override
	protected void actualizarTablero(JSONObject jsoMovimiento, User jugadorQueHaMovido) throws IOException {
		int posicion = jsoMovimiento.getInt(POSICION);
		JSONObject jso = new JSONObject();
		if (jugadorQueHaMovido == this.players.get(0)) {
			this.fichas[posicion] = "X";
			this.jugadorConElTurno = this.players.get(1);
		} else {
			this.fichas[posicion] = "O";
			this.jugadorConElTurno = this.players.get(0);
		}
		jso.put("type", "actualizartablero");
		jso.put(POSICION, posicion);
		jso.put("estado", this.fichas[posicion]);
		for (User user : this.players)
			user.send(jso);
	}

	@Override
	public User turno() {
		return new Random().nextBoolean() ? this.players.get(0) : this.players.get(1);
	}

	@Override
	protected void comprobarTurno(User jugadorQueHaMovido) throws Exception {
		if (this.ganador != null || empate) {
			JSONObject jso = new JSONObject();
			jso.put("type", "fin");
			jugadorQueHaMovido.send(jso);
			throw new Exception("Partida Finalizada");
		}
		if (this.jugadorConElTurno != jugadorQueHaMovido) {
			JSONObject jso = new JSONObject();
			jso.put("type", "Turno");
			jugadorQueHaMovido.send(jso);
			throw new Exception("No tienes el turno");
		}
	}

	@Override
	protected void comprobarLegalidad(JSONObject jsoMovimiento, User jugadorQueHaMovido) throws Exception {
		int posicion = jsoMovimiento.getInt(POSICION);
		if (!this.fichas[posicion].equals("")) {
			JSONObject jso = new JSONObject();
			jso.put("type", "Movimiento");
			jugadorQueHaMovido.send(jso);
			throw new Exception("Movimiento no permitido");
		}

	}

	@Override
	protected void notificarAClientes(JSONObject jsoMovimiento) throws IOException {
		JSONObject jso = new JSONObject();
		if (ganador != null) {
			jso.put("type", "ganador");
			jso.put("ganador", this.ganador.getUserName());
		} else if (empate) {
			jso.put("type", "empate");
		} else {
			jso.put("type", "cambioturno");
			jso.put("turno", this.jugadorConElTurno.getUserName());
		}
		for (User user : this.players)
			user.send(jso);
	}

	protected void comprobarjugada(User jugadorQueHaMovido) {
		contador++;
		if (this.contador > 4 && this.ganador == null) {
			this.ganador = compruebaGanador(jugadorQueHaMovido);
		}
		if (this.contador == 9 && this.ganador == null)
			this.empate = true;

	}

	public User compruebaGanador(User jugadorQueHaMovido) {
		boolean fila = comprobarfila();
		boolean columna = comprobarcolumna();
		boolean diagonalderecha = comprobardiagonalderecha();
		boolean diagonalizquierda = comprobardiagonalizquierda();

		if (fila || columna || diagonalderecha || diagonalizquierda) {
			return jugadorQueHaMovido;
		}

		return null;
	}

	private boolean comprobardiagonalizquierda() {
		return (this.fichas[2].equals(this.fichas[4]) && this.fichas[2].equals(this.fichas[6]));
	}

	private boolean comprobardiagonalderecha() {
		return (this.fichas[0].equals(this.fichas[4]) && this.fichas[0].equals(this.fichas[8]));
	}

	private boolean comprobarcolumna() {
		for (int i = 0; i < 3; i++) {
			if (this.fichas[i].equals(this.fichas[i + 3]) && this.fichas[i].equals(this.fichas[i + 6])) {
				return true;
			}
		}
		return false;
	}

	private boolean comprobarfila() {
		for (int i = 0; i < this.fichas.length; i = i + 3) {
			if (this.fichas[i].equals(this.fichas[i + 1]) && this.fichas[i].equals(this.fichas[i + 2])) {
				return true;
			}
		}
		return false;
	}

}