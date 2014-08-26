	
	var EditHeadWord = dejavu.Class.declare({
		
		slideMaxNr : 4,
		$slidesHTML : null,
		$slideChoice : null,
		slideSize : 0,
		$listItem : null,
		
		initialize : function(maxSize) {
			this.slideMaxNr = maxSize;
			this.$slidesHTML = jQuery($.parseHTML('<div>'+$('#headWordsData').val()+'</div>'));
			//alert(this.$slidesHTML.html());
			this.slideSize = this.$slidesHTML.children('section').length;
			this.createPage();
			//alert(this.slideSize);
			var self = this;
			$('#save').click(function(){self.createData();});
			$('#addSlideAction').click(function(){self.addSlideAction();});
			$('#delSlideAction').click(function(){self.delSlide();});	
			
			if(this.slideSize == 0) this.slideSize = 1;
			for( i = this.slideSize +1;  i <  5;  i++){
		    	$('#section_'+ i).hide();
		    }		
		},
		
		createPage : function(){	
			var self = this;
		    this.$slidesHTML.children('section').each(function(index){
				var $this = $(this);
				//alert($('#slideInfo_'+ (index + 1) +  ' input:first').attr('checked'));
				var html = $this.html();
				//alert(html + "index: " + (index +1));
			    $('#section_'+ (index + 1)).html(html);
			}); 	
		},
		
		addSlideAction : function(){
			if(this.slideMaxNr <=  this.slideSize) {
				alert("Maksmalna ilość slajdów to: " + this.slideMaxNr);
				return;
			}
			this.slideSize++;
			$("#section_"+ this.slideSize ).show();
		},
		
		delSlide : function(){
			if (this.slideSize == 1) {
				alert("Nie można usunąć ostatniego slajdu, skasuj całe hasło.");
				return;
			}
			if(confirm('Na pewno chcesz usunąć slajd nr ' + this.slideSize + ' ?')){
				$("#section_"+ this.slideSize ).html("").hide();
				this.slideSize--;
			}
		},
			
		createData :  function(){
			//if(!isValid(document.getElementById('save'))) return false;
			var dataSlides = "";
			for(i = 1; i <= this.slideSize; i++){
				var $section = $('#section_'+i);
				var $checkbox = $('#slideInfo_'+ i + ' input:first');
				if($section.height() > 600 && $checkbox.prop('checked') ) {
					$checkbox.removeAttr('checked');
					alert("Element za długi na slajd");
				}
				var html = $section.html();
				
				var isSlide = $checkbox.prop('checked');
				dataSlides += '<section  class="slide">' + html.toString() + '</section>';
			}
			$('#headWordsData').val(dataSlides);
		    
			//alert(dataSlides);
			return true;
		}
		
	});
	
	
	
	
	
		
					
		