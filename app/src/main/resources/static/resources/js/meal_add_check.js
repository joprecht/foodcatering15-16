$(':input').keyup(function() {
    if($('#name').val() != "" && $('#price').val() != "") {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});