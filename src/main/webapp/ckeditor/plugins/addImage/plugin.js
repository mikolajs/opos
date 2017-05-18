

CKEDITOR.plugins.add( 'addImage',
          {
            init: function( editor )
            {  
              editor.addCommand( 'addImage', new CKEDITOR.dialogCommand( 'addImageDialog' ));
        
              editor.ui.addButton( 'AddImage',
              {
                label: 'Wczytaj obrazek',
                command: 'addImage',
                icon: this.path + 'image.png'
              } );
        
        CKEDITOR.dialog.add( 'addImageDialog', function( editor )
    {
      return {
        title : 'Dodaj obrazek',
        minWidth : 800,
        minHeight : 600,
        contents :
        [
          {
            id : 'tab1',
            label : 'Obrazek',
            elements :
            [
              {
	            type : 'html',
	            html : '<h2>Adres obrazka:</h2>'
	          },
              {
                type : 'text',
                id : 'url',
                label : '',
                validate : CKEDITOR.dialog.validate.notEmpty('Nie może być pusty')
              },
	          {
	            type : 'html',
	            html : '<iframe id="imageLoaderFrame" src="/imgstorage"  style="width:100%;min-height:80px;" frameborder="0" onload="getImageURLfromIFrame(this)" ></iframe>'
	          },
              {    type: 'html',
                 html: '<img src="" id="imagePreview" style="max-height:300px;width:auto;margin:auto;"/>'
              }
            ]
          },
          
        ],
	
        onOk : function()
        {
	        var imageNode = null;
			if ( !this.fakeImage )	{
				imageNode = new CKEDITOR.dom.element( 'img', editor.document );
			}
			else {
				imageNode = this.iImageNode;
			}
			var dialog = this;
		    var url = dialog.getValueOf('tab1','url');
			imageNode.setAttribute('src',url);
			editor.insertElement( imageNode );
            document.getElementById("imagePreview").setAttribute("src", "");
        }
      };
    } );

            }
          } );
 
