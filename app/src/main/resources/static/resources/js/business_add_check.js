$(':input').keyup(function() {
    if($('#name').val() != "" && $('#firstname').val() != ""&& $('#lastname').val() != ""&& $('#streetname').val() != ""&& $('#streetnumber').val() != ""&& $('#city').val() != ""&& $('#zip').val() != ""&& $('#country').val() != ""&& $('#referalcode').val() != "") {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});