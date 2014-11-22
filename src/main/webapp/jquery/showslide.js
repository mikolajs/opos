
   $(document).ready(function() {

        $.deck('.slide');
        var arrayId = new Array();

        $('#slides').children('section').each(function() {
            arrayId.push('#' + this.id)
        });
    $.deck(arrayId);
    $('#next-slide').click(function() {
            $.deck('next');
        });
    $('#previous-slide').click(function() {
            $.deck('prev');
        });

    $('#large-slide').click(goFull);

    $('#slides').css('width', 950 + 'px' );
    $('#slides').css('height', 600 + 'px' );


      $.extend(true, $.deck.defaults, {
        selectors: {
        statusCurrent: '.deck-status-current',
        statusTotal: '.deck-status-total'
       },
        countNested: true
        });

     if(!isFirefox()){
    	 if(!isSupported()) alert("Zawartość może nie wyglądać najlepiej (szczególnie wzory) użyj przeglądarki Firefox.");
     }
    });






    var styleId = 0;
    function changeTemplate(){
    	var link = document.getElementById('style-theme-link');
    	switch(styleId) {
    	case 0:
    		link.setAttribute('href', '/deckjs/themes/style/swiss.css');
    		styleId = 1;
    	    break;
    	case 1:
    		link.setAttribute('href', '/deckjs/themes/style/web-2.0.css');
    		styleId = 2;
    	    break;
    	case 2:
    		link.setAttribute('href', '/deckjs/themes/style/neon.css');
    		styleId = 0;
    	    break;
    	default:
    	    break;
    	}
    }


    function isMSIE(){
    	var userAgent = navigator.userAgent;
    	var msie = userAgent.match(/MSIE/gi);
    	if(msie != null && msie.length > 0) return true;
		else return false;
    }

    function isChrome(){
        var userAgent = navigator.userAgent;
            	var chrome = userAgent.match(/Chrome/gi);
            	if(chrome != null && chrome.length > 0) return true;
        		else return false;
    }

    function goFull(){
    	 $('#large-slide').unbind('click');
    	 var elem = document.getElementById('thebody');
    	 if (elem.mozRequestFullScreen) {
    	      elem.mozRequestFullScreen();
    	      _makeFull();
    	    } else if (elem.webkitRequestFullScreen) {
    	      elem.webkitRequestFullScreen();
    	      _makeFull();
    	   }
    	 $('#large-slide').click(resetGoFull);
    }

    function _makeFull(){
    	 var h = parseFloat(window.screen.height) - 40.0;
    	 var w = parseFloat(window.screen.width);
    	 var prop = 0.0;
    	 if(w/h < 1.65) {
    		 prop = (w/1000.0);
    	 }
    	 else {
    		 prop = (h/610.0) ;
    	 }
    	 var translateY = parseInt(h/2)-270;
    	 $('#slides').css('transform', 'scale(' + prop + ', ' + prop + ')' ).css('top', translateY+'px');
    }


    function isFirefox(){
    	var userAgent = navigator.userAgent;
    	var n = userAgent.match(/Firefox/gi);
		if(n != null && n.length > 0) return true;
		else return false;
    }

    function isSafari(){
        var userAgent = navigator.userAgent;
            	var n = userAgent.match(/Safari/gi);
        		if(n != null && n.length > 0) return true;
        		else return false;
    }

    function isSupported() {
        return isSafari() || isFirefox();
    }

    function isOld(){
    	var userAgent = navigator.userAgent;
    	var msie = userAgent.match(/MSIE .\../gi);
    	if (msie != null){
    		var n=msie.match(/MSIE \d+\../gi);
        	var w = "";
        	if (n.lenght > 0){
        		w = n[0];
        		w = w.split(' ')[1].split('.')[0];
            	if (parseInt(w) > 8) return false;
            	else return true;
        	}
    	}

    	n = userAgent.match(/Chrome/gi);
    	if(n != null) return false;
    	n = userAgent.match(/Safari/gi);
    	if(n != null) return false;
    	n = userAgent.match(/Opera/gi);
    	if(n != null) return true;

    	return true;
    }

    function resetGoFull() {
    	 $('#large-slide').unbind('click');
    	 if (document.mozCancelFullScreen) {
   	      document.mozCancelFullScreen();
   	    } else if (document.webkitCancelFullScreen) {
   	      document.webkitCancelFullScreen();
   	   }
    	$('#slides').css('transform', 'none').css('top', '34px');
    	 $('#large-slide').click(goFull);
    }



