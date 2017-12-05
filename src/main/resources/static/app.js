var angmodule = angular.module('demo', ['ngSanitize']);
/*module.controller('Hello', function ($scope, $http) {
    $http.get('http://rest-service.guides.spring.io/greeting').then(function (response) {
        $scope.greeting = response.data;
    });
});*/
angmodule.controller('search', function ($scope, $http) {
    $scope.sendToBackEnd = function () {
        console.log(JSON.stringify($scope.data));
        //        $http.post('http://rest-service.guides.spring.io/greeting', $scope.data).then(function (response) {
        $http.get('http://rest-service.guides.spring.io/greeting').then(function (response) {
            //let data = response.data;
            var data = [
                { country: "Muffinland", degrees: 21, price: 1025 },
                { country: "Kaninland", degrees: 27, price: 2750 },
                { country: "Minland", degrees: 56, price: 2687 },
                { country: "Muffinland", degrees: 21, price: 1025 },
                { country: "Kaninland", degrees: 27, price: 2750 },
                { country: "Minland", degrees: 56, price: 2687 },
                { country: "Muffinland", degrees: 21, price: 1025 },
                { country: "Kaninland", degrees: 27, price: 2750 },
                { country: "Minland", degrees: 56, price: 2687 },
                { country: "Muffinland", degrees: 21, price: 1025 },
                { country: "Kaninland", degrees: 27, price: 2750 },
                { country: "Minland", degrees: 56, price: 2687 },
                { country: "Muffinland", degrees: 21, price: 1025 },
                { country: "Kaninland", degrees: 27, price: 2750 },
                { country: "Minland", degrees: 56, price: 2687 },
                { country: "Muffinland", degrees: 21, price: 1025 },
                { country: "Kaninland", degrees: 27, price: 2750 },
                { country: "Minland", degrees: 56, price: 2687 },
                { country: "Minland", degrees: 50, price: 1560 }
            ];
            var htmlResult = "";
            data.forEach(function (d) {
                htmlResult +=
                    '<div class="resultDiv">' +
                        '<span>' + d.country + '</span>' +
                        '<span>' + d.degrees + '</span>' +
                        '<span>' + d.price + '</span>' +
                        '</div>';
            });
            var result = '<h1>We found ' + data.length + ' trips!</h1>' + '<h6>' +
                '<span>Destination</span>' +
                '<span>Avgerage temperature</span>' +
                '<span>Price</span>' + '</h6>';
            $scope.myHTML = result + htmlResult;
        });
    };
});
//# sourceMappingURL=app.js.map