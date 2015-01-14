var figurex;
var figurey;
var destx;
var desty;
var clickflag = 0;

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
	
	var input = "input/" + figurey + "%20" + figurex + "%20" + desty + "%20" + destx
	$("#dynamicallyLoadableContent").load(input + " #dynamicallyLoadableContent");
}

function cellclicked(ev){
    figurex=ev.target.dataset.x;
    figurey=ev.target.dataset.y;
    
    if(clickflag == 0){
        $("input.InputFigurex").val(figurex);
        $("input.InputFigurey").val(9-figurey);
        clickflag = 1;
    }else{
        $("input.InputDestinationx").val(destx);
        $("input.InputDestinationy").val(9-desty);

        var input = "input/" + figurey + "%20" + figurex + "%20" + desty + "%20" + destx
        $("#dynamicallyLoadableContent").load(input + " #dynamicallyLoadableContent");
        clickflag = 0;
    }

}
