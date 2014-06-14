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

    setInterval("updateLocations()", 1000);

});

function updateLocations() {
    $(".following").each(function() {
        var id = $(this).data("id");
        updateLastLocation(id);
    });
}

function updateLastLocation(id) {
    $.ajax({
        url: "/find/last?id=" + id,
        dataType: "json",
        type: "GET"
    }).done(function(data) {
        $("#map").children("." + id.split('.').join('')).remove();
        var $img = $("<img>", {
            src: "/images/pgradie.png",
            class: id.split('.').join('')
        });
        var right = data.x / 500 * $("#map").width() - 28;
        var top = data.y /350 * $("#map").height() - 26;
        $img.css({
            "position": "absolute",
            "right": right,
            "top": top
        });
        $("#map").append($img);
    });
}

function updateEmployeeList() {
    $.ajax({
        url: "/employee/all",
        dataType: "json",
        async: false,
        type: "GET"
    }).done(function(data) {
        console.log("got employee list");
        employeeList = $.map(data, function(item) {
            return { label: item.name, value: item.id};
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