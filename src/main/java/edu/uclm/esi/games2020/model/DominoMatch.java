package edu.uclm.esi.games2020.model;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class DominoMatch extends Match {
	private int contador = 0;
	private boolean empate = false;
	private BarajaDomino fichas;

	public DominoMatch() {
		super();
		this.fichas = new BarajaDomino();
		this.fichas.remover();
	}

	@Override
	protected void setState(User user) {
		IState state = new DominoState();
		user.setState(state);
		state.setUser(user);

	}

	@Override
	protected JSONObject startData(User player) {
		/* Fichas del usuario */
		FichaDomino ficha1 = this.fichas.getFichaDomino();
		FichaDomino ficha2 = this.fichas.getFichaDomino();
		FichaDomino ficha3 = this.fichas.getFichaDomino();
		FichaDomino ficha4 = this.fichas.getFichaDomino();
		FichaDomino ficha5 = this.fichas.getFichaDomino();
		FichaDomino ficha6 = this.fichas.getFichaDomino();
		FichaDomino ficha7 = this.fichas.getFichaDomino();

		JSONObject jso = new JSONObject();
		JSONArray jsaFichasDelJugador = new JSONArray();
		jsaFichasDelJugador.put(ficha1.toJSON());
		jsaFichasDelJugador.put(ficha2.toJSON());
		jsaFichasDelJugador.put(ficha3.toJSON());
		jsaFichasDelJugador.put(ficha4.toJSON());
		jsaFichasDelJugador.put(ficha5.toJSON());
		jsaFichasDelJugador.put(ficha6.toJSON());
		jsaFichasDelJugador.put(ficha7.toJSON());
		jso.put("data", jsaFichasDelJugador);
		return jso;
	}

	@Override
	protected void comprobarjugada(User jugadorQueHaMovido) {
		contador++;
		if (this.contador > 12 && this.ganador == null) {
			this.ganador = compruebaGanador(jugadorQueHaMovido);
		}
	}

	private User compruebaGanador(User jugadorQueHaMovido) {
		boolean ganador = false;
		return null;
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
		if (!comprobarjugada(jsoMovimiento)) {
			JSONObject jso = new JSONObject();
			jso.put("type", "Movimiento");
			jugadorQueHaMovido.send(jso);
			throw new Exception("Movimiento no permitido");
		}
	}

	private boolean comprobarjugada(JSONObject jsoMovimiento) {
		boolean valida=false;
		return valida;
	}

	@Override
	protected void actualizarTablero(JSONObject jsoMovimiento, User jugadorQueHaMovido) {

	}

	@Override
	protected void notificarAClientes(JSONObject jsoMovimiento) {
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
}
