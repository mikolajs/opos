

CKEDITOR.plugins.add( 'addFile',
          {
            init: function( editor )
            {  
              editor.addCommand( 'addFile', new CKEDITOR.dialogCommand( 'addFileDialog' ));
        
              editor.ui.addButton( 'AddFile',
              {
                label: 'Wczytaj plik',
                command: 'addFile',
                icon: this.path + 'uploadfile.png'
              } );
        
        CKEDITOR.dialog.add( 'addFileDialog', function( editor )
    {
      return {
        title : 'Dodaj plik',
        minWidth : 450,
        minHeight : 400,
        contents :
        [
          {
            id : 'tab1',
            label : 'Załaduj plik',
            elements :
            [
              {
                type : 'text',
                id : 'url',
                label : 'Url do pliku',
                validate : CKEDITOR.dialog.validate.notEmpty('Nie może być pusty')
              },
	      {
		type: 'text',
	  	id : 'descript',
		label : 'Opis linku',
		validate : CKEDITOR.dialog.validate.notEmpty('Nie może być pusty')
	      },
	      {
	       type : 'html',
	       html : '<iframe src="/filestorage" width="100%" height="350px" frameborder="0" onload="getImageURLfromIFrame(this)" ></iframe>'
	      }
            ]
          },
          
        ],
	
        onOk : function()
        {
		var dialog = this;
		var url = dialog.getValueOf('tab1','url');
		var descript = dialog.getValueOf('tab1','descript');
		var anchorStr = '<a href="' + url + '">' + descript + '</a>'
	 	var anchor = CKEDITOR.dom.element.createFromHtml(anchorStr);
		editor.insertElement( anchor );
        
        },
       
      };
    } );
               
             
            }
          } );
 
