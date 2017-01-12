 var xmlhttp = new XMLHttpRequest();
   var nr = 0;
   var interval = 5000;
   var reload = 0; //3580000;
   var imgList = [];
   var imgElements = [];
   var imgCont = document.getElementById('imgContener');
   var load = false;
   mkReload();

   setInterval(getNext, interval);

   function getNext(){
        if(load == true) closeLoader();
        if(nr >= imgElements.length)  nr = 0;

        imgElements[nr].style.display = "inline";
        imgElements[nr].setAttribute('class', 'showImg');
        if(nr != 0) {
            //imgElements[nr - 1].style.display = "none";
            imgElements[nr - 1].setAttribute('class', 'fadeImg');
        }
        else {
            //imgElements[imgElements.length - 1].style.display = "none";
            imgElements[imgElements.length - 1].setAttribute('class', 'fadeImg');
        }
        nr++;
   }

   function mkReload() {
      var url = "/getimgslide";
      xmlhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
          console.log("status OK \n" + this.responseText);
          var imgR = JSON.parse(this.responseText);
          imgElements.slice(0, 0);
            for(var i = 0; i < imgR.images.length; i++){
            showLoader();
              var img = document.createElement('img');
              img.style.position = "fixed";
              img.src = imgR.images[i];
              console.log("found " + img.src);
              imgCont.appendChild(img);
              imgElements.push(img);
              img.onload = function(){
                 var errorDelta = 0.2;
    	         var h = parseFloat(screen.height);
                 var w = parseFloat(screen.width);
                 console.log("me" + this + " image: " + this.src +
                    ", size: " + this.height + " : " + this.width);
                 var propImg = this.width / this.height;
                 var propScreen = w / h;
                 var delta = propScreen - propImg;

                 if(Math.abs(delta) <= errorDelta) {
                    this.style.top = "0px";
                    this.style.left = "0px";
                    this.style.width = w.toString() + "px";
     	            this.style.height = h.toString() + "px";
                 } else if(delta < 0) {
                    this.style.top =
                    Math.floor(Math.abs((h - w*this.height/this.width)) / 2.0).toString() + "px";
                    this.style.left = "0px";
                    this.style.width = w.toString() + "px";
     	            this.style.height = "auto";
                } else {
                    this.style.top = "0px";
                    this.style.left =
                    Math.floor(Math.abs((w - h*this.width/this.height)) / 2.0).toString() + "px";
                    this.style.width = "auto";
     	            this.style.height = h.toString() + "px";
                }
                console.log("top: " + this.style.top + " left " +
                this.style.left + " width: " + this.style.width + " height: " + this.style.height);
                this.setAttribute('class', 'fadeImg');
              };

            }

                //console.log(imgPos);
            interval = imgR.time * 1000;
            reload = imgR.reload * 1000;
        } else {console.log("not ready status: " + this.status)};
      };
      xmlhttp.open("GET", url, true);
      xmlhttp.send();
   }

function showLoader(){
    load = true;
    document.getElementById("loader").style.display ="inline";
    document.getElementById("imgContener").style.display ="none";

}
function closeLoader(){
    load = false;
    document.getElementById("loader").style.display ="none";
    document.getElementById("imgContener").style.display ="inline";
}