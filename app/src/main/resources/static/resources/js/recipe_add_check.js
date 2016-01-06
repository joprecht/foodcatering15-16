$(':input').keyup(function() {
    if($('#name').val() != "" && $('#description').val() != "" && $('#usedGroceries tr').length>0 && $('#quan').val() != "") {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});