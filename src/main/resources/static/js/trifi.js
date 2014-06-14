var employeeList;

$(document).ready(function() {
    var width = $("#map").width();
    $("#map").height(width * .6985);

    $("#search").autocomplete({
        source: autocompleteEmployees,
        minLength: 1
    });
    $("#search").on("autocompleteselect", function(event, ui) {
        event.preventDefault();
        $("#search").val(ui.item.label);
        updateLastLocation(ui.item.value);
    });

});

function updateLastLocation(id) {
    $.ajax({
        url: "/find/last?id=" + id,
        dataType: "json",
        type: "GET"
    }).done(function(data) {
        console.log(data);
    });
}

function autocompleteEmployees(request, response) {
    if (employeeList == undefined) {
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
        });
    }
    var filteredEmployees = [];
    var termLower = request.term.toLowerCase();
    employeeList.forEach(function(item) {
        if (item.label.toLowerCase().indexOf(termLower) >= 0) {
            filteredEmployees.push(item);
        }
    });
    response(filteredEmployees);

}