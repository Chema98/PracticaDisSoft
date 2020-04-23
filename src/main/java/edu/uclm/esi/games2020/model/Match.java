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
		return this.readyPlayers==game.requiredPlayers;
	}
}
