

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
	            html : '<iframe id="imageLoaderFrame" src="/imgstorage"  style="width:100%;min-height:250px;" frameborder="0" onload="getImageURLfromIFrame(this)" ></iframe>'
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
 
