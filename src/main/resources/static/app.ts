var angmodule = angular.module('demo', ['ngSanitize']);


angmodule.controller('search', function ($scope, $http, $interval) {
    $scope.sendToBackEnd = () => {
        $scope.loader = 'loader';
        let arr = ['Digging a hole!','Starting Engines!','Preparing drinks','Feeding the cat','Booking the rooms','Shoveling snow'];
        var index = 0;
        var textLoop = setInterval(function() {
            document.getElementById('textloop').innerText = arr[index++];
            if (index == arr.length)
                index = 0

        }, 2000);
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

        $http.post('search', $scope.data).then(function (response) {
            //$http.get('http://rest-service.guides.spring.io/greeting', $scope.data).then(function (response) {

            let data = response.data;
            console.log(data);

            /*let data = [
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
            ];*/

        let htmlResult = "";
        data.forEach((d) => {
            htmlResult +=
            '<div class="resultDiv">' +
            '<span>' + d.destination + '</span>' +
            '<span>' + d.temperature + '</span>' +
            '<span>' + d.price + '</span>' +
            '<span>' + d.currency + '</span>' +
            '</div>';
    });
        let result = '<h1>Yay! We found ' + data.length + ' trips!</h1>' +
            '<span>Destination</span>' +
            '<span>Average temperature</span>' +
            '<span>Price</span>' +
            '<span>Currency</span>';

        let changeSearch = '<div class="changeSearch">' + '<a href=\"#\">Change search</a>' + '</div>';
        $scope.myHTML = result + htmlResult + changeSearch;
        $scope.loader = '';
        clearInterval(textLoop);
        document.getElementById('textloop').innerText = '';

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
    // if (slide1 > slide2) {
    //     var tmp = slide2;
    //     slide2 = slide1;
    //     slide1 = tmp;
    // }

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
    }
    // start scrolling
    scroll(scrollContainer, scrollContainer.scrollTop, targetY, 0);
}

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







