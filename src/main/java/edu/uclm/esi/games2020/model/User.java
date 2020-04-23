package edu.uclm.esi.games2020.model;

import javax.websocket.Session;

import org.json.JSONObject;

import edu.uclm.esi.games2020.dao.UserDAO;

public class User {
	private String userName;
	private String email;
	private Session session;
	private IState state;
	
	public void setState(IState state) {
		this.state = state;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public static User identify(String userName, String pwd) throws Exception {
		return UserDAO.identify(userName, pwd);
	}

	public JSONObject toJSON() {
		return new JSONObject().put("userName", this.userName);
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void send(JSONObject json) {
		this.session.getAsyncRemote().sendText(json.toString());		
	}

	public IState getState() {
		return this.state;
	}

	public Session getSession() {
        return this.session;
	}
}
