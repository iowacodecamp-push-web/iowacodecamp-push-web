var map;
var users = new Object;
function initialize() {
    var myOptions = {
	zoom: 4,
	center: new google.maps.LatLng(37.09024,-95.712891),
	mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
}

function userAt(username, lat, lng) {
    if (users.hasOwnProperty(username)) {
	console.log(username + ' set position');
	users[username].setPosition(new google.maps.LatLng(lat, lng));
    } else {
	console.log(username + ' create marker');
	users[username] = new google.maps.Marker({
	    position: new google.maps.LatLng(lat, lng),
	    map: map,
	    title: username,
	    animation: google.maps.Animation.DROP
	});
    }
}

function userGone(username) {
    console.log(username + ' gone');
    users[username].setMap(null);
    users[username] = null;
    delete users[username];
}