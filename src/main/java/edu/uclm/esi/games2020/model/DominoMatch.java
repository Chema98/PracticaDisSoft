package edu.uclm.esi.games2020.model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class DominoMatch extends Match {
	private User jugadorConElTurno;
	private boolean ganador = false;
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
	public void start() {
		this.started = true;
		super.notifyStart();

	}

	@Override
	protected JSONObject startData(User player) {
		/*Fichas del usuario*/
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
	protected void comprobarjugada(JSONObject jsoMovimiento, User jugadorQueHaMovido) {
		// TODO Auto-generated method stub

	}

	@Override
	public User turno(List<User> players) {
		User user;
		if (this.jugadorConElTurno == null) {
			user = this.players.get((int) Math.random());
		} else {
			user = this.jugadorConElTurno;
		}
		return user;
	}

	@Override
	protected void comprobarTurno(User jugadorQueHaMovido) throws Exception {
		if (this.jugadorConElTurno != jugadorQueHaMovido) {
			JSONObject jso = new JSONObject();
			jso.put("type", "Turno");
			jugadorQueHaMovido.send(jso);
			throw new Exception("No tienes el turno");
		}
	}

	@Override
	protected void comprobarLegalidad(JSONObject jsoMovimiento, User jugadorQueHaMovido) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void actualizarTablero(JSONObject jsoMovimiento, User jugadorQueHaMovido) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void notificarAClientes(JSONObject jsoMovimiento) {
		// TODO Auto-generated method stub

	}

}
