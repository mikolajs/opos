

function copyLink() {
    var link = document.getElementById("linking").innerHTML;
    var topDoc = window.top.document;
    var urlElem = topDoc.getElementById('urlLink');
    if (urlElem) urlElem.value = link;
}
