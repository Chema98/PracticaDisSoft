package edu.uclm.esi.games2020.ws;

import java.util.List;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import edu.uclm.esi.games2020.model.Manager;
import edu.uclm.esi.games2020.model.Match;
import edu.uclm.esi.games2020.model.User;

@Component
public class SpringWebSocket extends TextWebSocketHandler {
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println("Se ha conectado " + session.getId());
		HttpHeaders headers = session.getHandshakeHeaders();
		List<String> cookies = headers.get("cookie");
		for (String cookie : cookies)
			if (cookie.startsWith("JSESSIONID=")) {
				String httpSessionId = cookie.substring("JSESSIONID=".length());
				User user = Manager.get().findUserByHttpSessionId(httpSessionId);
				user.setSession(session);
				Manager.get().addUserByWSSession(user, session);
				break;
			}
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		System.out.println("La sesión " + session.getId() + " dice " + message.getPayload());
		JSONObject jso = new JSONObject(message.getPayload().toString());
		try {
			if (jso.getString("type").equals("ready")) {
				Manager.get().playerReady(jso.getString("idMatch"));
			} else if (jso.getString("type").equals("movement")) {
				String idMatch = jso.getString("idMatch");
				Match match = Manager.get().findMatch(idMatch);
				match.mover(jso, session);
			}
		} catch (Exception e) {
			jso = new JSONObject();
			jso.put("type", "error").put("message", e.getMessage());
			if (e instanceof NullPointerException)
				jso.put("message", "NullPointerException en algún lado");
			session.sendMessage(new TextMessage(jso.toString()));
		}
	}
}
