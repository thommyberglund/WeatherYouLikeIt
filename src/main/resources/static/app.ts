var angmodule = angular.module('demo', ['ngSanitize', 'rzModule', 'daterangepicker']);

angmodule.controller('search', function ($scope, $http) {

    $scope.datePicker = {};
    $scope.datePicker.date = {startDate: null, endDate: null};

    $scope.slider= {
        min: 23,
        max: 27,
        options: {
            floor: 15,
            ceil: 35,
            minRange: 4,
            maxRange: 4,
            showSelectionBar: true,
            selectionBarGradient: {
                from: '#FFE067',
                to: '#c9241f'
            },
            pushRange: true,
            onChange: function () {
                             var displayElement = document.getElementsByClassName("rangeValues")[0];
                             displayElement.innerHTML = "Min: " + $scope.slider.min + " °C" + " Max: " + $scope.slider.max + " °C";
                         },
            hidePointerLabels: true,
            hideLimitLabels: true,
        }
    };

    // $scope.slider = {
    //     min: 23,
    //     max: 27,
    //     options: {
    //         floor: 15,
    //         ceil: 35,
    //         minRange: 4,
    //         maxRange: 4,
    //         showselectioBar: true,
    //         selectionBarGradient: {
    //             from: '#FFE067',
    //             to: '#A63D40'
    //         },
    //         pushRange: true,
    //         onChange: function () {
    //             var displayElement = document.getElementsByClassName("rangeValues")[0];
    //             displayElement.innerHTML = "Min: " + $scope.slider.min + " °C" + " Max: " + $scope.slider.max + " °C";
    //         },
    //         hidePointerLabels: true,
    //         hideLimitLabels: true,
    //         showTicks: 1,
    //
    //         getTickColor: function (value) {
    //             if (value == 15)
    //                 return '#FFE067';
    //             if (value == 16)
    //                 return '#FAD764';
    //             if (value == 17)
    //                 return '#F5CE62';
    //             if (value == 18)
    //                 return '#F0C660';
    //             if (value == 19)
    //                 return '#ECBD5E';
    //             if (value == 20)
    //                 return '#E7B55C';
    //             if (value == 21)
    //                 return '#E2AC5A';
    //             if (value == 22)
    //                 return '#DEA358';
    //             if (value == 23)
    //                 return '#D99B56';
    //             if (value == 24)
    //                 return '#D49254';
    //             if (value == 25)
    //                 return '#D08A52';
    //             if (value == 26)
    //                 return '#CB8150';
    //             if (value == 27)
    //                 return '#C6794E';
    //             if (value == 28)
    //                 return '#C2704C';
    //             if (value == 29)
    //                 return '#BD674A';
    //             if (value == 30)
    //                 return '#B85F48';
    //             if (value == 31)
    //                 return '#B45646';
    //             if (value == 32)
    //                 return '#AF4E44';
    //             if (value == 33)
    //                 return '#AA4542';
    //             if (value == 34)
    //                 return '#AA4541';
    //             if (value == 35)
    //                 return '#A63D40';
    //         }
    //     }
    // };

    var displayElement = document.getElementsByClassName("rangeValues")[0];
    displayElement.innerHTML = "Min: " + $scope.slider.min + " °C" + " Max: " + $scope.slider.max + " °C";

    $scope.sendToBackEnd = () => {

       if (document.getElementById("HTMLresult") != null) {
           document.getElementById("HTMLresult").innerText = "";
       }

        $scope.loader = 'loader';
        document.getElementById('textloop').innerText = 'Thanking you all for this time...';
        let arr = ['Waving goodbye to Senior...', 'Thinking about Juniors cats...', ' the banana cake...', 'Saying farewell to Academy...', 'Loving you all...', 'Planning for reunion...', 'Cleaning the coffee machine one last time...', 'Beating C# in ping pong...', 'These lines will be changed after the presentation, no worries... :)'];
            // 'Bargaining with the airlines...', 'Building airplanes...', 'Dusting off the passport...', 'Talking to the weather gods...', 'Grabbing the thermometer...', 'Turning off the rain...', 'Hiring pilots...', 'Polishing the runway...', 'Rehearsing safety instructions...', 'Heating the tea water...' ];
        var index = 0;
        var textLoop = setInterval(function () {
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
        //$http.get('http://rest-service.guides.spring.io/greeting', $scope.data).then(function (response) {

            let data = response.data;
            console.log(data);
            //
            // let data = [
            //     {destination: "Stockholm", country: "Muffinland", temperature: 21, price: 1025},
            //     {destination: "Stockholm", country: "Kaninland", temperature: 27, price: 2750},
            //     {destination: "Stockholm", country: "Minland", temperature: 56, price: 2687},
            //     {destination: "Stockholm", country: "Muffinland", temperature: 21, price: 1025},
            //     {destination: "Stockholm", country: "Kaninland", temperature: 27, price: 2750},
            //     {destination: "Stockholm", country: "Minland", temperature: 56, price: 2687},
            //     {destination: "Stockholm", country: "Muffinland", temperature: 21, price: 1025},
            //     {destination: "Stockholm", country: "Kaninland", temperature: 27, price: 2750},
            //     {destination: "Stockholm", country: "Minland", temperature: 56, price: 2687},
            //     {destination: "Stockholm", country: "Minland", temperature: 50, price: 1560}
            // ];

            let result = "";
            let htmlResult = "";

            if (Object.keys(data).length === 0) {
                result = '<h1>Oh no! We found no trips from ' + $scope.data.origin + '! Try changing your budget.</h1>';
            } else {

                data.forEach((d) => {
                    htmlResult +=
                        '<div class="resultTable">' +
                        '<div class="resultCity">' + d.destination + ', ' + d.country + '<img src="' + d.flagUrl + '"/>' + '</div>' +
                        '<div class="item resultDurationTo">Outbound: ' + d.outboundDepartureDate + ' ' + d.outboundDepartureTime +
                        '<br>Stops: ' + d.outboundStops + '<img src="img/outbound.png"/>' + '</div>' +
                        '<div class="item resultDurationFrom">Return: ' + d.inboundDepartureDate + ' ' + d.inboundDepartureTime +
                        '<br>Stops: ' + d.inboundStops + '<img src="img/return.png"/>' + '</div>' +
                        '<div class="item resultPrice">Price per person: €' + Math.round(d.pricePerPerson) +
                        '<br>Total Price: €' + Math.round(d.priceTotal) + '<img src="img/euro.png"/>' + '</div>' +
                        '<div class="item resultTemp">' + 'Expected temperature: ' +
                        '<br>' + Math.round(d.temperature) + '°C' + '<img src="img/thermometer.png"/>' + '</div>' +
                        '<div class="item resultTempToday">' + 'Temperature today: ' +
                        '<br>' + Math.round(d.temperatureToday) + '°C' + '<img src="img/thermometer.png"/>' + '</div>' +
                        '<div class="item resultRain">' + 'Expected precipitation:' +
                        '<br>' + Math.round(d.precipitation * 10) / 10 + ' mm per day' + '<img src="img/rain.png"/>' + '</div>' +
                        '<div class="item resultBuy">' + '<a class="btn btn-info" role="button">More information</a>' + '</div></div>'
                });

                result = '<h1>Yay! We found ' + data.length + ' trips from ' + $scope.data.origin + '!</h1>';
            }

            $scope.myHTML = result + htmlResult;
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
                success: function (data) {
                    data.forEach(function (item) {
                        dataList.push(item);
                    });
                }
            });
        } else {
            return;
        }
        // Fuzzy matching with regex
        var matchData = function (input, dataList) {
            /*var reg = new RegExp(input.split('').join('\\w*').replace(/\W/, ""), 'i');*/
            if (input.length != 0) {
                var reg = new RegExp(('^' + input + '+').replace(/\W/, ""), 'i');
                return dataList.filter(function (data) {
                    if (data.match(reg)) {
                        return data;
                    }

                });
            }
            ;
        };
        // Change input value upon keyup
        var changeInput = function (input, dataList) {
            var val = input.val();
            var inputAncestor = input.parent();
            var res = inputAncestor.find('.fuzzy-autocomplete-result');
            while (res.length == 0) {
                inputAncestor = inputAncestor.parent();
                res = inputAncestor.find('.fuzzy-autocomplete-result');
            }
            res.empty().hide();
            var autoCompleteResult = matchData(val, dataList);
            if (val == "" || autoCompleteResult.length == 0) {
                return;
            }
            autoCompleteResult.forEach(function (e) {
                var p = $('<p class="autocomplete" />');
                p.css({
                    'margin': '0px',
                    'padding-left': parseInt(input.css('padding-left'), 10) + parseInt(input.css('border-left-width'), 10)
                });
                p.text(e);
                p.click(function () {
                    input.val(p.text());
                    $scope.data.origin = p.text();
                    res.empty().hide();
                })
                p.mouseenter(function () {
                    $(this).css("background-color", "#999");
                }).mouseleave(function () {
                    $(this).css("background-color", "white");
                });
                res.append(p);
            });
            res.css({
                'left': input.position().left,
                'width': input.width() + parseFloat(input.css('padding-left'), 10) + parseInt(input.css('border-left-width'), 10) + 1,
                'position': 'absolute',
                'background-color': "white",
                'border': '1px solid #dddddd',
                'max-height': '150px',
                'overflow': 'scroll',
                'overflow-x': 'hidden',
                'font-family': input.css('font-family'),
                'font-size': input.css('font-size'),
                'z-index': '10'
            }).insertAfter(input).show();
        };
        // Create a div for collecting the results, and a container for enclosing the input element and result div
        var res = $("<div class='fuzzy-autocomplete-result' />");
        res.insertAfter(input);
        input.keyup(function () {
            changeInput(input, dataList);
        });
        // Hide result div upon clicking anywhere outside the div
        $(document).click(function (event) {
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









