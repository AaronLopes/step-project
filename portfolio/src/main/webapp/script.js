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

const greetings =['Hi, nice to meet you!', '¡Hola, encantado de conocerte!', '你好，很高興見到你', 'Salut! Enchanté!'];
const textElement = document.getElementById('changeText');
textElement.innerHTML = greetings[0];
let counter = 1;
setInterval(change, 2000);
document.getElementById('commentBtn').addEventListener('click', getComments);
const markers = []

/**
 * Cycles through greetings in different languages for front page. 
 */
function change() {
  textElement.innerHTML = greetings[counter];
  counter++;
  if (counter >= greetings.length) {
    counter = 0;
  }
}

class Landmark {
  constructor(title, lat, lng, description) {
    this.title = title;
    this.lat = lat;
    this.lng = lng;
    this.description = description;     
  }
}

function initMap() {
    const map = new google.maps.Map(
      document.getElementById('map'),
      {
        center: {
          lat: 37.422403,
          lng: -122.088073
        },
        zoom: 5
      }
    );

    const landmarks = [
      new Landmark('Home', 37.3230, -122.0322, 'Where I currently live, and spent my high school years. \
        Home to Apple HQ, the best boba, and my closest friends :)'),
      new Landmark('UMass Amherst', 42.3868, -72.5301, 'Snowy, remote, but cozy; where I spent my first \
        1.5 years of college.'),
      new Landmark('Georgia Tech', 33.7756, -84.3963, 'Where I currently attend college!'),
      new Landmark('Home, Away From Home', 19.4564, 72.7925, 'Where my parents grew up; the comfort of my \
        extended family and the cities unique culture always brings me back'),
      new Landmark('An Extraordinary Layover', 51.5074, -0.1278, 'Spent an ambitious 8 hours searching for \
        all the famous landmarks, check out pictures in misc!')
    ];

    for (let landmark of landmarks) {
      addLandmark(map, landmark);
    }
    // add user landmark
};

function addLandmark(map, landmark) {
  const infoWindow = new google.maps.InfoWindow({content: landmark.description});
  const marker = new google.maps.Marker(
    {
      position: {lat: landmark.lat, lng: landmark.lng},
      map: map,
      title: landmark.title,
      info: infoWindow
    }
  );
  markers.push(marker);
  marker.addListener('click', () => {
    closeAllInfoWindows(map);
    marker.info.open(map, marker);
    let latLng = new google.maps.LatLng(landmark.lat, landmark.lng);
    map.panTo(latLng);
    map.setZoom(10);
  });
}

function closeAllInfoWindows(map) {
  markers.forEach(function(marker) {
    marker.info.close(map, marker);
  });
}

function getUserLandmark(map) {
  const markerTitle = document.getElementById("title-box");
  const markerDesc = document.getElementById("desc-box");

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
      var pos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      };

      infoWindow.setPosition(pos);
      infoWindow.setContent('Location found.');
      infoWindow.open(map);
      map.setCenter(pos);
    }, function() {
      handleLocationError(true, infoWindow, map.getCenter());
    });

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
