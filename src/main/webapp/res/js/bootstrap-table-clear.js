/**
 * @author zhixin wen <wenzhixin2010@gmail.com>
 * extensions: https://github.com/kayalshri/tableExport.jquery.plugin
 */

(function($) {
    'use strict';

    $.extend($.fn.bootstrapTable.defaults, {
        showClear: false,
        clearUrl: "",
        formatClear: function() {
            return "Clear table"
        }
    });

    var BootstrapTable = $.fn.bootstrapTable.Constructor,
            _initToolbar = BootstrapTable.prototype.initToolbar;

    BootstrapTable.prototype.clear = function(params) {
        if (this.options.clearUrl !== "") {
            var oldUrl = this.options.url;
            var oldMethod = this.options.method;
            var oldData = this.getData();
            console.log("clear ");
            this.options.url = this.options.clearUrl;
            this.options.method = "DELETE";
            this.options.pageNumber = 1;
            var previousFunction = this.options['onLoadError'];
            // didnt find better way to reset view
            // and restore state if an error occurs
            this.initData([]);
            this.options['onLoadError'] = function(args, bsTable) {
                console.log("error on delete");
                this.initData(oldData);
                return false;
            };
            this.initServer(false);
            this.options['onLoadError'] = previousFunction;
            this.options.url = oldUrl;
            this.options.method = oldMethod;
        }
    }

    BootstrapTable.prototype.initToolbar = function() {
        _initToolbar.apply(this, Array.prototype.slice.apply(arguments));

        if (this.options.showClear) {
            var that = this,
                    $btnGroup = this.$toolbar.find('>.btn-group'),
                    $clear = $btnGroup.find('div.clear');

            if (!$clear.length) {
                $clear = $([
                    '<div class="clear btn-group">',
                    '<button class="btn btn-default " ' +
                            ' type="button" name="clear" title="' + this.options.formatClear() + '">',
                    '<i class="glyphicon glyphicon-trash"></i> ',
                    '</button>',
                    '</div>'].join('')).appendTo($btnGroup);
                $clear.find('button[name="clear"]')
                        .off('click').on('click', $.proxy(this.clear, this));
            }
        }
    };
})(jQuery);