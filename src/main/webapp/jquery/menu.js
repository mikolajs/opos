var isMenuOpen = true;

$(function(){
	closeMenu();
});


document.getElementById('closeButton').addEventListener('click', function(e){
    e.stopPropagation();
    closeMenu();
});
document.getElementById('upperBar').addEventListener("click", function(e) {
    e.stopPropagation();
    if(isMenuOpen) closeMenu();
    else openMenu();
});


/*
var isMenuOpen = true;
var stopWork = false;

console.log("start menu");

document.getElementById('closeButton').addEventListener('click', function(e){
    e.stopPropagation();
    stopWork = true;
    closeMenu();
    setTimeout(function(){
        stopWork = false;
    }, 500);

});
document.getElementById('myNavbar').addEventListener("click", openMenu);
document.getElementById('myNavbar').addEventListener("mouseover",
    function(e){
        e.stopPropagation();
        if(!stopWork) {
            openMenu();
            //console.log("mouseover navbar");
        }
    });
document.addEventListener("mouseover",
    function(e) {
        e.stopPropagation();
        if(!stopWork){
            closeMenu();
            //console.log("mouseover body");
        }
    });

*/



function openMenu() {
    //console.log("openMenu");
    if(!isMenuOpen){
        $('#linkarea').slideDown(1);
         isMenuOpen = true;
    }

}

function closeMenu() {
    if(isMenuOpen) {
        $('#linkarea').slideUp(1);
         isMenuOpen = false;
    }

}




