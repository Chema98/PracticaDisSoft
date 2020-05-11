package edu.uclm.esi.games2020.ws;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

import edu.uclm.esi.games2020.model.Manager;
import edu.uclm.esi.games2020.model.Match;
import edu.uclm.esi.games2020.model.User;

@ServerEndpoint(value="/juegos", configurator=HttpSessionConfigurator.class)
public class WSServer {
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		System.out.println("Se ha conectado " + session.getId());
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		User user = (User) httpSession.getAttribute("user");
		user.setSession(session);
	}
	
	@OnMessage
	public void recibir(Session session, String message) throws Exception {
		System.out.println("La sesión " + session.getId() + " dice " + message);
		JSONObject jso = new JSONObject(message);
		if (jso.getString("type").equals("ready")) {
			Manager.get().playerReady(jso.getString("idMatch"));
		}else if (jso.getString("type").equals("movimiento")) {
            Match match = Manager.get().findMatch(jso.getString("idMatch"));
            match.mover(jso, session);
        }
	}
}
