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

var updateFunction = function(sourceID, targetID) {
  return function() {
    var source = document.getElementById(sourceID);
    var target = document.getElementById(targetID);
    target.value = source.value;
  }
}

var newSlider = function(name, className, id, min, max, step, start) {
  var div = document.createElement("div");
  var p = document.createElement("p");
  p.setAttribute("style", "width: 240px");
  var slider = document.createElement("input");
  var slider=document.createElement("input");
  slider.name=name;
  slider.className=className;
  slider.setAttribute("type", "range");
  slider.setAttribute("id", id);
  slider.setAttribute("min", min);
  slider.setAttribute("max", max);
  slider.setAttribute("value", start);
  slider.setAttribute("step", step);
  p.appendChild(slider);
  componentHandler.upgradeElement(slider);
  div.appendChild(p);
  return div;
}
var newValueField = function(start, id) {
  var div = document.createElement("div");
  var error = document.createElement("span");
  var span = document.createElement("span");
  div.className="mdl-textfield mdl-js-textfield mdl-textfield--floating-label";
  span.style.fontSize = "14px";
  error.className = "mdl-textfield__error";
  error.innerHTML = "Zahl eingeben";
  div.className = "mdl-textfield mdl-js-textfield";
  var field = document.createElement("input");
  field.className = "mdl-textfield__input";
  field.setAttribute("type", "text");
  field.setAttribute("id", id);
  field.setAttribute("value", start);
  field.setAttribute("pattern", "-?[0-9]*(\.[0-9]+)?")
  field.style.paddingTop = "0px"
  field.setAttribute("style","border-style:hidden");
  field.setAttribute("readonly","");
  div.appendChild(field);
  div.appendChild(span);
  div.appendChild(error);
  div.setAttribute("style", "margin-left: auto; margin-right: auto; display: block;width:65px;padding-top:0px;padding-bottom:5px;");
  componentHandler.upgradeElement(div);
  return div;
}

//var newDropDownMenu(

var counter=1;

$(function() {
    $("#allRecipes, #usedRecipes").dblclick( function(e){
      var item = e.target.closest("tr");
      if (e.currentTarget.id === 'allRecipes' && e.target.id == "add") {
		  e.target.id += counter;
		  counter++;
		  e.target.innerHTML="cancel";
		  e.target.style="color:rgba(0, 0, 0, 0.87)";
		  var newr=document.createElement("tr");
		  var newd1=document.createElement("td");
		  var newd2=document.createElement("td");
		  
		  //Create Slider for Multiplicity
		  var field= newValueField(document.getElementById("initial_mult").value,"mult"+counter);
		  var slider= newSlider("multiplier","mdl-slider mdl-js-slider","slider"+counter,"1","3","0.05",document.getElementById("initial_mult").value);
		  
		  field.oninput = updateFunction("mult"+counter, "slider"+counter);
		  slider.oninput = updateFunction("slider"+counter, "mult"+counter);
		  newd1.appendChild(field);
		  newd1.appendChild(slider);
		  
		  //Create Radio Toggle
		  var newdiv=document.createElement("div");
		  newdiv.align="left";
		  var newp1=document.createElement("p");
		  var newp2=document.createElement("p");
		  var newp3=document.createElement("p");
		  //Option 1: Normal
		  var newlabel1=document.createElement("label");
		  newlabel1.className="mdl-radio mdl-js-radio mdl-js-ripple-effect";
		  newlabel1.setAttribute("for","option-1");
		  var newinput1=document.createElement("input");
		  newinput1.name="type";
		  newinput1.className="mdl-radio__button";
		  newinput1.type="radio";
		  newinput1.id="option-1";
		  newinput1.setAttribute("value","");
		  newinput1.setAttribute("checked","");
		  var newspan1=document.createElement("span");
		  newspan1.className="mdl-radio__label"
		  newspan1.innerHTML="<i class='material-icons'>local_dining</i> <span class='mdl-radio__label'>Normal</span>";
		  newlabel1.appendChild(newinput1);
		  newlabel1.appendChild(newspan1);
		  newp1.appendChild(newlabel1);
		  //Option 2: Diet
		  var newlabel2=document.createElement("label");
		  newlabel2.className="mdl-radio mdl-js-radio mdl-js-ripple-effect";
		  newlabel2.setAttribute("for","option-2");
		  var newinput2=document.createElement("input");
		  newinput2.name="type";
		  newinput2.className="mdl-radio__button";
		  newinput2.type="radio";
		  newinput2.id="option-2";
		  newinput2.setAttribute("value","");
		  var newspan2=document.createElement("span");
		  newspan2.className="mdl-radio__label"
		  newspan2.innerHTML="<i class='material-icons'>spa</i> <span class='mdl-radio__label'>Diet</span>";
		  newlabel2.appendChild(newinput2);
		  newlabel2.appendChild(newspan2);
		  newp2.appendChild(newlabel2);
		  //Option 3: Special
		  var newlabel3=document.createElement("label");
		  newlabel3.className="mdl-radio mdl-js-radio mdl-js-ripple-effect";
		  newlabel3.setAttribute("for","option-3");
		  var newinput3=document.createElement("input");
		  newinput3.name="type";
		  newinput3.className="mdl-radio__button";
		  newinput3.type="radio";
		  newinput3.id="option-3";
		  newinput3.setAttribute("value","");
		  var newspan3=document.createElement("span");
		  newspan3.className="mdl-radio__label"
		  newspan3.innerHTML="<i class='material-icons'>star_rate</i> <span class='mdl-radio__label'>Special</span>";
		  newlabel3.appendChild(newinput3);
		  newlabel3.appendChild(newspan3);
		  newp3.appendChild(newlabel3);
		  //Bundle 3 Options in one div
		  newdiv.appendChild(newp1);
		  newdiv.appendChild(newp2);
		  newdiv.appendChild(newp3);
		  
		  newd2.appendChild(newdiv);
		  
		  componentHandler.upgradeElement(newdiv);
		  newr.innerHTML=item.outerHTML;
		  newr.appendChild(newd1);
		  newr.appendChild(newd2);
          document.getElementById("usedRecipes").appendChild(newr);
		  e.target.innerHTML="check_circle";
		  e.target.style="color:#8bc34a";
      }
	  
	  
	  if (e.target.closest("tbody").id === 'usedRecipes' && e.target.tagName=="I")  {
		var resetid=e.target.id;
        $(item).fadeOut('fast');
		document.getElementById(resetid).innerHTML="add_circle";
		document.getElementById(resetid).style="color:rgba(0, 0, 0, 0.87)";
		document.getElementById(resetid).id = "add";
      if($('#usedRecipes tr').length=0) {
       		$('#submit').attr('disabled', true);
    	}
      }
	  if($('#usedRecipes tr').length>0) {
       	$('#submit').removeAttr('disabled');
      }
    });
  });