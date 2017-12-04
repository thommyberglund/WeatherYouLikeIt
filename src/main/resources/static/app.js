var module = angular.module('demo', ['ngSanitize']);
module.controller('Hello', function ($scope, $http) {
    $http.get('http://rest-service.guides.spring.io/greeting').then(function (response) {
        $scope.greeting = response.data;
    });
});
module.controller('search', function ($scope, $http) {
    $scope.sendToBackEnd = function () {
        console.log(JSON.stringify($scope.data));
        //        $http.post('http://rest-service.guides.spring.io/greeting', $scope.data).then(function (response) {
        $http.get('http://rest-service.guides.spring.io/greeting').then(function (response) {
            //let data = response.data;
            var data = [
                { country: "Muffinland", degrees: 50, price: 10 },
                { country: "Kaninland", degrees: 50, price: 10 },
                { country: "Minland", degrees: 50, price: 10 }
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
            $scope.myHTML = htmlResult;
        });
    };
});
//# sourceMappingURL=app.js.map