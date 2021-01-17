


var EditWork=  dejavu.Class.declare({

	initialize : function() {
		 this.loadQuizzesTrig();
	},

	refreshedCourse : function(){
	 var courseId = $("#coursesWork").children("option:selected").val();
	 $("#courseWorkId").val(courseId).change().blur();
	},

	refreshedLesson : function(){
	 var lessId = "";
	 lessId = $("#lessonWorkSelect").children("option:selected").val();
	 if(lessId == undefined) lessId = $('#lessonWorkSelect').first(),val();
	 $("#lessonWorkId").val(lessId);
	 $("#triggerLesson").val(lessId);
	 console.log("refreshedLesson: " + lessId );
	 this.loadQuizzesTrig();
	},

    loadQuizzesTrig: function() {
       $("#triggerLesson").change().blur();
    },


});