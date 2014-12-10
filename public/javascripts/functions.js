$("#dynamicallyLoadableContent").on("click", "button.nextMove",function(){
	var figurex =$("input.InputFigurex").val();
	var figurey =9-$("input.InputFigurey").val();
	
	var destinationx =$("input.InputDestinationx").val();
	var destinationy =9-$("input.InputDestinationy").val();
	
	var input = "input/" + figurey + "%20" + figurex + "%20" + destinationy + "%20" + destinationx
	$("#dynamicallyLoadableContent").load(input + " #dynamicallyLoadableContent");
	
});

var figurex;
var figurey;
var destx;
var desty;

function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
	figurex=ev.target.dataset.x;
	figurey=ev.target.dataset.y;
	
	$("input.InputFigurex").val(figurex);
	$("input.InputFigurey").val(9-figurey);
}

function drop(ev) {
	ev.preventDefault();
	destx=ev.target.dataset.x;
	desty=ev.target.dataset.y;
	
	$("input.InputDestinationx").val(destx);
	$("input.InputDestinationy").val(9-desty);
	
//	alert(figurex + " " + figurey + " " + destx + " " + desty);
	
	var input = "input/" + figurey + "%20" + figurex + "%20" + desty + "%20" + destx
	$("#dynamicallyLoadableContent").load(input + " #dynamicallyLoadableContent");
}
