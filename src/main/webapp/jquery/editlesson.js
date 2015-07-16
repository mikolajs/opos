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
									"aaSorting" : [ [ 1, "asc" ] ],
									"aoColumnDefs" : [ {
										"bSearchable" : false,
										"aTargets" : [ 0 ]
									}, {
										"bVisible" : false,
										"aTargets" : [ 0 ]
									} ]
								});
				
				CKEDITOR.disableAutoInline = true;

				this.editor = CKEDITOR.replace('noticeEditor', {
                			width : 700,
                			height : 250,
                			allowedContent : true,
                			extraPlugins: 'sourcedialog,addImage,syntaxhighlight,formula,symbol,image',
                            format_tags : 'p;h2;h3;h4;h5;h6;pre;address',
                            language : 'pl',
                			toolbar: [
                                [ 'Sourcedialog' ],
                            	[ 'Cut', 'Copy','Paste', 'PasteText', 'PasteFromWord', '-','Undo', 'Redo' ],
                            	[ 'AddImage', 'Table','Syntaxhighlight','Formula', 'Symbol', "Image" ],
                            	[ 'Link', 'Unlink',	'Anchor' ], 	'/',
                            	[ 'Find', 'Replace','-', 'SelectAll' ],
                            	[ 'Bold', 'Italic','Underline', 'Strike','Subscript',	'Superscript', '-','RemoveFormat' ],
                            	[ 'NumberedList',	'BulletedList', '-','Outdent', 'Indent','-', 'Blockquote', '-','JustifyLeft',
                            	'JustifyCenter','JustifyRight',	'JustifyBlock' ], 	'/',
                            	[ 'Styles', 'Format',	'Font', 'FontSize' ]
                            ]
                		});


				this.$dialog = $('#dialog');
				this.$dialog.dialog( {
				 autoOpen: false,
                  height: 450,
                  width: 750,
                  modal: true});
				
				
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
							var jsonString = "[";
							$('#lessonList').children('li').each(
									function() {
										var $this = $(this);
										$title = $this.children('.title')
												.first();
										var title = $title.text();
										var id = $title.attr('name');
										var descript = $this.children(
												'.descript').html().toString();
										var what = $this.children('.what')
												.children('img').first().attr(
														'title');
										jsonString += '{"what": "' + what
												+ '", "id": "' + id
												+ '", "title":"' + title
												+ '", "descript":"' + descript
												+ '"},';
									});
							jsonString += "]";
							$('#json').val(jsonString);
							$('#extraText').val(
									$('#extraTextEdit').html().toString());
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

			_createItem : function(item) {
				var mapIco = {
					q : "quiz.png",
					w : "presentation.png",
					v : "video.png",
					d : "document.png",
					n : "notice.png"
				};
				var title = '<span class="title" name="' + item.id + '">'
						+ item.title + '</span>';
				var descript = '<div class="descript">' + item.descript + '</div>';
				var what = '<span class="what"><img src="/style/images/'
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


			refreshDataTable : function() {
				console.log("begin refresh " + this.$name);
				var data = $('#jsonForDataTable').val();
				this.table.fnClearTable();
			    var array = eval( data );
				this.table.fnAddData(array);
				this.table.fnDraw();
				this._bindInsertData();

			},

			createNewItem : function(tr, self) {
				//alert("createNewItem");
				var item = new Object();
				var aData = self.table.fnGetData(tr);
				item.title = aData[1];
				item.id = "_" + aData[0];
				item.descript = aData[2];
				item.what = $('#getItemType option:selected').val();
				var str = self._createItem(item);
				self.$listBody.append(str);
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
				$(elem).parent('li').remove();
			},

			_bindSortableList : function() {
				this.$listBody.sortable();
			},

		});
