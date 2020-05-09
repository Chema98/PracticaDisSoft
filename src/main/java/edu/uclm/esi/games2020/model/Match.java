package edu.uclm.esi.games2020.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Match {
	protected List<User> players;
	protected String id;
	protected boolean started;
	private int readyPlayers;
	private Game game;
	protected User jugadorConElTurno, ganador;

	public Match() {
		this.id = UUID.randomUUID().toString();
		this.players = new ArrayList<>();
	}

	public void addPlayer(User user) {
		this.players.add(user);
		//setState(user);
	}

	//protected abstract void setState(User user);

	public List<User> getPlayers() {
		return players;
	}

	public String getId() {
		return id;
	}

	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("idMatch", this.id);
		jso.put("started", this.started);
		JSONArray jsa = new JSONArray();
		for (User user : this.players)
			jsa.put(user.toJSON());
		if (this.jugadorConElTurno != null) {
			jso.put("turno", this.jugadorConElTurno.getUserName());
		}
		jso.put("players", jsa);
		return jso;
	}

	public void notifyStart() {
		JSONObject jso = this.toJSON();
		jso.put("type", "matchStarted");
		for (User player : this.players) {
			jso.put("startData", startData(player));
			player.send(jso);
		}
	}

	protected abstract JSONObject startData(User player);

	public void playerReady(Session session) {
		++readyPlayers;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public boolean ready() {
		this.started = this.readyPlayers==game.requiredPlayers;
		if (this.started)
			this.jugadorConElTurno = turno();
		return this.started;
	}

	public void mover(JSONObject jsoMovimiento, Session session) throws Exception {
		User jugadorQueHaMovido = jugadorQueHaMovido(session);
		comprobarTurno(jugadorQueHaMovido);
		comprobarLegalidad(jsoMovimiento, jugadorQueHaMovido);
		actualizarTablero(jsoMovimiento, jugadorQueHaMovido);
		comprobarjugada(jugadorQueHaMovido);
		notificarAClientes(jsoMovimiento);
	}

	private User jugadorQueHaMovido(Session session) {
		for (User user : this.players)
			if (user.getSession() == session) {
				return user;
			}
		return null;
	}

	protected abstract void comprobarjugada(User jugadorQueHaMovido);

	public abstract User turno();

	protected abstract void comprobarTurno(User jugadorQueHaMovido) throws Exception;

	protected abstract void comprobarLegalidad(JSONObject jsoMovimiento, User jugadorQueHaMovido) throws Exception;

	protected abstract void actualizarTablero(JSONObject jsoMovimiento, User jugadorQueHaMovido);

	protected abstract void notificarAClientes(JSONObject jsoMovimiento);

}