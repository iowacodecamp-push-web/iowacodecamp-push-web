function log(msg) {
    $('#console').append('<p>' + msg + '</p>');
}

function error(msg) {
    $('#error').append('<p>' + msg + '</p>');
}

var delay = 2 * 1000;
var latitude = 0;
var longitude = 0;
var scheduled = false;
jQuery(window).ready(function() {
    if (navigator.geolocation) {
	navigator.geolocation.watchPosition(onLocation, onError);
    } else {
	alert("Sorry, no navigator");
    }
});

function onLocation(position) {
    latitude = position.coords.latitude;
    longitude = position.coords.longitude;
    sendLocation();
    if (!scheduled) {
	setInterval("sendLocation()", delay);
	scheduled = true;
    }
}

function onError(e) {
    error(e.message);
}

function sendLocation() {
    $.post('/location', {latitude: latitude, longitude: longitude});
}