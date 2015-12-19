$(function() {
    $("#allGroceries, #usedGroceries").dblclick( function(e){
      var item = e.target.closest("tr");
      if (e.currentTarget.id === 'allGroceries') {
		  var newr=document.createElement("tr");
		  newr.innerHTML = item.outerHTML + '<td ><div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" style="width:50px" ><input name="quan" class="mdl-textfield__input" type="text" id="quan" pattern="-?[0-9]*(\.[0-9]+)?" min="0"/><label class="mdl-textfield__label" for="quan">Anzahl</label><span class="mdl-textfield__error">Zahl eingeben!</span></div></td>';
        //$(item).fadeOut('fast', function() {
          document.getElementById("usedGroceries").appendChild(newr);
        //});
      }
	  
	  
	  else {
        $(item).fadeOut('fast'//, function() {
          //var newr = document.createElement("tr");
          //newr.innerHTML=item.childNodes[-1].outerHTML+item.childNodes[3].outerHTML;
          //document.getElementById("allGroceries").appendChild(newr);
        //}
		);
      }
    });
  });