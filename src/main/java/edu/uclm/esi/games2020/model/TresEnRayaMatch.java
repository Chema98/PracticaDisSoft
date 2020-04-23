package edu.uclm.esi.games2020.model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class TresEnRayaMatch extends Match {
	private String[] fichas;
	private User jugadorConElTurno;
	private int contador = 0;
	private boolean ganador = false;

	public TresEnRayaMatch() {
		super();
		this.fichas = new String[9];
		for (int i = 0; i < 9; i++)
			this.fichas[i] = "";
	}

	@Override
	public void start() {
		this.started = true;
		super.notifyStart();
	}

	@Override
	protected JSONObject startData(User player) {
		JSONObject jso = new JSONObject();
		JSONArray squaremesa = new JSONArray();
		this.jugadorConElTurno = turno(players);
		for (int i = 0; i < 9; i++) {
			squaremesa.put(this.fichas[i]);
		}
		jso.put("fichas", squaremesa);
		return jso;
	}

	@Override
	protected void setState(User user) {
		IState state = new TresEnRayaState();
		user.setState(state);
		state.setUser(user);
	}

	@Override
	protected void actualizarTablero(JSONObject jsoMovimiento, User jugadorQueHaMovido) {
		int posicion = jsoMovimiento.getInt("posicion");
		if (jugadorQueHaMovido == this.players.get(0)) {
			this.fichas[posicion] = "X";
			this.jugadorConElTurno = this.players.get(1);
		} else {
			this.fichas[posicion] = "O";
			this.jugadorConElTurno = this.players.get(0);
		}
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
		int posicion = jsoMovimiento.getInt("posicion");
		if (this.fichas[posicion] != "") {
			JSONObject jso = new JSONObject();
			jso.put("type", "Movimiento");
			jugadorQueHaMovido.send(jso);
			throw new Exception("Movimiento no permitido");
		}

	}

	@Override
	protected void notificarAClientes(JSONObject jsoMovimiento) {
		int posicion = jsoMovimiento.getInt("posicion");
		JSONObject jso = new JSONObject();
		jso.put("type", "actualizartablero");
		jso.put("posicion", posicion);
		jso.put("estado", this.fichas[posicion]);
		jso.put("turno", this.jugadorConElTurno.getUserName());
		for (User user : this.players)
			user.send(jso);
	}

	protected void comprobarjugada(JSONObject jsoMovimiento, User jugadorQueHaMovido) {
		contador++;
		JSONObject jso = new JSONObject();
		String estado = null;
		if (jugadorQueHaMovido == this.players.get(0)) {
			estado = "X";
		} else {
			estado = "O";
		}
		if (this.contador > 4 && this.ganador == false) {
			this.ganador = compruebaGanador(jsoMovimiento, estado);
			if (this.ganador == true) {
				jso.put("type", "ganador");
				jso.put("jugador", jugadorQueHaMovido.getUserName());
			}
		} else if (this.contador == 9 && this.ganador == false) {
			jso.put("type", "empate");
		}
		for (User user : this.players)
			user.send(jso);
	}

	public boolean compruebaGanador(JSONObject jsoMovimiento, String estado) {

		// valida por fila
		for (int i = 0; i < this.fichas.length; i = i + 3) {
			if (this.fichas[i].equals(estado) && this.fichas[i + 1].equals(estado)
					&& this.fichas[i + 2].equals(estado)) {
				return ganador = true;
			}
		}

		// validacion por columna
		for (int i = 0; i < 3; i++) {
			if (this.fichas[i].equals(estado) && this.fichas[i + 3].equals(estado)
					&& this.fichas[i + 6].equals(estado)) {
				return ganador = true;
			}
		}

		// validación diagonal a derechas
		if (this.fichas[0].equals(estado) && this.fichas[4].equals(estado) && this.fichas[8].equals(estado)) {
			return ganador = true;
		}

		// validación diagonal a izquierdas
		if (this.fichas[2].equals(estado) && this.fichas[4].equals(estado) && this.fichas[5].equals(estado)) {
			return ganador = true;
		}
		return ganador;
	}
}