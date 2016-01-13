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

function updateSelect(id, value){
	document.getElementById(id).setAttribute("value",value);
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
  field.setAttribute("style","border-style:hidden; text-align: center;");
  field.setAttribute("readonly","");
  div.appendChild(field);
  div.appendChild(span);
  div.appendChild(error);
  div.setAttribute("style", "margin-left: auto; margin-right: auto; display: block;width:65px;padding-top:0px;padding-bottom:5px;");
  componentHandler.upgradeElement(div);
  return div;
}
var newSelectField = function(start, name, id) {
  var div = document.createElement("div");
  var span = document.createElement("span");
  div.className="mdl-textfield mdl-js-textfield mdl-textfield--floating-label";
  span.style.fontSize = "14px";
  div.className = "mdl-textfield mdl-js-textfield";
  var field = document.createElement("input");
  field.name=name;
  field.className = "mdl-textfield__input";
  field.setAttribute("type", "text");
  field.setAttribute("id", id);
  field.setAttribute("value", start);
  field.setAttribute("pattern", "-?[0-9]*(\.[0-9]+)?")
  field.style.paddingTop = "0px"
  field.setAttribute("style","border-style:hidden; text-align: center;");
  field.setAttribute("readonly","");
  div.appendChild(field);
  div.appendChild(span);
  div.setAttribute("style", "margin-left: auto; margin-right: auto; display: block;width:65px;padding-top:0px;padding-bottom:5px;");
  componentHandler.upgradeElement(div);
  return div;
}

var newDropDownMenu=function(id){
  var div = document.createElement("div");
  var button = document.createElement("button");
  button.className="mdl-button mdl-js-button mdl-button--icon";
  button.setAttribute("id", id);
  button.setAttribute("type", "button");
  button.innerHTML="<i class='material-icons'>arrow_drop_down</i>";
  var ul = document.createElement("ul");
  ul.className="mdl-menu mdl-menu--bottom-right mdl-js-menu mdl-js-ripple-effect";
  ul.setAttribute("for",id);
  var li1 = document.createElement("li");
  li1.setAttribute("onclick", "updateSelect(this.parentNode.for;+'type','REGULAR')");
  li1.className="mdl-menu__item";
  li1.innerHTML="<i class='material-icons'>local_dining</i>";
  var li2 = document.createElement("li");
  li2.setAttribute("onclick", "updateSelect(this.parentNode.for;+'type','DIET')");
  li2.className="mdl-menu__item";
  li2.innerHTML="<i class='material-icons'>spa</i>";
  var li3 = document.createElement("li");
  li3.setAttribute("onclick", "updateSelect(this.parentNode.for;+'type','SPECIAL')");
  li3.className="mdl-menu__item";
  li3.innerHTML="<i class='material-icons'>star_rate</i>";
  ul.appendChild(li1);
  ul.appendChild(li2);
  ul.appendChild(li3);
  div.appendChild(button);
  div.appendChild(ul);
  componentHandler.upgradeElement(button);
  componentHandler.upgradeElement(ul);
  return div;
}

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
		  
		  var selectField= new newSelectField("REGULAR", "type", "drop"+counter+"type");
		  var dropDown= new newDropDownMenu("drop"+counter);
		  selectField.appendChild(dropDown);
		  newd2.appendChild(selectField);
		  
		  newr.innerHTML=item.outerHTML;
		  newr.appendChild(newd1);
		  newr.appendChild(newd2);
          document.getElementById("usedRecipes").appendChild(newr);
		  e.target.innerHTML="check_circle";
		  e.target.style="color:#8bc34a";
      }
	  
	  
	  if (e.target.closest("tbody").id === 'usedRecipes' && e.target.tagName=="I")  {
		var resetid=e.target.id;
		document.getElementById("usedRecipes").removeChild(item);
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