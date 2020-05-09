package edu.uclm.esi.games2020.model;

import java.io.IOException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Entity
public class User {
	@Id
	private String userName;
	private String email;
	private String pwd;
	
	@Transient
	private WebSocketSession session;
	@Transient
	private IState state;
	@Transient
	private HttpSession httpSession;
	
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

	public JSONObject toJSON() {
		return new JSONObject().put("userName", this.userName);
	}

	public void setSession(WebSocketSession session) {
		this.session = session;
	}

	public void send(JSONObject json) throws IOException {
		this.session.sendMessage(new TextMessage(json.toString()));
	}

	public IState getState() {
		return this.state;
	}

	public WebSocketSession getSession() {
        return this.session;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}
}
