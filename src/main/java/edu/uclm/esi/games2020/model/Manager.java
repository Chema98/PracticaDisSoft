package edu.uclm.esi.games2020.model;

import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uclm.esi.games2020.dao.UserDAO;

public class Manager {
	private ConcurrentHashMap<String, User> connectedUsers;
	private ConcurrentHashMap<String, Game> games;
	private ConcurrentHashMap<String, Match> pendingMatches;
	private ConcurrentHashMap<String, Match> inPlayMatches;
	
	private Manager() {
		this.connectedUsers = new ConcurrentHashMap<>();
		this.games = new ConcurrentHashMap<>();
		this.pendingMatches = new ConcurrentHashMap<>();
		this.inPlayMatches = new ConcurrentHashMap<>();
		
		Game ajedrez = new Ajedrez();
		Game ter = new TresEnRaya();
		Game escoba = new Escoba();
		Game domino = new Domino();
		
		this.games.put(ajedrez.getName(), ajedrez);
		this.games.put(ter.getName(), ter);
		this.games.put(escoba.getName(), escoba);
		this.games.put(domino.getName(), domino);
	}
	
	public JSONObject joinToMatch(User user, String gameName) {
		Game game = this.games.get(gameName);
		Match match = game.joinToMatch(user);
		if (!pendingMatches.containsKey(match.getId()))
			pendingMatches.put(match.getId(), match);
		return match.toJSON();
	}
	
	private static class ManagerHolder {
		static Manager singleton=new Manager();
	}
	
	public static Manager get() {
		return ManagerHolder.singleton;
	}

	public User login(String userName, String pwd) throws Exception {
		try {
			User user = UserDAO.identify(userName, pwd);
			this.connectedUsers.put(userName, user);
			return user;
		}
		catch(SQLException e) {
			throw new Exception("Credenciales inválidas");
		}
	}
	
	public void register(String email, String userName, String pwd) throws Exception {
		UserDAO.insert(email, userName, pwd);
	}
	
	public void logout(User user) {
		this.connectedUsers.remove(user.getUserName());
	}
	
	public JSONArray getGames() {
		Collection<Game> gamesList = this.games.values();
		JSONArray result = new JSONArray();
		for (Game game : gamesList)
			result.put(game.getName());
		return result;
	}

	
	public void playerReady(String idMatch) {
		Match match = this.pendingMatches.get(idMatch);
		match.playerReady();
		if (match.ready()) {
			this.pendingMatches.remove(idMatch);
			this.inPlayMatches.put(idMatch, match);
			match.notifyStart();
		}
	}

	public Match findMatch(String string) {
		return this.inPlayMatches.get(string);
	}
}