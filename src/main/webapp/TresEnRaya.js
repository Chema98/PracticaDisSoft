var self;
function ViewModel() {
    self = this;
    self.usuarios = ko.observableArray([]);
    self.tablero = ko.observableArray([]);
    self.turno = ko.observable();/* para mostrar el turno cada vez */
    
    var idMatch = sessionStorage.idMatch;
    var started = JSON.parse(sessionStorage.started);
    self.mensaje = ko.observable("");
    if (started) {
        self.mensaje("La partida " + idMatch + " ha comenzado");
    } else {
        self.mensaje("Esperando oponente para la partida " + idMatch);
    }
    
    self.ponerEnMesa = function(ficha) {
        var msg = {
            type : "tablero",
            ficha : ficha
        };
        ws.send(JSON.stringify(msg));
    }
    
    var url = "ws://localhost:8600/juegos";
    self.ws = new WebSocket(url);

    self.ws.onopen = function(event) {
        var msg = {
            type : "ready",
            idMatch : sessionStorage.idMatch
        };
        self.ws.send(JSON.stringify(msg));
    }

 

    self.ws.onmessage = function(event) {
        var data = event.data;
        data = JSON.parse(data);
        if (data.type == "matchStarted") {
            self.mensaje("La partida ha empezado");
            var players = data.players;
            for (var i=0; i<players.length; i++) {
                var player = players[i];
                self.usuarios.push(player.userName);
            }
            for (var i=0; i<9; i++)
                self.tablero.push(new Ficha(i,""));
            var turno = data.turno;
            self.turno(turno)
            console.log(data);
        } else if (data.type == "Turno"){ 
        	self.mensaje("No es tu turno");
        } else if (data.type == "Movimiento"){ 
        	self.mensaje("Movimiento no permitido, prueba otra vez.");
        } else if (data.type == "actualizartablero") {
        	/* actualizo la ficha indicada */
        	var posicion = data.posicion;
        	var estado = data.estado;
        	var f = new Ficha(posicion,estado)
            self.tablero.splice(posicion,1,f);
        } else if (data.type =="cambioturno"){
        	var turno = data.turno;
            self.turno(turno);
        } else if (data.type=="ganador"){
        	var ganador = data.ganador;
        	self.mensaje("Ha ganado " + ganador + ",enhorabuena");
        }else if (data.type=="empate"){
        	self.mensaje("EMPATE");	
        }else if (data.type=="fin"){
        	self.mensaje("La partida ya ha finalizado.")
        }
    }
}

 

class Ficha {
    constructor(posicion,estado) {
        this.estado = estado;
        this.posicion = posicion;
    }
    
    ponerFicha() {
        var msg = {
            type : "movimiento",
            idMatch : sessionStorage.idMatch,
            posicion : this.posicion
        };
        self.ws.send(JSON.stringify(msg));
    }
    
}

var vm = new ViewModel();
ko.applyBindings(vm);