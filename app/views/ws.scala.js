 $(function(){
	var ObserverSocket = getObserverSocket();
	ObserverSocket.onmessage = function(event){
		if(event.data == "updateNow"){
			$("#dynamicallyLoadableContent").load("/WUIupdate" + " #dynamicallyLoadableContent");
		}else if(event.data == "youWon"){
			$("#dynamicallyLoadableContent").load("/WUIwon" + " #dynamicallyLoadableContent");
		}else if(event.data == "youLost"){
			$("#dynamicallyLoadableContent").load("/WUIlost" + " #dynamicallyLoadableContent");
		}else{
			 $('#socket-messages').prepend('<p>'+event.data+'</p>');
		}
		
	}
	 
	$('#socket-input').keyup(function(event){
        var charCode = (event.which) ? event.which : event.keyCode ;
       
        // if enter (charcode 13) is pushed, send message, then clear input field
        if(charCode === 13){
        	ObserverSocket.send($(this).val());
            $(this).val('');    
        }
    }); 
});

function getChatSocket(){
	// get websocket class, firefox has a different way to get it
	var WS = window['MozWebSocket'] ? window['MozWebSocket'] : WebSocket;
	// open pewpew with websocket
	var socket = new WS('@routes.Application.wsInterface().webSocketURL(request)');
	return socket;
}

function getObserverSocket(){
	// get websocket class, firefox has a different way to get it
	var WS = window['MozWebSocket'] ? window['MozWebSocket'] : WebSocket;
	// open pewpew with websocket
	var socket = new WS('@routes.Application.getNewObserverSocket().webSocketURL(request, true)');
//	var socket = new WS('@routes.Application.getNewObserverSocket().webSocketURL(request)');
	return socket;
}
