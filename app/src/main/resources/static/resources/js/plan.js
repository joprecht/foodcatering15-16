function changeImg(name){
	setTimeout(hide(),500);
	setTimeout(newImage(name), 500);
	setTimeout(show(),500);
	}
function newImage(name){
	$('#img').attr('src','/resources/jpg/'+name+'.jpg');
	}
function show(){
	$('#img').attr('style','opacity: 1; transition-property: opacity; transition-duration: 0.5s; transition-delay: 10ms;');
	}
function hide(){
	$('#img').attr('style','opacity: 0; transition-property: opacity; transition-duration: 0.5s; transition-delay: 10ms;');
	}