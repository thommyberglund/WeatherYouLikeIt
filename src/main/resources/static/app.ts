let module = angular.module('demo', ['ngSanitize']);


/*module.controller('Hello', function ($scope, $http) {
    $http.get('http://rest-service.guides.spring.io/greeting').then(function (response) {
        $scope.greeting = response.data;
    });
});*/


module.controller('search', function ($scope, $http) {
    $scope.sendToBackEnd = () => {
        console.log(JSON.stringify($scope.data));

//        $http.post('http://rest-service.guides.spring.io/greeting', $scope.data).then(function (response) {
        $http.get('http://rest-service.guides.spring.io/greeting').then(function (response) {

            //let data = response.data;

            let data = [
                {country: "Muffinland", degrees: 21, price: 1025},
                {country: "Kaninland", degrees: 27, price: 2750},
                {country: "Minland", degrees: 56, price: 2687},
                {country: "Muffinland", degrees: 21, price: 1025},
                {country: "Kaninland", degrees: 27, price: 2750},
                {country: "Minland", degrees: 56, price: 2687},
                {country: "Muffinland", degrees: 21, price: 1025},
                {country: "Kaninland", degrees: 27, price: 2750},
                {country: "Minland", degrees: 56, price: 2687},
                {country: "Minland", degrees: 50, price: 1560}
            ];

            let htmlResult = "";
            data.forEach((d) => {
                htmlResult +=
                    '<div class="resultDiv">' +
                    '<span>' + d.country + '</span>' +
                    '<span>' + d.degrees + '</span>' +
                    '<span>' + d.price + '</span>' +
                    '</div>';
            });

            $scope.myHTML = htmlResult;

        });

    };
});
