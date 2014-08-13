

CKEDITOR.plugins.add( 'youTube',
          {
            init: function( editor )
            {  
              editor.addCommand( 'youTube', new CKEDITOR.dialogCommand( 'youTubeDialog' ));
        
              editor.ui.addButton( 'YouTube',
              {
                label: 'YouTube -iframe',
                command: 'youTube',
                icon: this.path + 'youtube.png'
              } );
        
        CKEDITOR.dialog.add( 'youTubeDialog', function( editor )
    {
      return {
        title : 'Dodaj obrazek',
        minWidth : 400,
        minHeight : 200,
        contents :
        [
          {
            id : 'tab1',
            label : 'Basic Settings',
            elements :
            [
              {
                type : 'textarea',
                id : 'iFrame',
                label : 'Wklej znacznik iframe skopiowany YouTube',
                validate : CKEDITOR.dialog.validate.notEmpty('Nie może być pusty')
              }
              
            ]
          },
          
        ],
	
        onOk : function()
        {
	  var iframeNode = null;
				if ( !this.fakeImage )
				{
					iframeNode = CKEDITOR.dom.element.createFromHtml( '<cke:iframe></cke:iframe>', editor.document );
				}
				else
				{
					iframeNode = this.iframeNode;
				}
				
				 var dialog = this;
		      var inner = dialog.getValueOf('tab1','iFrame');
          var arrayStr = inner.split(' ');
         // var frame = editor.document.createElement('iframe');
          var width = "100";
          var height = "100";
          var src="";
          for(i in arrayStr) {
           var a2 = arrayStr[i].split('=');
           if(a2.length > 1) {
             if (a2[0] == 'width') width =  a2[1];
             else if (a2[0] == 'height') height =  a2[1];
             else if (a2[0] == 'src') src =  a2[1];
      }
    }
    width = width.replace(/\"/gi,'');
    height = height.replace(/\"/gi,'');
    src = src.replace(/\"/gi,'');
	alert(height + width + src);
	iframeNode.setAttribute('width',width);
	iframeNode.setAttribute('height',height);
	iframeNode.setAttribute('src',src);
				var newFakeImage = editor.createFakeElement( iframeNode, 'cke_iframe', 'iframe', true );
				
				newFakeImage = newFakeImage.setAttribute('width',width);
				
	newFakeImage.setAttribute('height',height);
	newFakeImage.setAttribute('src',src);
				if ( this.fakeImage )
				{
					newFakeImage.replace( this.fakeImage );
					editor.getSelection().selectElement( newFakeImage );
				}
				else
					editor.insertElement( newFakeImage );
        
        },
       
      };
    } );
               
             
            }
          } );
