
    var domain = window.location.protocol + '//' + window.location.hostname;
    var logo = document.getElementById("logopng");
    var favicon = document.getElementById('favicon');

    logo.setAttribute('src', domain  + logo.getAttribute('src'));

    favicon.setAttribute('href', domain  + favicon.getAttribute('href'));
