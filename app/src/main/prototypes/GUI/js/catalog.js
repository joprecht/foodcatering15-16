//Show only one Recipe from the Drop Down
//Use in catalog
function change_tbl(dhi) {
 if (dhi == '') {
     return;
 }
 $('#tbl_div > div').css('display', 'none');
 $('#' + dhi).css('display', 'block');
}

//Add nore Ingredients
//Used in addRecipe
var counter = 1;
var limit = 5;
function addInput(divName){
     if (counter == limit)  {
          alert("You have reached the limit of " + counter + " Ingredients");
     }
     else {
          var newdiv = document.createElement('div');
          newdiv.innerHTML = "<input type='text' name='Zutat"+ (counter+1) +"' placeholder='Zutat'> <input type='number' name='Anzahl Groß' placeholder='Anzahl Groß'> <input type='number' name='Anzahl Klein' placeholder='Anzahl Klein'>";
          document.getElementById(divName).appendChild(newdiv);
          counter++;
     }
}