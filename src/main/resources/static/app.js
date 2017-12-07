var angmodule = angular.module('demo', ['ngSanitize']);
angmodule.controller('search', function ($scope, $http) {
    $scope.sendToBackEnd = function () {
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
        $http.post('search', $scope.data).then(function (response) {
            //$http.get('http://rest-service.guides.spring.io/greeting', $scope.data).then(function (response) {
            var data = response.data;
            console.log(data);
            // let data = [
            //     {country: "Muffinland", degrees: 21, price: 1025},
            //     {country: "Kaninland", degrees: 27, price: 2750},
            //     {country: "Minland", degrees: 56, price: 2687},
            //     {country: "Muffinland", degrees: 21, price: 1025},
            //     {country: "Kaninland", degrees: 27, price: 2750},
            //     {country: "Minland", degrees: 56, price: 2687},
            //     {country: "Muffinland", degrees: 21, price: 1025},
            //     {country: "Kaninland", degrees: 27, price: 2750},
            //     {country: "Minland", degrees: 56, price: 2687},
            //     {country: "Minland", degrees: 50, price: 1560}
            // ];
            var htmlResult = "";
            data.forEach(function (d) {
                htmlResult +=
                    '<div class="resultDiv">' +
                        '<span>' + d.destination + '</span>' +
                        '<span>' + '24' + '</span>' +
                        '<span>' + d.price + '</span>' +
                        '<span>' + d.currency + '</span>' +
                        '</div>';
            });
            var result = '<h1>Yay! We found ' + data.length + ' trips!</h1>' +
                '<span>Destination</span>' +
                '<span>Average temperature</span>' +
                '<span>Price</span>' +
                '<span>Currency</span>';
            $scope.myHTML = result + htmlResult;
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
    do {
        scrollContainer = scrollContainer.parentNode;
        if (!scrollContainer)
            return;
        scrollContainer.scrollTop += 1;
    } while (scrollContainer.scrollTop == 0);
    var targetY = 0;
    do {
        if (target == scrollContainer)
            break;
        targetY += target.offsetTop;
    } while (target = target.offsetParent);
    scroll = function (c, a, b, i) {
        i++;
        if (i > 30)
            return;
        c.scrollTop = a + (b - a) / 30 * i;
        setTimeout(function () { scroll(c, a, b, i); }, 10);
    };
    // start scrolling
    scroll(scrollContainer, scrollContainer.scrollTop, targetY, 0);
};
//# sourceMappingURL=app.js.map