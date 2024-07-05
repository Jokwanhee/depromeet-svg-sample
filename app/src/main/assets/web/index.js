window.androidObj = function AndroidClass() {};

var svgBody = document.getElementById('div').innerHTML;

var unclick = '.unclick { pointer-events: none; }'
var unselected = '.unselected { opacity: 0.8; }'

var svg = document.getElementsByTagName('svg')[0];

var inner = svg.getElementsByTagName('style')[0].innerHTML;
var addingValue = inner + unclick + unselected
svg.getElementsByTagName('style')[0].innerHTML = addingValue;

var query = '*[id*=section]';
var redQuery = "*[id^=red]"
var grayQuery = "*[id^=gray]"
var allElements = document.querySelectorAll("*");
var tablePathList = document.querySelectorAll(query);

var allIds = Array.from(allElements)
    .map(el => el.id)
allIds.forEach(id => {
    var element = document.getElementById(id);
    if (element) {
        element.classList.add("unclick");
    }
})

document.addEventListener("click", doSomething);

function doSomething(e) {
    var clickedItem = e.target.id;
    var item;

    for (item = 0; item < tablePathList.length; item++) {
        if (clickedItem === tablePathList[item].id) {
            window.androidObj.textToAndroid(clickedItem);
        }
    }

    e.stopPropagation();
}

function updateFromAndroid(message) {
    console.log(message)

    var allIds = Array.from(allElements)
        .map(el => el.id)
    allIds.forEach(id => {
        var element = document.getElementById(id);
        if (element) {
            element.classList.remove("unclick");
            element.classList.remove("unselected");
        }
    })

    if (message == "red") {
        var redElements = document.querySelectorAll(redQuery);

        // 특정 패턴을 가진 요소들의 ID를 Set으로 저장합니다.
        var redIds = new Set(Array.from(redElements).map(el => el.id));

        // 특정 패턴을 가진 요소들을 제외한 나머지 요소들의 ID를 가져옵니다.
        var excludedIds = new Set(["l_value", "div", "seat", "Rectangle 154185", "room101", "room102"]);
        var otherIds = Array.from(allElements)
            .map(el => el.id)
            .filter(id => id && !redIds.has(id) && !excludedIds.has(id));

        console.log(otherIds)

        otherIds.forEach(id => {
            var element = document.getElementById(id);
            if (element) {
                element.classList.add('unclick');
                element.classList.add('unselected');
            }
        });

    } else if (message == "gray") {
        var grayElements = document.querySelectorAll(grayQuery);

        // 특정 패턴을 가진 요소들의 ID를 Set으로 저장합니다.
        var grayIds = new Set(Array.from(grayElements).map(el => el.id));

        var excludedIds = new Set(["l_value", "div", "seat", "Rectangle 154185", "room103", "room104", "room105", "room106", "room107"]);
        var otherIds = Array.from(allElements)
            .map(el => el.id)
            .filter(id => id && !grayIds.has(id) && !excludedIds.has(id));

        console.log(otherIds)

        otherIds.forEach(id => {
            var element = document.getElementById(id);
            if (element) {
                element.classList.add('unclick');
                element.classList.add('unselected');
            }
        });
    }
    else if (message == "duplicate") {
        allIds.forEach(id => {
            var element = document.getElementById(id);
            if (element) {
                element.classList.add("unclick");
            }
        })
    }
}
