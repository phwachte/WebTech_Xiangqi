var ObserverSocket;

$(function(){
	$('#isHidden').hide();
	$('#rules-text').hide();

	ObserverSocket = getObserverSocket();
    setInterval(function(){ObserverSocket.send("ping");}, 40000);
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

function getObserverSocket(){
	// get websocket class, firefox has a different way to get it
	var WS = window['MozWebSocket'] ? window['MozWebSocket'] : WebSocket;
	// for secure socket
	var socket = new WS('@routes.Application.getNewObserverSocket().webSocketURL(request, true)');
	return socket;
}

function ruleTextFor(piece){
	$("#modal-body-id").load("/WUIrule/"+piece);
	$('#ruleModal').modal('toggle');
}
