$(':input').keyup(function() {
    if($('#usedMealsRegular tr').length==5 && $('#usedMealsDiet tr').length==5 && $('#usedMealsSpecial tr').length==5 && $('#week').val() != "") {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});