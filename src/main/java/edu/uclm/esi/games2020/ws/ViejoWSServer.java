package edu.uclm.esi.games2020.ws;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

import edu.uclm.esi.games2020.model.User;

@ServerEndpoint(value="/wsServer", configurator=ViejoHttpSessionConfigurator.class)
public class ViejoWSServer {
	
	@OnOpen
	public void open(Session sesion, EndpointConfig config) {
		HttpSession httpSession=(HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		User user = (User) httpSession.getAttribute("user");
	}
	
	@OnMessage
	public void recibir(Session session, String msg) throws Exception {
		System.out.println(msg);
		JSONObject jso=new JSONObject(msg);
		String type = jso.getString("type");
		
	}

	private void sendError(Session session, String message) throws Exception {
		JSONObject jso = new JSONObject();
		jso.put("type", "error");
		jso.put("message", message);
		session.getBasicRemote().sendText(jso.toString());
	}
}
