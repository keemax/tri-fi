var employeeList;

$(document).ready(function() {

    var width = $("#map").width();
    $("#map").height(width * .6985);

    //preload images
    IMAGES = {"smallLoc": new Image(), "bigLoc": new Image()};
    IMAGES["smallLoc"].src="/images/red_dot.png";
    IMAGES["bigLoc"].src="/images/red_circle.png";

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

    setInterval("updateLocations()", 5000);

});

function updateLocations() {
    $(".following").each(function() {
        var id = $(this).data("id");
        updateLastLocation(id);
    });
}

function displayLocation(id, x, y)
{
    var bigOpacity = .1;
    var smallOpacity = .7;
    var smallImg = IMAGES["smallLoc"];
    var bigImg = IMAGES["bigLoc"];

    //$("#map").children("." + id).remove();
    var smallTop = Math.round(x / 500 * $("#map").width() - (smallImg.height/2));
    var smallRight = Math.round(y / 350 * $("#map").height() - (smallImg.width/2));
    var bigTop = Math.round(x / 500 * $("#map").width() - (bigImg.height/2));
    var bigRight = Math.round(y / 350 * $("#map").height() - (bigImg.width/2));

    //check to see if we are already displaying
    if( $("#"+id+"SMALL").length>0 ) {
        //if we haven't moved, don't do anything
        if( smallTop != Math.round($("#"+id+"SMALL").position().top)
               || ($("#map").width()-smallRight-smallImg.width) != Math.round($("#"+id+"SMALL").position().left)) {
            $("#"+id+"BIG").fadeTo(0, 0);
            $("#"+id+"SMALL").animate({
                    "right": smallRight,
                    "top": smallTop
                }, 1000, function(){
                    $("#"+id+"BIG").css({"right":bigRight,"top":bigTop});
                    $("#"+id+"BIG").fadeTo( 1000, bigOpacity);
                }
            );
        }
    } else {
        //inner circle
        var $imgSmall = $("<img>", {
            src: "/images/red_dot.png",
            class: id,
            id: id+"SMALL",
        });
        $imgSmall.css({
            "position": "absolute",
            "right": smallRight,
            "top": smallTop,
            "opacity": smallOpacity
        });
        $("#map").append($imgSmall);

        //outer circle
        var $imgBig = $("<img>", {
            src: "/images/red_circle.png",
            class: id,
            id: id+"BIG"
        });
        $imgBig.css({
            "position": "absolute",
            "right": bigRight,
            "top": bigTop,
            "opacity": bigOpacity
        });
        $("#map").append($imgBig);
    }
}

function updateLastLocation(hostname) {
    var imgWidth = 15;
    var imgHeight = 15;

    $.ajax({
        url: "/location/last?hostname=" + hostname,
        dataType: "json",
        type: "GET"
    }).done(function(data) {
        console.log(data);
        displayLocation(hostname.split('.').join(''), data.x, data.y);
        $("#map").css("background-image", "url(/images/floor_"+Math.round(data.floor)+"_grid.png)");
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