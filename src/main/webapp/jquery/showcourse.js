
var ShowCourse = dejavu.Class.declare({
	isHideSidebar : true,
	isOpenForm : false,
	initialize : function() {
		var self = this;
		SyntaxHighlighter.all();

	},
	
	hideSidebar : function() {
		if (this.isHideSidebar) {
			$("#subjectsList").show();
			this.isHideSidebar = false;

		} else {
			$("#subjectsList").hide();
			this.isHideSidebar = true;
		}
	},

	checkAnswer : function(elem) {
		var $section = $(elem).parent();
		var correct = $section.children("input.correct").val();
		var info = "";
		var classInfo = "answer" + correct;
		var $infoElem = $section.children(".alertWell");
		if ($section.children("ul").children("li").children("input:checked")
				.hasClass(classInfo)) {
			info = "Bardzo dobrze!";
			$infoElem.css("color", "green");
		} else {
			info = "Błędna odpowiedź";
			$infoElem.css("color", "red");
		}
		$section.children(".alertWell").text(info);
	}
	
	
});