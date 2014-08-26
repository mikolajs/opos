	//get url from iframe in CKEditor and insert it in url input in window massage
		function getImageURLfromIFrame(elem){
			var innerDoc = elem.contentDocument || elem.contentWindow.document;
			var url  = innerDoc.getElementById('path').value;
			$('.cke_dialog_ui_input_text').val(url);
			$('#imagePreview').attr('src',url);
		}
		
		//for ascii to math
		//var formula = document.getElementById("formulaEditor");
		translateOnLoad=false;
		function displayFormula() {

		  var str = document.getElementById("formulaEditor").value;
		  str = "`" + str + "`";
		  var outnode = document.getElementById("formulaDisplay");
		  var n = outnode.childNodes.length;
		  for (var i=0; i<n; i++)
		    outnode.removeChild(outnode.firstChild);
		  outnode.appendChild(document.createTextNode(str));
		  AMprocessNode(outnode);
		}
		//end ascii to math