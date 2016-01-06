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
    $("#allMealsRegular, #usedMealsRegular").dblclick( function(e){
      var item = e.target.closest("tr");
      if (e.currentTarget.id === 'allMealsRegular' && e.target.id == "add") {
		  e.target.id += counter;
		  counter++;
		  e.target.innerHTML="cancel";
		  e.target.style="color:rgba(0, 0, 0, 0.87)";
		  var newr=document.createElement("tr");
		  newr.innerHTML=item.outerHTML;
          document.getElementById("usedMealsRegular").appendChild(newr);
		  e.target.innerHTML="check_circle";
		  e.target.style="color:#8bc34a";
      }
	  
	  
	  if (e.target.closest("tbody").id === 'usedMealsRegular' && e.target.tagName=="I")  {
		var resetid=e.target.id;
        $(item).fadeOut('fast');
		document.getElementById(resetid).innerHTML="add_circle";
		document.getElementById(resetid).style="color:rgba(0, 0, 0, 0.87)";
		document.getElementById(resetid).id = "add";
      }
    });
    $("#allMealsDiet, #usedMealsDiet").dblclick( function(e){
      var item = e.target.closest("tr");
      if (e.currentTarget.id === 'allMealsDiet' && e.target.id == "add") {
		  e.target.id += counter;
		  counter++;
		  e.target.innerHTML="cancel";
		  e.target.style="color:rgba(0, 0, 0, 0.87)";
		  var newr=document.createElement("tr");
		  newr.innerHTML=item.outerHTML;
          document.getElementById("usedMealsDiet").appendChild(newr);
		  e.target.innerHTML="check_circle";
		  e.target.style="color:#8bc34a";
      }
	  
	  
	  if (e.target.closest("tbody").id === 'usedMealsDiet' && e.target.tagName=="I")  {
		var resetid=e.target.id;
        $(item).fadeOut('fast');
		document.getElementById(resetid).innerHTML="add_circle";
		document.getElementById(resetid).style="color:rgba(0, 0, 0, 0.87)";
		document.getElementById(resetid).id = "add";
      }
    });
    $("#allMealsSpecial, #usedMealsSpecial").dblclick( function(e){
      var item = e.target.closest("tr");
      if (e.currentTarget.id === 'allMealsSpecial' && e.target.id == "add") {
		  e.target.id += counter;
		  counter++;
		  e.target.innerHTML="cancel";
		  e.target.style="color:rgba(0, 0, 0, 0.87)";
		  var newr=document.createElement("tr");
		  newr.innerHTML=item.outerHTML;
          document.getElementById("usedMealsSpecial").appendChild(newr);
		  e.target.innerHTML="check_circle";
		  e.target.style="color:#8bc34a";
      }
	  
	  
	  if (e.target.closest("tbody").id === 'usedMealsSpecial' && e.target.tagName=="I")  {
		var resetid=e.target.id;
        $(item).fadeOut('fast');
		document.getElementById(resetid).innerHTML="add_circle";
		document.getElementById(resetid).style="color:rgba(0, 0, 0, 0.87)";
		document.getElementById(resetid).id = "add";
      }
    });
  });