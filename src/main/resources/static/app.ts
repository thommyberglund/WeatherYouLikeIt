var angmodule = angular.module('demo', ['ngSanitize']);


angmodule.controller('search', function ($scope, $http) {
    $scope.sendToBackEnd = () => {

        function splitTheString(date) {
            var dateFormat = (JSON.stringify(date));
            var noSnuffs = dateFormat.replace('"', '');
            noSnuffs = noSnuffs.replace('"', '');
            var splitDate = noSnuffs.split('T');
            date = splitDate[0];
            return date;
        }

        $scope.data.startDate = splitTheString($scope.data.startDate);
        $scope.data.endDate = splitTheString($scope.data.endDate);

        console.log(JSON.stringify($scope.data));

        if ($scope.data.tempMin > $scope.data.tempMax) {
            var tmp = $scope.data.tempMin;
            $scope.data.tempMin = $scope.data.tempMax;
            $scope.data.tempMax = tmp;
        }

        console.log(JSON.stringify($scope.data));

        // $http.post('search', $scope.data).then(function (response) {
        $http.get('http://rest-service.guides.spring.io/greeting', $scope.data).then(function (response) {
            // let data = response.data;
            console.log(data);


            let data = [
                {destination: "Stockholm", country: "Muffinland", temperature: 21, price: 1025},
                {destination: "Stockholm", country: "Kaninland", temperature: 27, price: 2750},
                {destination: "Stockholm", country: "Minland", temperature: 56, price: 2687},
                {destination: "Stockholm", country: "Muffinland", temperature: 21, price: 1025},
                {destination: "Stockholm", country: "Kaninland", temperature: 27, price: 2750},
                {destination: "Stockholm", country: "Minland", temperature: 56, price: 2687},
                {destination: "Stockholm", country: "Muffinland", temperature: 21, price: 1025},
                {destination: "Stockholm", country: "Kaninland", temperature: 27, price: 2750},
                {destination: "Stockholm", country: "Minland", temperature: 56, price: 2687},
                {destination: "Stockholm", country: "Minland", temperature: 50, price: 1560}
            ];

            let htmlResult = "";
            data.forEach((d) => {
                htmlResult +=
                    '<div class="resultTable">' +
                    '<div class="resultCity">' + d.destination + ', ' + d.country + '</div>' +
                    '<div class="item resultDurationTo">Duration ' + $scope.data.startDate + ': 4.30h' +
                    '<br>Stops: ' + '1' + '<img src="time.png"/>' + '</div>' +
                    '<div class="item resultDurationFrom">Duration ' + $scope.data.startDate + ': 2.10h' +
                    '<br>Stops: ' + '0' + '<img src="timefrom.png"/>' + '</div>' +
                    '<div class="item resultSunHours">' + 'Expected sunshine: ' +
                    '<br>7 hours per day' + '<img src="sun.png"/></div>' +
                    '<div class="item resultPrice">Price per person: $' + d.price +
                    '<br>Total Price: $' + d.price * $scope.data.noadults + '<img src="price.png"/>' + '</div>' +
                    '<div class="item resultTemp">' + 'Expected temperature: ' +
                    '<br>' + d.temperature + '°C' + '<img src="thermometer.png"/>' + '</div>' +
                    '<div class="item resultTempToday">' + 'Temperature today: ' +
                    '<br>24' + '°C' + '<img src="thermometer.png"/>' + '</div>' +
                    '<div class="item resultRain">' + 'Expected precipitation:' +
                    '<br>2 mm per day' + '<img src="rain.png"/>' + '</div>' +
                    '<div class="item resultBuy">' + '<a class="btn btn-info" role="button">More information</a>' + '</div></div>'

            });

            let result = '<h1>Yay! We found ' + data.length + ' trips from ' + $scope.data.origin + ' between ' + $scope.data.startDate + ' and ' + $scope.data.endDate + '!</h1>';

            let changeSearch = '<div class="changeSearch">' + '<a href=\"#\">Change search</a>' + '</div>';

            $scope.myHTML = result + htmlResult + changeSearch;

        });


    };


});

function getVals() {
    // Get slider values
    var parent = this.parentNode;
    var slides = parent.getElementsByTagName("input");
    var slide1 = parseFloat(slides[0].value);
    var slide2 = parseFloat(slides[1].value);
    // Neither slider will clip the other, so make sure we determine which is larger
    if (slide1 > slide2) {
        var tmp = slide2;
        slide2 = slide1;
        slide1 = tmp;
    }

    var displayElement = parent.getElementsByClassName("rangeValues")[0];
    displayElement.innerHTML = "Min: " + slide1 + " °C" + " Max: " + slide2 + " °C";
}

window.onload = function () {
    // Initialize Sliders
    var sliderSections = document.getElementsByClassName("range-slider");
    for (var x = 0; x < sliderSections.length; x++) {
        var sliders = sliderSections[x].getElementsByTagName("input");
        for (var y = 0; y < sliders.length; y++) {
            if (sliders[y].type === "range") {
                sliders[y].oninput = getVals;
                // Manually trigger event first time to display values
                sliders[y].oninput();
            }
        }
    }
};

window.smoothScroll = function (target) {
    var scrollContainer = target;
    do { //find scroll container
        scrollContainer = scrollContainer.parentNode;
        if (!scrollContainer) return;
        scrollContainer.scrollTop += 1;
    } while (scrollContainer.scrollTop == 0);

    var targetY = 0;
    do { //find the top of target relatively to the container
        if (target == scrollContainer) break;
        targetY += target.offsetTop;
    } while (target = target.offsetParent);

    scroll = function (c, a, b, i) {
        i++;
        if (i > 30) return;
        c.scrollTop = a + (b - a) / 30 * i;
        setTimeout(function () {
            scroll(c, a, b, i);
        }, 10);
    };
    // start scrolling
    scroll(scrollContainer, scrollContainer.scrollTop, targetY, 0);
};