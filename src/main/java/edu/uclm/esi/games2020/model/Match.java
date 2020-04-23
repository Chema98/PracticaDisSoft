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

	public Match() {
		this.id = UUID.randomUUID().toString();
		this.players = new ArrayList<>();
	}

	public void addPlayer(User user) {
		this.players.add(user);
		setState(user);
	}

	protected abstract void setState(User user);

	public List<User> getPlayers() {
		return players;
	}

	public String getId() {
		return id;
	}

	public abstract void start();

	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("idMatch", this.id);
		jso.put("started", this.started);
		JSONArray jsa = new JSONArray();
		for (User user : this.players)
			jsa.put(user.toJSON());
		jso.put("players", jsa);
		return jso;
	}

	public void notifyStart() {
		JSONObject jso = this.toJSON();
		jso.put("type", "matchStarted");
		jso.put("turno", turno(this.players).getUserName());
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
		return this.readyPlayers == game.requiredPlayers;
	}

	public void mover(JSONObject jsoMovimiento, Session session) throws Exception {
		User jugadorQueHaMovido = null;
		for (User user : this.players)
			if (user.getSession() == session) {
				jugadorQueHaMovido = user;
				break;
			}
		comprobarTurno(jugadorQueHaMovido);
		comprobarLegalidad(jsoMovimiento,jugadorQueHaMovido);
		actualizarTablero(jsoMovimiento, jugadorQueHaMovido);
		comprobarjugada(jsoMovimiento,jugadorQueHaMovido);
		notificarAClientes(jsoMovimiento);
	}
	
    protected abstract void comprobarjugada(JSONObject jsoMovimiento, User jugadorQueHaMovido);

	public abstract User turno(List<User> players);
	
	protected abstract void comprobarTurno(User jugadorQueHaMovido) throws Exception;

	protected abstract void comprobarLegalidad(JSONObject jsoMovimiento, User jugadorQueHaMovido) throws Exception;

	protected abstract void actualizarTablero(JSONObject jsoMovimiento, User jugadorQueHaMovido);

	protected abstract void notificarAClientes(JSONObject jsoMovimiento);
	

}