$("#allRecipes, #usedRecipes").dblclick( function(){
    if($('#usedRecipes tr').length>0) {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});