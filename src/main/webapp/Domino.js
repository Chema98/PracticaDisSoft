var self;
function ViewModel() {
    self = this;
    self.usuarios = ko.observableArray([]);
    self.mesa = ko.observableArray([]);
    self.fichasJugador = ko.observableArray([]);
    self.turno = ko.observable();
    
    var idMatch = sessionStorage.idMatch;
    var started = JSON.parse(sessionStorage.started);
    self.mensaje = ko.observable("");
    if (started) {
        self.mensaje("La partida " + idMatch + " ha comenzado");
    } else {
        self.mensaje("Esperando oponente para la partida " + idMatch);
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
            /*usuarios de la partida*/
            var players = data.players;
            for (var i=0; i<players.length; i++) {
                var player = players[i];
                self.usuarios.push(player.userName);
            }
            /*fichas del usuario*/
            var fichas = data.starData;
            for (var i=0; i< fichas.length; i++){
                self.fichasJugador.push(fichas[i]);
            }
            /*turno al empezar*/
            var turno = data.turno;
            self.turno(turno)
            console.log(data);
        } else if (data.type == "Turno"){ 
        	self.mensaje("No es tu turno");
        } else if (data.type == "Movimiento"){ 
        	self.mensaje("Movimiento no permitido, prueba otra vez.");
        } else if (data.type == "actualizartablero") {
        	/* actualizo la ficha indicada */
        	
        	
        	/* actualizo el turno */
        	var turno = data.turno;
            self.turno(turno);
        } else if (data.type=="ganador"){
        	var ganador = data.jugador;
        	self.mensaje("Ha ganado " + ganador + ",enhorabuena");
        }else if (data.type=="empate"){
        	var ganador = data.jugador;
        	self.mensaje("No ha habido ganador\n EMPATE");	
        }
    }
    
}

var vm = new ViewModel();
ko.applyBindings(vm);