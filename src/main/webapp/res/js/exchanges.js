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

    $('#refresh').click(function() {
        $.ajax($(this).attr('data-href'))
                .done(function(msg) {
                    console.log("refresh exchanges");
                    var div = $('<div>');
                    div.html(msg);
                    var content = div.find('#exchangestable tbody tr');
                    var previousLength = $('#exchangestable tbody tr').length;
                    var table = $('#exchangestable tbody').hide().html(content);
                    destroyTable();
                    table.fadeIn('slow');
                    transformTable();
                    if (previousLength === 0) {
                        var newLength = $('#exchangestable tbody tr').length;
                        if (newLength > 0) {
                            $("#actionButtons").fadeIn('slow').show();
                        }
                    }
                    // reload highlighting
                    $('pre code').each(function(i, block) {
                        hljs.highlightBlock(block);
                    });
                    // TODO : reload links to validators tab
                })
                .fail(function() {
                    console.log("error on refresh");
                });
        return false;
    });

    $('.viewvalidator').click(function() {
        $('#myTab a[href="#validators"]').tab('show');
    });

    $('#clear').click(function() {
        $.ajax($(this).attr('data-href'))
                .done(function() {
                    console.log("exchanges cleared");
                    $('#exchangestable tbody').fadeOut('slow').html();
                    $("#custom-toolbar").fadeIn('slow').hide();
                })
                .fail(function() {
                    console.log("error on refresh");
                });
        return false;
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
});
/*
 function destroyTable() {
 console.log("destroyTable table");
 var table = $('#exchangestable');
 table.bootstrapTable('destroy');
 }
 function transformTable() {
 console.log("transform table");
 var table = $('#exchangestable');
 var classes = 'table table-bordered table-striped table-hover table-condensed';
 table.bootstrapTable({
 classes: classes,
 sortName: "date", sortOrder: "asc", sortable: true,
 search: true,
 showColumns: true,
 pagination: true
 });
 }

 $(function() {
 transformTable();
 });*/

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

$(function() {
    $btn = $('button[name="refresh"]');
    $btn.click(function() {
        var trs = $.find('#exchangestable tbody tr');
        console.log(trs.length);
        if (trs.length > 1)
            $("#custom-toolbar").fadeIn('slow').show();
        else if (trs.length === 1) {
            var tds = trs.find("td");
            console.log(tds);
        }
        else
            $("#custom-toolbar").fadeIn('slow').hide();
    });

});

