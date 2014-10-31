/*********************************************
 * COOKIE MANAGEMENT
 *********************************************/
var Cookie = {
    set: function(name, value) {
        document.cookie = name + "=" + value + "; max-age=" + (60 * 60 * 24 * 10);
    },
    get: function(name) {
        var cookies = document.cookie.split(';');

        for (var i in cookies) {
            var c = cookies[i].trim().split('=');
            if (c[0] == name)
                return c[1];
        }

        return null;
    }
};

/*********************************************
 * COOKIE MANAGEMENT
 *********************************************/

$(function() {
    var timer = 0;
    $table = $('.fixed-table-container');
    $table.addClass('panel-collapse collapse in');
    $table.attr('role', 'tabpanel');
    $table.attr('aria-labelledby', 'headingOne');

    $('.viewvalidator').click(function() {
        $('#myTab a[href="#validators"]').tab('show');
    });

    $('#export').click(function() {
        $('#preparing-file-modal').modal('show');

        $.fileDownload($(this).attr('data-href'), {
            successCallback: function(url) {
                console.log("download success");
                $('#preparing-file-modal').modal('hide');
            },
            failCallback: function(responseHtml, url) {
                console.log("download failed");
                $('#preparing-file-modal').modal('hide');
                $("#error-modal").modal('show');
            }
        });
        return false; //this is critical to stop the click event which will trigger a normal file download!
    });

    $('.autorefresh')
            .click(function() {
                console.log($(this));
                if ($(this).data('enabled')) {
                    clearTimeout(timer);
                    $(this).data('enabled', false);
                    $('span', this).text('off');
                } else {
                    timer = setTimeout(function() {
                        window.location.reload();
                    }, 4000);
                    $(this).data('enabled', true);
                    $('span', this).text('on');
                }

                Cookie.set('autorefresh', $(this).data('enabled'));
            })
            .data('enabled', Cookie.get('autorefresh') != "true")
            .click();
}
);

function rowStyle(row, index) {
    if (row.request_valid === "true" && row.response_valid === "true") {
        return {classes: 'success'};
    } else if ((row.request_valid !== "true" || row.response_valid !== "true") && row.validator !== "") {
        return {classes: 'danger'};
    } else if (row.request_xml_valid !== "true" || row.response_xml_valid !== "true") {
        return {classes: 'danger'};
    }
    return {classes: 'warning'};
}


