		var counter = 1;
		var limit = 5;
		function addInput(divName){
		     if (counter == limit)  {
		          alert("You have reached the limit of " + counter + " Ingredients");
		     }
		     else {
		          var newdiv = document.createElement('div');
		          newdiv.innerHTML = "<input type='text' name='ing' placeholder='Zutat'/> <input type='text' pattern='(^\d+(\.)\d{2}$)' name='quan' placeholder='Anzahl' style='width:60px'/><select name='metric'><option value='KILOGRAM'>Kilo</option><option value='LITER'>Liter</option><option value='UNIT'>Stück</option></select>";
		          document.getElementById(divName).appendChild(newdiv);
		          counter++;
		          }
		}
		
		function change_tbl(dhi) {
		 if (dhi == '') {
		     return;
		 }
		 $('#tbl_div > div').css('display', 'none');
		 //$('#' + dhi).css('display', 'block');
		  $("[id='"+dhi+"']").css('display', 'block');
		}