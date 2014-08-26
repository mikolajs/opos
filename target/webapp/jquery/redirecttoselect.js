function getSubject() {
	var subject = $("#subjectSelect option:selected").val();
	if (subject)
		return subject;
	else {
		return $("#subjectSelect option:first").val();
	}
}

function getLevel() {
	var level = $("#levelSelect option:selected").val();
	if (level)
		return level;
	else {
		return $("#levelSelect option:first").val();
	}
}

function redirectTo(where) {
	var sub = getSubject();
	document.location.href = document.location.pathname + "?s=" + sub;
}

function redirectToWithLevel() {
	var sub = getSubject();
	var lev = getLevel();
	document.location.href = document.location.pathname + "?s=" + sub + "&l="
			+ lev;
}