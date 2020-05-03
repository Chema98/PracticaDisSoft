package edu.uclm.esi.games2020.model;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONObject;

public class DominoMatch extends Match {
	private int contador = 0;
	private boolean empate = false;
	private BarajaDomino fichas;
	private ArrayList <FichaDomino> mesa = new ArrayList<FichaDomino>();
	private ConcurrentHashMap<Session,ArrayList<FichaDomino>> fichasjugador = new ConcurrentHashMap<Session,ArrayList<FichaDomino>>();

	public DominoMatch() {
		super();
		this.fichas = new BarajaDomino();
		this.fichas.remover();
	}

	@Override
	protected void setState(User user) {
		IState state = new DominoState();
		user.setState(state);
		state.setUser(user);

	}

	@Override
	protected JSONObject startData(User player) {
		/* Fichas del usuario */
		ArrayList<FichaDomino> fj = new ArrayList<FichaDomino>();
		for (int i=0; i<7;i++) {
			FichaDomino ficha = this.fichas.getFichaDomino();
			fj.add(ficha);
		}
		
		this.fichasjugador.put(player.getSession(),fj);

		JSONObject jso = new JSONObject();
		JSONArray jsaFichasDelJugador = new JSONArray();
		for (FichaDomino ficha : fj) {
			jsaFichasDelJugador.put(ficha.toJSON());
		}
		jso.put("data", jsaFichasDelJugador);
		return jso;
	}

	@Override
	protected void comprobarjugada(User jugadorQueHaMovido) {
		contador++;
		if (this.contador > 12 && this.ganador == null) {
			this.ganador = compruebaGanador(jugadorQueHaMovido);
		}
		/* preguntar a macario sobre el empate*/
	}

	private User compruebaGanador(User jugadorQueHaMovido) {
		ArrayList<FichaDomino> auxiliar = new ArrayList<FichaDomino>();
		auxiliar = this.fichasjugador.get(jugadorQueHaMovido.getSession());
		if (auxiliar.isEmpty()) {
			return jugadorQueHaMovido;
		}
		return null;
	}
	

	@Override
	public User turno() {
		return new Random().nextBoolean() ? this.players.get(0) : this.players.get(1);
	}

	@Override
	protected void comprobarTurno(User jugadorQueHaMovido) throws Exception {
		if (this.ganador != null || empate) {
			JSONObject jso = new JSONObject();
			jso.put("type", "fin");
			jugadorQueHaMovido.send(jso);
			throw new Exception("Partida Finalizada");
		}
		if (this.jugadorConElTurno != jugadorQueHaMovido) {
			JSONObject jso = new JSONObject();
			jso.put("type", "Turno");
			jugadorQueHaMovido.send(jso);
			throw new Exception("No tienes el turno");
		}
	}

	@Override
	protected void comprobarLegalidad(JSONObject jsoMovimiento, User jugadorQueHaMovido) throws Exception {
		if (!comprobarjugada(jsoMovimiento)) {
			JSONObject jso = new JSONObject();
			jso.put("type", "Movimiento");
			jugadorQueHaMovido.send(jso);
			throw new Exception("Movimiento no permitido");
		}
	}

	private boolean comprobarjugada(JSONObject jsoMovimiento) {
		boolean valida=false;
		FichaDomino auxiliar = new FichaDomino(jsoMovimiento.getInt("numero1"),jsoMovimiento.getInt("numero2"));
		if (this.mesa.isEmpty()) {
			valida=true;
		}else if (comprobarFicha(this.mesa,auxiliar)) {
			valida=true;
		}
		return valida;
	}

	private boolean comprobarFicha(ArrayList<FichaDomino> mesa, FichaDomino auxiliar) {
		boolean movimientovalido =false;
		FichaDomino primera = mesa.get(0);
		FichaDomino ultima = mesa.get(mesa.size());
		/*preguntar a macario*/
		if(auxiliar.getNumero2() == primera.getNumero1() || auxiliar.getNumero1() == ultima.getNumero2()) {
			movimientovalido = true;
		}
		return movimientovalido;
	}

	@Override
	protected void actualizarTablero(JSONObject jsoMovimiento, User jugadorQueHaMovido) {

	}

	@Override
	protected void notificarAClientes(JSONObject jsoMovimiento) {
		JSONObject jso = new JSONObject();
		if (ganador != null) {
			jso.put("type", "ganador");
			jso.put("ganador", this.ganador.getUserName());
		} else if (empate) {
			jso.put("type", "empate");
		} else {
			jso.put("type", "cambioturno");
			jso.put("turno", this.jugadorConElTurno.getUserName());
		}
		for (User user : this.players)
			user.send(jso);
	}
}
