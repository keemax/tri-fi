var employeeList;

$(document).ready(function() {

    var width = $("#map").width();
    $("#map").height(width * .6985);

    updateEmployeeList()

    // set up autocomplete on search input
    $("#search").autocomplete({
        source: autocompleteEmployees,
        minLength: 1
    });
    // append employee to list when user selects one
    $("#search").on("autocompleteselect", function(event, ui) {
        event.preventDefault();
        $("#search").val("");
        $("#following").append("<li class=\"following\" data-id=\"" + ui.item.value + "\"><a class=\"remove\">x</a><span>" + ui.item.label +"</span></li>");
    });

    // set up click handler for x's by employee names
    $(document).on("click", ".remove", function() {
        var parentLi = $(this).parent();
        $("#map").children("." + parentLi.data("id").split('.').join('')).remove();
        parentLi.remove();
    });

    setInterval("updateLocations()", 20000);

});

function updateLocations() {
    $(".following").each(function() {
        var id = $(this).data("id");
        updateLastLocation(id);
    });
}

function updateLastLocation(hostname) {
//function updateLastLocation(x,y) {
    var imgWidth = 15;
    var imgHeight = 15;
    /*var data = {"x":x,"y":y};
    var top = data.x / 500 * $("#map").width() - (imgHeight/2);
    var right = data.y /350 * $("#map").height() - (imgWidth/2);
    $("#map").children(".bki").remove();
    var $img = $("<img>", { src: "images/red_dot.png", class: "bki", alt: "alt text"});
    $img.css({ "position": "absolute", "right": right, "top": top});
    $("#map").append($img);*/
    $.ajax({
        url: "/location/last?hostname=" + hostname,
        dataType: "json",
        type: "GET"
    }).done(function(data) {
        console.log( data);
        $("#map").children("." + hostname.split('.').join('')).remove();
        var $img = $("<img>", {
            src: "/images/red_dot.png",
            class: hostname.split('.').join('')
        });
        var top = data.x / 500 * $("#map").width() - (imgHeight/2);
        var right = data.y /350 * $("#map").height() - (imgWidth/2);
        $img.css({
            "position": "absolute",
            "right": right,
            "top": top
        });
        $("#map").css("background-image", "url(/images/floor_"+Math.round(data.floor)+"_grid.png)");
        $("#map").append($img);
    });
}

function updateEmployeeList() {
    //var item = {"name":"Max Keener","id":""};
    /*employeeList = {label:"max keener","value":"12345"};
    $("#search").prop("disabled", false);*/
    $.ajax({
        url: "/employee/all",
        dataType: "json",
        async: false,
        type: "GET"
    }).done(function(data) {
        console.log("got employee list");
        console.log( data);
        employeeList = $.map(data, function(item) {
            return { label: item.realname, value: item.hostname};
        });
        $("#search").prop("disabled", false);
    });
}

function autocompleteEmployees(request, response) {
    var filteredEmployees = [];
    var termLower = request.term.toLowerCase();
    employeeList.forEach(function(item) {
        var inFollowingList = false;
        $(".following").each(function() {
            if ($(this).data("id") == item.value) {
                inFollowingList = true;
            }
        });
        if (item.label.toLowerCase().indexOf(termLower) >= 0 && !inFollowingList) {
            filteredEmployees.push(item);
        }
    });
    response(filteredEmployees);
}