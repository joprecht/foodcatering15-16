$(':input').keyup(function() {
    if($('#name').val() != "" && $('#description').val() != ""&& $('#quan').val() != "") {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});