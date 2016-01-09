function highlight(x){
	if (x.innerHTML=="add_circle"){
		x.style.cssText="cursor:pointer; color:grey";
	}
	if (x.innerHTML=="cancel"){
		x.style.cssText="cursor:pointer; color:red";
	}
}
function normal(x){
	if (x.innerHTML!="check_circle"){
		x.style.cssText="cursor:pointer; color:rgba(0, 0, 0, 0.87)";
	}
}

var counter=1;

$(function() {
    $("#allGroceries, #usedGroceries").dblclick( function(e){
      var item = e.target.closest("tr");
      if (e.currentTarget.id === 'allGroceries' && e.target.id == "add") {
		  e.target.id += counter;
		  counter++;
		  e.target.innerHTML="cancel";
		  e.target.style="color:rgba(0, 0, 0, 0.87)";
		  var newr=document.createElement("tr");
		  var newd=document.createElement("td");
		  //newr.innerHTML = item.outerHTML + '<td ><div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" style="width:50px" ><input name="quan" class="mdl-textfield__input" type="text" id="quan" pattern="-?[0-9]*(\.[0-9]+)?" min="0"/><label class="mdl-textfield__label" for="quan">Anzahl</label><span class="mdl-textfield__error">Zahl eingeben!</span></div></td>';
		  
		  var newdiv=document.createElement("div");
		  newdiv.className="mdl-textfield mdl-js-textfield mdl-textfield--floating-label";
		  newdiv.style="width:50px";
		  
		  var newinput=document.createElement("input");
		  newinput.name="quan";
		  newinput.className="mdl-textfield__input";
		  newinput.type="text";
		  newinput.id="quan";
		  newinput.pattern="-?[0-9]*(\.[0-9]+)?";
		  newinput.setAttribute("min","0");
		  var newlabel=document.createElement("label");
		  newlabel.className="mdl-textfield__label";
		  newlabel.setAttribute("for","quan");
		  newlabel.innerHTML="Anzahl";
		  var newspan=document.createElement("span");
		  newspan.className="mdl-textfield__error"
		  newspan.innerHTML="Zahl eingeben!";
		  
		  newdiv.appendChild(newinput);
		  newdiv.appendChild(newlabel);
		  newdiv.appendChild(newspan);
		  newd.appendChild(newdiv);
		  
		  newr.innerHTML=item.outerHTML;
		  newr.appendChild(newd);
		  componentHandler.upgradeElement(newdiv);
          document.getElementById("usedGroceries").appendChild(newr);
		  e.target.innerHTML="check_circle";
		  e.target.style="color:#8bc34a";
      }
	  
	  
	  if (e.target.closest("tbody").id === 'usedGroceries' && e.target.tagName=="I")  {
		var resetid=e.target.id;
		document.getElementById("usedGroceries").removeChild(item);
		document.getElementById(resetid).innerHTML="add_circle";
		document.getElementById(resetid).style="color:rgba(0, 0, 0, 0.87)";
		document.getElementById(resetid).id = "add";
      }
	  
	  if($('#name').val() != "" && $('#description').val() != "" && $('#usedGroceries tr').length>0 && $('#quan').val() != "") {
		$('#submit').removeAttr('disabled');
      }
	  if($('#name').val() == "" || $('#description').val() == "" || $('#usedGroceries tr').length==0 || $('#quan').val() == "") {
		$('#submit').attr('disabled', true);   
      }
    });
  });