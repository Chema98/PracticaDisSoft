package edu.uclm.esi.games2020.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.JSONObject;

public class DominoMatch extends Match {
	private int contadorpasar = 0;
	private boolean empate = false;
	private BarajaDomino fichas;
	private ArrayList<FichaDomino> mesa = new ArrayList<FichaDomino>();
	private String colocar;
	private FichaDomino nueva = new FichaDomino();
	private static String DELANTE = "delante";
	private static String DETRAS = "detras";

	public DominoMatch() {
		super();
		this.fichas = new BarajaDomino();
		this.fichas.remover();
	}
	
	@Override
	public void addPlayer(User user) {
		super.addPlayer(user);
		this.setState(user);
	}

	protected void setState(User user) {
		IState state = new DominoState();
		user.setState(state);
		state.setUser(user);

	}

	@Override
	protected JSONObject startData(User player) {
		/* Fichas del usuario */
		DominoState state = (DominoState) player.getState();
		for (int i = 0; i < 7; i++) {
			FichaDomino ficha = this.fichas.getFichaDomino();
			state.addFicha(ficha);
		}

		JSONObject jso = new JSONObject();
		jso.put("data", state.getJSONArrayFichas());
		return jso;
	}

	@Override
	protected void comprobarjugada(User jugadorQueHaMovido) { 
		if (this.ganador == null) {
			this.ganador = compruebaGanador(jugadorQueHaMovido);
		}
		if (contadorpasar == 2) {
			this.ganador = calcularGanador();
			if(this.ganador == null) {
				empate=true;
			}
		}
	}

	private User calcularGanador() {
		DominoState jugador1 = (DominoState) this.players.get(0).getState();
		DominoState jugador2 = (DominoState) this.players.get(1).getState();
		if(jugador1.calcularPuntos()> jugador2.calcularPuntos()) {
			return this.players.get(0);
		}else if(jugador1.calcularPuntos() < jugador2.calcularPuntos()) {
			return this.players.get(1);
		} else {
			return null;
		}
	}

	private User compruebaGanador(User jugadorQueHaMovido) {
		DominoState state = (DominoState) jugadorQueHaMovido.getState();
		List<FichaDomino> auxiliar = state.getFichas();
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
		String subtype = jsoMovimiento.getString("subtype");
		if (subtype.equals("ponerFicha")) {
			if (!comprobarmovimiento(jsoMovimiento)) {
				JSONObject jso = new JSONObject();
				jso.put("type", "Movimiento");
				jugadorQueHaMovido.send(jso);
				throw new Exception("Movimiento no permitido");
			}
			contadorpasar = 0;
		} else if (subtype.equals("robar")) {
			if (this.fichas.vacio()) {
				JSONObject jso = new JSONObject();
				jso.put("type", "Fichas");
				jugadorQueHaMovido.send(jso);
				throw new Exception("No quedan fichas");
			}
			FichaDomino robar = this.fichas.getFichaDomino();
			DominoState state = (DominoState) jugadorQueHaMovido.getState();
			state.addFicha(robar);
			JSONObject jso = new JSONObject();
			jso.put("type", "Ficha Robada");
			jso.put("ficha", robar.toJSON());
			jugadorQueHaMovido.send(jso);
		} else if (subtype.equals("pasar") && this.fichas.vacio()) {
				contadorpasar++;
		}
	}

	private boolean comprobarmovimiento(JSONObject jsoMovimiento) {
		boolean valida = false;
		FichaDomino auxiliar = new FichaDomino(jsoMovimiento.getInt("numero1"), jsoMovimiento.getInt("numero2"));
		if (this.mesa.isEmpty()) {
			valida = true;
			this.colocar = DELANTE;
			this.nueva = auxiliar;
		} else if (comprobarFicha(this.mesa, auxiliar)) {
			valida = true;
		}
		return valida;
	}

	private boolean comprobarFicha(ArrayList<FichaDomino> mesa, FichaDomino auxiliar) {
		boolean movimientovalido = false;
		FichaDomino primera = mesa.get(0);
		FichaDomino ultima = mesa.get(mesa.size() - 1);
		boolean primeraficha = comprobarprimeraficha(auxiliar, primera);
		boolean ultimaficha = comprobarultimaficha(auxiliar, ultima);
		if (primeraficha || ultimaficha) {
			movimientovalido = true;
		}

		return movimientovalido;
	}

	private boolean comprobarultimaficha(FichaDomino auxiliar, FichaDomino ultima) {
		boolean valida = false;
		if (auxiliar.getNumero1() == ultima.getNumero2()) {
			this.colocar = DETRAS;
			this.nueva = auxiliar;
			valida = true;
		} else if (auxiliar.getNumero2() == ultima.getNumero2()) {
			FichaDomino poner = new FichaDomino(auxiliar.getNumero2(), auxiliar.getNumero1());
			this.colocar = DETRAS;
			this.nueva = poner;
			valida = true;
		}
		return valida;
	}

	private boolean comprobarprimeraficha(FichaDomino auxiliar, FichaDomino primera) {
		boolean valida = false;
		if (auxiliar.getNumero2() == primera.getNumero1()) {
			this.colocar = DELANTE;
			this.nueva = auxiliar;
			valida = true;
		} else if (auxiliar.getNumero1() == primera.getNumero1()) {
			FichaDomino poner = new FichaDomino(auxiliar.getNumero2(), auxiliar.getNumero1());
			this.colocar = DELANTE;
			this.nueva = poner;
			valida = true;
		}
		return valida;
	}

	@Override
	protected void actualizarTablero(JSONObject jsoMovimiento, User jugadorQueHaMovido) throws IOException {
		String subtype = jsoMovimiento.getString("subtype");
		if (subtype.equals("ponerFicha")) {
			int posicion = jsoMovimiento.getInt("posicion");
			DominoState state = (DominoState) jugadorQueHaMovido.getState();
			state.eliminarFicha(posicion);
			eliminarFichaJugador(posicion,jugadorQueHaMovido);
			if (colocar.equals(DELANTE)) {
				mesa.add(0, nueva);
			} else {
				mesa.add(nueva);
			}
			JSONObject jso = new JSONObject();
			jso.put("type", "actualizartablero");
			jso.put("posicion", this.colocar);
			jso.put("ficha", this.nueva.toJSON());
			for (User user : this.players)
				user.send(jso);
		}
		cambiarTurno(jugadorQueHaMovido);
	}

	private void eliminarFichaJugador(int posicion, User jugadorQueHaMovido) throws IOException {
		JSONObject jso = new JSONObject();
		jso.put("type", "Eliminar Ficha");
		jso.put("posicion", posicion);
		jugadorQueHaMovido.send(jso);	
	}

	private void cambiarTurno(User jugadorQueHaMovido) {
		if (jugadorQueHaMovido == this.players.get(0)) {
			this.jugadorConElTurno = this.players.get(1);
		} else {
			this.jugadorConElTurno = this.players.get(0);
		}
	}

	@Override
	protected void notificarAClientes(JSONObject jsoMovimiento) throws IOException {
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
