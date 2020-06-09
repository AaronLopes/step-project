// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
/**
 * Cycles through greetings in different languages for front page. 
 */
const greetings =['Hi, nice to meet you!', '¡Hola, encantado de conocerte!', '你好，很高興見到你', 'Salut! Enchanté!'];
var textElement = document.getElementById('changeText');
textElement.innerHTML = greetings[0];
var counter = 1;
var intervalLength = setInterval(change, 2000);
document.getElementById('commentBtn').addEventListener('click', getComments);

var script = document.createElement('script');
script.src = `https://maps.googleapis.com/maps/api/js?key=${MAPKEY}&callback=initMap`;
script.defer = true;
script.async = true;

window.initMap = function() {
    const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 37.422403, lng: -122.088073}, zoom: 5});
    addLandmark(
      map, 37.3230, -122.0322, 'Home',
      'Where I currently live, and spent my high school years. Home to Apple HQ, the best boba, and my closest friends :)');
    addLandmark(
      map, 42.3868, -72.5301, 'UMass Amherst',
      'Snowy, remote, but cozy; where I spent my first 1.5 years of college.');
    addLandmark(
      map, 33.7756, -84.3963, 'Georgia Tech',
      'Where I currently attend college!');
    addLandmark(
      map, 19.4564, 72.7925, 'Home Away From Home',
      'Where my parents grew up; the comfort of my extended family and the cities unique culture always brings me back');
    addLandmark(
      map, 51.5074, -0.1278, 'An Uncommon Layover',
      'Spent an ambitious 8 hours searching for all the famous landmarks, check out pictures in misc!');
};

document.head.appendChild(script);

function addLandmark(map, lat, lng, title, description) {
  const marker = new google.maps.Marker(
      {position: {lat: lat, lng: lng}, map: map, title: title});

  const infoWindow = new google.maps.InfoWindow({content: description});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
    var latLng = new google.maps.LatLng(lat, lng);
    map.panTo(latLng);
    map.setZoom(10);
  });
}

function change() {
  textElement.innerHTML = greetings[counter];
  counter++;
  if (counter >= greetings.length) {
    counter = 0;
  }
}

function getComments() {
  const displayValue = document.getElementById("comment-choice").value;
  fetch(`/data?comment-choice=${displayValue}`).then(response => response.json()).then((comments) => {
    const commentListElement = document.getElementById('comment-container');
    commentListElement.innerHTML = '';
    for (let comment of comments) {
      commentListElement.appendChild(createListElement(comment.commentText));
    }
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
