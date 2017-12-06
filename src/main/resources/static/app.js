var angmodule = angular.module('demo', ['ngSanitize']);
angmodule.controller('search', function ($scope, $http) {
    $scope.sendToBackEnd = function () {
        function splitTheString(date) {
            var dateFormat = (JSON.stringify(date));
            var noSnuffs = dateFormat.replace('"', '');
            var splitDate = noSnuffs.split('T');
            date = splitDate[0];
            return date;
        }
        $scope.data.startDate = splitTheString($scope.data.startDate);
        $scope.data.endDate = splitTheString($scope.data.endDate);
        console.log(JSON.stringify($scope.data));
        $http.post('https://weatheryoulikeit.herokuapp.com/getFlightResults', $scope.data).then(function (response) {
            //$http.get('http://rest-service.guides.spring.io/greeting' + $scope.data).then(function (response) {
            var data = response.data;
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
                        '<span>' + d.country + '</span>' +
                        '<span>' + d.degrees + '</span>' +
                        '<span>' + d.price + '</span>' +
                        '</div>';
            });
            var result = '<h1>We found ' + data.length + ' trips!</h1>' + '<h3>' +
                '<span>Destination</span>' +
                '<span>Average temperature</span>' +
                '<span>Price</span>' + '</h3>';
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
    displayElement.innerHTML = slide1 + " - " + slide2;
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
//# sourceMappingURL=app.js.map