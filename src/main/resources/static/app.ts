var angmodule = angular.module('demo', ['ngSanitize', 'rzModule', 'daterangepicker']);

angmodule.controller('search', function ($scope, $http) {

    $scope.datePicker = {};
    $scope.datePicker.date = { startDate: null, endDate: null };

    $scope.slider = {
        min: 23,
        max: 27,
        options: {
            floor: 15,
            ceil: 35,
            minRange: 4,
            maxRange: 4,
            pushRange: true,
            onChange: function () {
                var displayElement = document.getElementsByClassName("rangeValues")[0];
                displayElement.innerHTML = "Min: " + $scope.slider.min + " °C" + " Max: " + $scope.slider.max + " °C";
            },
            hidePointerLabels: true,
            hideLimitLabels: true,
            showTicks: 1,

            getTickColor: function (value) {
                if (value == 15)
                    return '#FFE067';
                if (value == 16)
                    return '#FAD764';
                if (value == 17)
                    return '#F5CE62';
                if (value == 18)
                    return '#F0C660';
                if (value == 19)
                    return '#ECBD5E';
                if (value == 20)
                    return '#E7B55C';
                if (value == 21)
                    return '#E2AC5A';
                if (value == 22)
                    return '#DEA358';
                if (value == 23)
                    return '#D99B56';
                if (value == 24)
                    return '#D49254';
                if (value == 25)
                    return '#D08A52';
                if (value == 26)
                    return '#CB8150';
                if (value == 27)
                    return '#C6794E';
                if (value == 28)
                    return '#C2704C';
                if (value == 29)
                    return '#BD674A';
                if (value == 30)
                    return '#B85F48';
                if (value == 31)
                    return '#B45646';
                if (value == 32)
                    return '#AF4E44';
                if (value == 33)
                    return '#AA4542';
                if (value == 34)
                    return '#AA4541';
                if (value == 35)
                    return '#A63D40';
            }
        }
    };

    var displayElement = document.getElementsByClassName("rangeValues")[0];
    displayElement.innerHTML = "Min: " + $scope.slider.min + " °C" + " Max: " + $scope.slider.max + " °C";

    $scope.sendToBackEnd = () => {

        $scope.loader = 'loader';
        let arr = ['Building airplanes...','Dusting off the passport...','Talking to the weather gods...','Grabbing the thermometer...','Checking for rain...'];
        var index = 0;
        var textLoop = setInterval(function() {
            document.getElementById('textloop').innerText = arr[index++];
            if (index == arr.length)
                index = 0

        }, 2000);

        $scope.data.tempMin = $scope.slider.min;
        $scope.data.tempMax = $scope.slider.max;
        $scope.data.startDate = $scope.datePicker.date.startDate.format('YYYY-MM-DD');
        $scope.data.endDate = $scope.datePicker.date.endDate.format('YYYY-MM-DD');

        console.log($scope.data);

        $http.post('search', JSON.stringify($scope.data)).then(function (response) {
           // $http.get('http://rest-service.guides.spring.io/greeting', $scope.data).then(function (response) {

            let data = response.data;
            console.log(data);

// /*            let data = [
//                 {destination: "Stockholm", country: "Muffinland", temperature: 21, price: 1025},
//                 {destination: "Stockholm", country: "Kaninland", temperature: 27, price: 2750},
//                 {destination: "Stockholm", country: "Minland", temperature: 56, price: 2687},
//                 {destination: "Stockholm", country: "Muffinland", temperature: 21, price: 1025},
//                 {destination: "Stockholm", country: "Kaninland", temperature: 27, price: 2750},
//                 {destination: "Stockholm", country: "Minland", temperature: 56, price: 2687},
//                 {destination: "Stockholm", country: "Muffinland", temperature: 21, price: 1025},
//                 {destination: "Stockholm", country: "Kaninland", temperature: 27, price: 2750},
//                 {destination: "Stockholm", country: "Minland", temperature: 56, price: 2687},
//                 {destination: "Stockholm", country: "Minland", temperature: 50, price: 1560}
//             ];*/

            let htmlResult = "";
            data.forEach((d) => {
                htmlResult +=
                    '<div class="resultTable">' +
                    '<div class="resultCity">' + d.destination + ', ' + d.country + '</div>' +
                    '<div class="item resultDurationTo">Duration ' + $scope.data.startDate + ': 4.30h' +
                    '<br>Stops: ' + '1' + '<img src="img/time.png"/>' + '</div>' +
                    '<div class="item resultDurationFrom">Duration ' + $scope.data.startDate + ': 2.10h' +
                    '<br>Stops: ' + '0' + '<img src="img/timefrom.png"/>' + '</div>' +
                    '<div class="item resultSunHours">' + 'Expected sunshine: ' +
                    '<br>7 hours per day' + '<img src="img/sun.png"/></div>' +
                    '<div class="item resultPrice">Price per person: $' + d.price +
                    '<br>Total Price: $' + d.price * $scope.data.noadilts + '<img src="img/price.png"/>' + '</div>' +
                    '<div class="item resultTemp">' + 'Expected temperature: ' +
                    '<br>' + d.temperature + '°C' + '<img src="img/thermometer.png"/>' + '</div>' +
                    '<div class="item resultTempToday">' + 'Temperature today: ' +
                    '<br>24' + '°C' + '<img src="img/thermometer.png"/>' + '</div>' +
                    '<div class="item resultRain">' + 'Expected precipitation:' +
                    '<br>2 mm per day' + '<img src="img/rain.png"/>' + '</div>' +
                    '<div class="item resultBuy">' + '<a class="btn btn-info" role="button">More information</a>' + '</div></div>'

            });

            let result = '<h1>Yay! We found ' + data.length + ' trips from ' + $scope.data.origin + ' between ' + $scope.data.startDate + ' and ' + $scope.data.endDate + '!</h1>';

            let changeSearch = '<div class="changeSearch">' + '<a href=\"#\">Change search</a>' + '</div>';

            $scope.myHTML = result + htmlResult + changeSearch;
            $scope.loader = '';
            clearInterval(textLoop);
            document.getElementById('textloop').innerText = '';

        });
    };


    /* Written by Alex Jiao
     * Usage: fuzzyAutocomplete($('#your-input-element'), ['data1', 'data2']);
     *        fuzzyAutocomplete($('#your-input-element'), '/javascripts/data.json');
     */
    function fuzzyAutocomplete(input, data) {
        var dataList = [];
        // Check if data source is array or string type, otherwise return
        if (Array.isArray(data)) {
            dataList = dataList.concat(data);
        } else if (typeof data === 'string' || data instanceof String) {
            $.ajax({
                url: data,
                type: 'GET',
                success: function(data) {
                    data.forEach(function(item) {
                        dataList.push(item);
                    });
                }
            });
        } else {
            return;
        }
        // Fuzzy matching with regex
        var matchData = function(input, dataList) {
            /*var reg = new RegExp(input.split('').join('\\w*').replace(/\W/, ""), 'i');*/
            if(input.length != 0){
                var reg = new RegExp(('^'+input+'+').replace(/\W/, ""), 'i');
                return dataList.filter(function(data) {
                    if (data.match(reg)) {
                        return data;
                    }

                });
            };
        };
        // Change input value upon keyup
        var changeInput = function(input, dataList) {
            var val = input.val();
            var inputAncestor = input.parent();
            var res = inputAncestor.find('.fuzzy-autocomplete-result');
            while(res.length == 0) {
                inputAncestor = inputAncestor.parent();
                res = inputAncestor.find('.fuzzy-autocomplete-result');
            }
            res.empty().hide();
            var autoCompleteResult = matchData(val, dataList);
            if (val == "" || autoCompleteResult.length == 0) {
                return;
            }
            autoCompleteResult.forEach(function(e) {
                var p = $('<p class="autocomplete" />');
                p.css({
                    'margin': '0px',
                    'padding-left': parseInt(input.css('padding-left'),10) + parseInt(input.css('border-left-width'),10)
                });
                p.text(e);
                p.click(function() {
                    input.val(p.text());
                    $scope.data.origin = p.text();
                    res.empty().hide();
                })
                p.mouseenter(function() {
                    $(this).css("background-color", "#BA9EB0");
                }).mouseleave(function() {
                    $(this).css("background-color", "white");
                });
                res.append(p);
            });
            res.css({
                'left': input.position().left,
                'width': input.width() + parseFloat(input.css('padding-left'),10) + parseInt(input.css('border-left-width'),10) + 1,
                'position': 'absolute',
                'background-color': "white",
                'border': '1px solid #dddddd',
                'max-height': '150px',
                'overflow': 'scroll',
                'overflow-x': 'hidden',
                'font-family': input.css('font-family'),
                'font-size' : input.css('font-size'),
                'z-index' : '10'
            }).insertAfter(input).show();
        };
        // Create a div for collecting the results, and a container for enclosing the input element and result div
        var res = $("<div class='fuzzy-autocomplete-result' />");
        res.insertAfter(input);
        input.keyup(function() {
            changeInput(input, dataList);
        });
        // Hide result div upon clicking anywhere outside the div
        $(document).click(function(event) {
            if (!$(event.target).closest('.fuzzy-autocomplete-result').length) {
                res.empty().hide();
            }
        });
    }

    fuzzyAutocomplete($('#origin'), '/list.json');

});

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

}









