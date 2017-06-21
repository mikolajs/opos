var LessonEditor = dejavu.Class
		.declare({
			$name : "LessonEditor",
			$extra : null,
			sendFormId : "#send",
			$listBody : null,
			$json : null,
			$window : null,
			$edited : null,
			$dialog: null,
			table : null,
			editor: null,
			isEditNotice: false,
			editedNoticeDesc: null,

			initialize : function() {

				this.table = jQuery('#choiceTable')
						.dataTable(
								{
									"sPaginationType" : "two_button",
									"bFilter" : true,
									"iDisplayLength" : 20,
									"bLengthChange" : true,
									"oLanguage" : {
										"sSearch" : "Filtruj wiersze: ",
										"sZeroRecords" : "Brak danych do wyświetlenia",
										"sInfoEmpty" : "Brak danych do wyświetlenia",
										"sEmptyTable" : "Brak danych do wyświetlenia",
										"sInfo" : "Widzisz wiersze od _START_ do _END_  z wszystkich _TOTAL_",
										"oPaginate" : {
											"sPrevious" : "Poprzednie",
											"sNext" : "Następne",
											"sFirst" : "Początek",
											"sLast" : "Koniec",
										},
										"sInfoFiltered" : " - odfiltrowano z _MAX_ wierszy",
										"sLengthMenu" : 'Pokaż <select>'
												+ '<option value="20">10</option>'
												+ '<option value="50">20</option>'
												+ '<option value="100">50</option>'
												+ '<option value="-1">całość</option>'
												+ '</select> wierszy'
									},
									"aaSorting" : [ [ 1, "asc" ] ]
								});
				
				CKEDITOR.disableAutoInline = true;

				this.editor = CKEDITOR.replace('noticeEditor', {
                			width : 700,
                			height : 220,
                			allowedContent : true,
                			format_tags : 'p;h2;h3;h4;h5;h6;pre;address',
                			language : 'pl',
                			extraPlugins : 'youtube,addImage,addFile,codesnippet,mathjax,specialchar',
                            toolbar: [
                               { name: 'document', items: [ 'Sourcedialog' ] },
                          { name: 'basicstyles', items: [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ]},
                           { name: 'edit', items: [ 'PasteText','PasteFromWord','Undo','Redo' ] },
                            { name: 'extraedit', items: [ 'Find','Replace','SelectAll' ]},
                                   			{ name: 'paragraph', items:
                                   			 [ 'NumberedList','BulletedList','-','Outdent','Indent','-',
                                   			 'Blockquote','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock',
                                 			 'BidiLtr','BidiRtl' ] },
                                    			{ name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
                                       			{ name: 'insert', items: [ 'CodeSnippet', 'Mathjax', 'AddFile', 'AddImage', 'Table', 'Youtube','SpecialChar'] },
                                    			{ name: 'styles', items: [ 'Format', 'Styles', 'FontSize', 'Styles', 'TextColor','BGColor'] }
                             ],
                             extraPlugins: 'codesnippet,mathjax,youtube,addFile,addImage,sourcedialog,specialchar',
                             mathJaxLib : 'https://cdn.mathjax.org/mathjax/2.6-latest/MathJax.js?config=TeX-AMS_HTML'
                		});


				this.$dialog = $('#dialog');
				this.$dialog.dialog( {
				 autoOpen: false,
                  height: 500,
                  width: 735,
                  modal: false
                  });
				
				
				this._bindChapterNameSwitch();
				//alert("table")
				this._prepareVar();
				this._bindBeforeSubmit();
				//alert("List");
				//this.refreshDataTable("");
				//alert("refresh");
				this._createList();
				//alert("bindRefesh");
				//this._bindRefreshData();
				//alert("bindSortable");
				this._bindSortableList();

			},

			_bindChapterNameSwitch : function() {
				var $check = $("#chapterNameIsNew");
				this.setChapterNew($check.prop("checked"));
				var self = this;
				$check.change(function() {
					self.setChapterNew($check.prop("checked"));
				});
			},

			setChapterNew : function(isNew) {
				if (isNew) {
					$("#chapterNameNewGroup").show();
					$("#chapterNameExistsGroup").hide();
				} else {
					$("#chapterNameNewGroup").hide();
					$("#chapterNameExistsGroup").show();
				}
			},

			_prepareVar : function() {
				this.$listBody = $('#lessonList').first();
				var extra = $('#extraText').val();
				
				//$('#extraTextEdit').html(extra);
				var jsonStr = $('#json').val();
				this.$json = eval('(' + jsonStr + ')');
			},

			_bindBeforeSubmit : function() {
				var self = this;
				$(this.sendFormId).submit(
						function() {
							//alert("before submit");
							self.copyFromEditorToInput();
							//alert($('#extraText').val());
							var jsonArray = [];
							$('#lessonList').children('li').each(
									function() {
										var $this = $(this);
										$title = $this.children('.title').first();
									    var objJson = {};
										objJson.title = $title.text();
										objJson.id = $title.attr('name');
										objJson.descript = $this.children(
												'.descript').html().toString();
										objJson.what = $this.children('.what')
												.children('img').first().attr('title');
										jsonArray.push(objJson);
									});

							$('#json').val(JSON.stringify(jsonArray));
							$('#extraText').val($('#extraTextEdit').html().toString());
							//alert($('#extraText').val() + " J: " + jsonString );
							return true;
						});
			},

			copyFromEditorToInput : function() {
				$('#extraText').val($('#extraTextEdit').html());
			},

			_createList : function() {
				var strItem = ""
				for (i in this.$json) {
					strItem = this._createItem(this.$json[i]);
					this.$listBody.append(strItem);
				}
			},
            ///change to mk notice and convert from datatable element to list element
			_createItem : function(item) {
				var mapIco = {
					q : "quiz.png",
					p : "presentation.png",
					v : "video.png",
					d : "document.png",
					n : "notice.png"
				};
				var title = '<span class="title" name="' + item.id + '">'
						+ item.title + '</span>';
				var descript = '<div class="descript">' + item.descript + '</div>';
				var what = '<span class="what"><img src="/images/'
						+ mapIco[item.what] + '" name="' + item.what
						+ '" title="' + item.what + '" /></span>';
				var del = '<button class="btn btn-danger imgDel" onclick="lessonEditor.deleteData(this);">' +
				   '<span class="glyphicon glyphicon-remove-sign" ></span></button>';
				var edit = "";
				if(item.what == "n")
				    edit =  '<button class="btn btn-info imgEdit" onclick="lessonEditor.editData(this);">' +
                            '<span class="glyphicon glyphicon-pencil" ></span></button>';
				return '<li class="list-group-item">' + what + title + del + edit
						+ '<br/>' + descript + '</li>';
			},
                //item for create in datatable
			_createItemNoClose : function(item) {
            				var mapIco = {
            					q : "quiz.png",
            					p : "presentation.png",
            					v : "video.png",
            					d : "document.png"
            				};
            				var title = '<span class="title" name="_' + item.id + '">'
            						+ item.title + '</span>';
            				var descript = '<div class="descript">' + item.descript + '</div>';
            				var what = '<span class="what"><img src="/images/'
            						+ mapIco[item.what] + '" name="' + item.what
            						+ '" title="' + item.what + '" /></span>';
            				var edit = "";
            				return '<li class="list-group-item">' + what + title
            						+ '<br/>' + descript + '</li>';
            			},


			refreshDataTable : function() {
				var data = $('#jsonForDataTable').val();
				this.table.fnClearTable();
			    var array = eval( data );
			    console.log(JSON.stringify(array));
				this.table.fnAddData(this._insertItemToDataTable(array));
				this.table.fnDraw();
				this._bindInsertData();

			},

			_insertItemToDataTable : function(array) {
			    var itemTable = [];
			    for(var i = 0; i < array.length; i++){
			        var item = new Object();
			        item.title = array[i][1];
			        var id = array[i][0];
			        item.id = array[i][0];
			        item.descript = array[i][2];
			        item.what = $('#getItemType option:selected').val();
			        if(item.what === "q") item.title = "Zadanie " + item.title;
                    itemTable.push(this._createItemNoClose(item))
			    }
			    return itemTable;
			},

			createNewItem : function(tr, self) {
				//alert("createNewItem");
				var item = new Object();
				var aData = self.table.fnGetData(tr);

				var $obj = jQuery(aData[0]);
				var what = $obj.children('span.what').children('img').attr('name');
				var del = '<button class="btn btn-danger imgDel" onclick="lessonEditor.deleteData(this);">' +
                				   '<span class="glyphicon glyphicon-remove-sign" ></span></button>';
                var edit = "";

                $obj.children('span.title').after(del + edit);

				//if(item.what === "q") item.title = "Zadanie " + item.title;
				//var str = self._createItem(item);
				self.$listBody.append($obj);
			},

			addNotice : function() {
			  this.editor.setData("");
			  this.$dialog.dialog("open");
			  this.isEditNotice = false;
			},

			saveNotice : function() {
			    var noticeCont = this.editor.getData();
                if(!this.isEditNotice){
			        var noticeItem = {};
			        noticeItem.id = 0;
			        noticeItem.what = "n";
			        noticeItem.descript = noticeCont;
			        noticeItem.title = "Notatka";
			        this.$listBody.append(this._createItem(noticeItem));
			    }
			    else {
			        this.editedNoticeDesc.html(noticeCont);
			    }
			    this.$dialog.dialog('close');
			},

			editData : function(elem) {
			    this.isEditNotice = true;
			    this.editedNoticeDesc = $(elem).parent().children('div.descript');
			    this.editor.setData(this.editedNoticeDesc.html());
			    this.$dialog.dialog("open");
			},

			_bindInsertData : function() {
				var self = this;
				this.table.$('tr').each(function() {
					$(this).click(function() {
						self.createNewItem(this, self);
					});
				});
			},

			deleteData : function(elem) {
			    //alert($(elem).parent("li").get(0).innerHTML);
				$(elem).parent('li').remove();
			},

			_bindSortableList : function() {
				this.$listBody.sortable();
			},

		});
