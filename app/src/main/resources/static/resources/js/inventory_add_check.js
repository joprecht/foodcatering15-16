$(':input').keyup(function() {
    if($('#quantity').val() != ""  && $('#DD').val() != "" && $('#MM').val() != "" && $('#YYYY').val() != "") {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});