

CKEDITOR.plugins.add( 'addThumb',
          {
            init: function( editor )
            {  
              editor.addCommand( 'addThumb', new CKEDITOR.dialogCommand( 'addThumbDialog' ));
        
              editor.ui.addButton( 'AddThumb',
              {
                label: 'Wczytaj obrazek',
                command: 'addThumb',
                icon: this.path + 'image.png'
              } );
        
        CKEDITOR.dialog.add( 'addThumbDialog', function( editor )
    {
      return {
        title : 'Dodaj obrazek',
        minWidth : 550,
        minHeight : 400,
        contents :
        [
          {
            id : 'tab1',
            label : 'Obrazek',
            elements :
            [
              {
                type : 'text',
                id : 'url',
                label : 'Url do obazka',
                validate : CKEDITOR.dialog.validate.notEmpty('Nie może być pusty')
              },
	          {
	            type : 'html',
	            html : '<iframe id="imageLoaderFrame" src="/thumbstorage"  style="width:100%;min-height:100px;" frameborder="0" onload="getImageURLfromIFrame(this)" ></iframe>'
	          },
              {
                  type: 'html',
                  html: '<img src="" id="imagePreview" style="height:30%;width:auto;"/>'
                },
            ]
          },
          
        ],
	
        onOk : function()
        {
	  var imageNode = null;
				if ( !this.fakeImage )
				{
					imageNode = new CKEDITOR.dom.element( 'img', editor.document );
				}
				else
				{
					imageNode = this.iImageNode;
				}
				
				 var dialog = this;
		      var url = dialog.getValueOf('tab1','url');
			imageNode.setAttribute('src',url);
					editor.insertElement( imageNode );
        
        },
       
      };
    } );
               
             
            }
          } );
 
