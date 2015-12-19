$(':input').keyup(function() {
    if($('#username').val() != "" && $('#password').val() != ""&& $('#referalcode').val() != ""&& $('#email').val() != ""&& $('#firstname').val() != ""&& $('#lastname').val() != "") {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});