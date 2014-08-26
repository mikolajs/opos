

CKEDITOR.plugins.add( 'formula',
          {
            init: function( editor )
            {  
              editor.addCommand( 'formula', new CKEDITOR.dialogCommand( 'formulaDialog' ));
        
              editor.ui.addButton( 'Formula',
              {
                label: 'Edytuj formułę',
                command: 'formula',
                icon: this.path + 'image.png'
              } );
        
        CKEDITOR.dialog.add( 'formulaDialog', function( editor )
    {
      return {
        title : 'Edytor wzorów',
        minWidth : 500,
        minHeight : 300,
        contents :
        [
          {
            id : 'tab1',
            label : 'Edycja wzorów',
            elements :
            [
	      {
	       type : 'html',
	       html : '<textarea id="formulaEditor" onkeyup="displayFormula(this)" class="cke_dialog_ui_input_textarea"></textarea>'
	      },
	      {
	       type : 'html',
	       html : '<div id="formulaDisplay"></div>'
	      }
            ]
          },
          {
            id : 'tab2',
            label : 'Pomoc',
            elements :
            [
	      {
	       type : 'html',
	       html : '<iframe src="/static/asciitomathhelp.html" style="width:100%;min-height:250px;" ></iframe>'
	      }
            ]
          },
          
        ],
	
        onOk : function()
        {
	 var mathFormula = document.getElementById('formulaDisplay').innerHTML;
	 var div = CKEDITOR.dom.element.createFromHtml(mathFormula);
	 editor.insertElement(div);
        }
       
      };
    } );
               
             
            }
          } );
 
