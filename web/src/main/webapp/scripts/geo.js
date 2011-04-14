function log(msg) {
    $('#console').append('<p>' + msg + '</p>');
}

function error(msg) {
    $('#error').append('<p>' + msg + '</p>');
}

function Server() {
    this.setLocation = function(latitude, longitude) { 
	log("lat=" + latitude + ", lng=" + longitude);
    }
}
var server = new Server();

var delay = 2 * 1000;
var latitude = 0;
var longitude = 0;
var scheduled = false;
jQuery(window).ready(function() {
    if (navigator.geolocation) {
	getLocation();
    } else {
	alert("Sorry, no navigator");
    }
});

function getLocation() {
    log("Getting location " + count++ + "...");
    navigator.geolocation.watchPosition(onLocation, onError);
}

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
    server.setLocation(latitude, longitude);
}