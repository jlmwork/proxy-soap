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
        $.ajax($(this).attr('href'))
                .done(function(msg) {
                    console.log("refresh requests");
                    var div = $('<div>');
                    div.html(msg);
                    var content = div.find('#requeststable tbody tr');
                    var previousLength = $('#requeststable tbody tr').length;
                    $('#requeststable tbody').hide().html(content).fadeIn('slow');
                    if (previousLength === 0) {
                        var newLength = $('#requeststable tbody tr').length;
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
        $.ajax($(this).attr('href'))
                .done(function() {
                    console.log("requests cleared");
                    $('#requeststable tbody').fadeOut('slow').html();
                    $("#actionButtons").fadeOut('slow').hide();
                })
                .fail(function() {
                    console.log("error on refresh");
                });
        return false;
    });

    $('#export').click(function() {
        $('#preparing-file-modal').modal('show');

        $.fileDownload($(this).attr('href'), {
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