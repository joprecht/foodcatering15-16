$(':input').keyup(function() {
    if($('#name').val() != "" && $('#quantity').val() != "" && $('#price').val() != "" && $('#DD').val() != "" && $('#MM').val() != "" && $('#YYYY').val() != "") {
       $('#submit').removeAttr('disabled');
    } else {
       $('#submit').attr('disabled', true);   
    }
});