// Wait for the DOM to be ready
$(function() {


    var maxLength = 1200;
    $('textarea').keyup(function() {
    var length = $(this).val().length;
    length = maxLength-length;
    $('#chars').text(length);
    });

    var form = document.getElementsByTagName('form')[0];
    var err = document.getElementById('error');
    var improvementSuggestion = document.getElementById('more-details');

    // options overall satisfaction rate
    var opt1 = document.getElementById('opt1');
    var opt2 = document.getElementById('opt2');
    var opt3 = document.getElementById('opt3');
    var opt4 = document.getElementById('opt4');
    var opt5 = document.getElementById('opt5');

    // options user registration rate
    var opt11 = document.getElementById('opt11');
    var opt12 = document.getElementById('opt12');
    var opt13 = document.getElementById('opt13');
    var opt14 = document.getElementById('opt14');
    var opt15 = document.getElementById('opt15');
    
    // options online Guidance Rate
    var opt21 = document.getElementById('opt11');
    var opt22 = document.getElementById('opt12');
    var opt23 = document.getElementById('opt13');
    var opt24 = document.getElementById('opt14');
    var opt25 = document.getElementById('opt15');
    var opt26 = document.getElementById('opt15');

    // options creating Account Rate
    var opt31 = document.getElementById('opt31');
    var opt32 = document.getElementById('opt32');
    var opt33 = document.getElementById('opt33');
    var opt34 = document.getElementById('opt34');
    var opt35 = document.getElementById('opt35');
    var opt36 = document.getElementById('opt36');

    // options on Boarding Rate
    var opt41 = document.getElementById('opt41');
    var opt42 = document.getElementById('opt42');
    var opt43 = document.getElementById('opt43');
    var opt44 = document.getElementById('opt44');
    var opt45 = document.getElementById('opt45');
    var opt46 = document.getElementById('opt46');

    //options tasks Rate
    var opt51 = document.getElementById('opt51');
    var opt52 = document.getElementById('opt52');
    var opt53 = document.getElementById('opt53');
    var opt54 = document.getElementById('opt54');
    var opt55 = document.getElementById('opt55');
    var opt56 = document.getElementById('opt56');


    form.addEventListener('change', function() {

        let isSatisfactionRateChecked = opt1.checked || opt2.checked || opt3.checked | opt4.checked || opt5.checked;
        let isUserRegistrationRateChecked = opt11.checked || opt12.checked || opt13.checked | opt14.checked || opt15.checked;
        let isOnlineguidanceRateChecked = opt21.checked || opt22.checked || opt23.checked | opt24.checked || opt25.checked || opt26.checked;
        let isCreatingAccountRateChecked = opt31.checked || opt32.checked || opt33.checked | opt34.checked || opt35.checked || opt36.checked;
        let isOnBoardingRateChecked = opt41.checked || opt42.checked || opt43.checked | opt44.checked || opt45.checked || opt46.checked;
        let isTasksRateChecked = opt51.checked || opt52.checked || opt53.checked | opt54.checked || opt55.checked || opt56.checked;
        

        
        if (isSatisfactionRateChecked || 
            isUserRegistrationRateChecked ||
            isOnlineguidanceRateChecked ||
            isCreatingAccountRateChecked ||
            isOnBoardingRateChecked ||
            isTasksRateChecked ||
            document.getElementById('more-details').value.length > 0){            

            document.getElementById('error').hidden = true;
        } 
    });

    form.addEventListener("submit", function (event) {
    
          // stop the form from submitting the normal way and refreshing the page
        event.preventDefault();
         
        err.hidden = true;        
        let isSatisfactionRateChecked = opt1.checked || opt2.checked || opt3.checked | opt4.checked || opt5.checked;
        let isUserRegistrationRateChecked = opt11.checked || opt12.checked || opt13.checked | opt14.checked || opt15.checked;
        let isOnlineguidanceRateChecked = opt21.checked || opt22.checked || opt23.checked | opt24.checked || opt25.checked || opt26.checked;
        let isCreatingAccountRateChecked = opt31.checked || opt32.checked || opt33.checked | opt34.checked || opt35.checked || opt36.checked;
        let isOnBoardingRateChecked = opt41.checked || opt42.checked || opt43.checked | opt44.checked || opt45.checked || opt46.checked;
        let isTasksRateChecked = opt51.checked || opt52.checked || opt53.checked | opt54.checked || opt55.checked || opt56.checked;
        


        let valid = true;
        if (!isSatisfactionRateChecked && 
            !isUserRegistrationRateChecked && 
            !isOnlineguidanceRateChecked && 
            !isCreatingAccountRateChecked && 
            !isOnBoardingRateChecked && 
            !isTasksRateChecked && 
            improvementSuggestion.value.length === 0
            
            ){
            valid = false;
            err.hidden = false
            location.hash = "#error"
        }

        if (valid) {
            
            
        	// there are many ways to get this data using jQuery (you can use the class or id also)
	        var formData = {
	            'satisfactionRate': $('input[name=satisfactionRate]:checked').val(),	            
	            'userRegistrationRate': $('input[name=userRegistrationRate]:checked').val(),
	            'onlineGuidanceRate': $('input[name=onlineGuidanceRate]:checked').val(),
	            'creatingAccountRate': $('input[name=creatingAccountRate]:checked').val(),
	            'onBoardingRate': $('input[name=onBoardingRate]:checked').val(),
	            'tasksRate': $('input[name=tasksRate]:checked').val(),
	            'improvementSuggestion': $('textarea[name=improvementSuggestion]').val(),
	            'timestamp': $('input[name=timestamp]').val()
	        };
	        	                                    
            $.ajax({
                headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
                },
                type: 'POST',                                    
                url: '/api-user-feedback/survey',
                data: formData,
                success: function(response) {
                    console.log('success');
                    window.location.href = "confirmation.html";
                },
                error: function(response) {
                    console.error('error');            
                    window.location.href = "error.html";
                }
            })      
        }
                
        
    }, false);
       
  });

function setLocaleGdsFormat(timestamp) {
    let locale = moment().locale('uk');
    timestamp.value = locale.format('DD MMM yyyy.hh:mma');
}
