$("#usedMealsRegular, #allMealsRegular,usedMealsDiet,allMealsDiet,usedMealsSpecial,allMealsSpecial").dblclick( function(){
    if($('#usedMealsRegular tr').length>0 && $('#usedMealsDiet tr').length>0 && $('#usedMealsSpecial tr').length>0  &&  $('#usedMealsRegular tr').length<5 && $('#usedMealsDiet tr').length<5 && $('#usedMealsSpecial tr').length<5) {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});