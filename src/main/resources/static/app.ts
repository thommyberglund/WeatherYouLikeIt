var angmodule = angular.module('demo', ['ngSanitize']);



angmodule.controller('search', function ($scope, $http) {
    $scope.sendToBackEnd = () => {

        function splitTheString(date) {
            var dateFormat = (JSON.stringify(date));
            var noSnuffs = dateFormat.replace('"','' );
            var splitDate = noSnuffs.split('T');
            date = splitDate[0];
            return date;
        }

        $scope.data.startDate = splitTheString($scope.data.startDate);
        $scope.data.endDate = splitTheString($scope.data.endDate);

        console.log(JSON.stringify($scope.data));

        $http.post('https://weatheryoulikeit.herokuapp.com/getFlightResults', $scope.data).then(function (response) {
        //$http.get('http://rest-service.guides.spring.io/greeting' + $scope.data).then(function (response) {

            let data = response.data;

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

            let htmlResult = "";
            data.forEach((d) => {
                htmlResult +=
                    '<div class="resultDiv">' +
                    '<span>' + d.country + '</span>' +
                    '<span>' + d.degrees + '</span>' +
                    '<span>' + d.price + '</span>' +
                    '</div>';
            });

            let result = '<h1>We found ' + data.length + ' trips!</h1>' + '<h6>' +
                '<span>Destination</span>' +
                '<span>Average temperature</span>' +
                '<span>Price</span>' + '</h6>';

            $scope.myHTML = result + htmlResult;
            
        });

    };


});
