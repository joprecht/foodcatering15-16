$(':input').keyup(function() {
    if($('#username').val() != "" && $('#password').val() != "") {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});