angular.module('personalizationsmarteditDataUtils', [])
    .factory('PaginationHelper', function() {
        var paginationHelper = function(initialData) {
            initialData = initialData || {};

            this.count = initialData.count || 0;
            this.page = initialData.page || 0;
            this.totalCount = initialData.totalCount || 0;
            this.totalPages = initialData.totalPages || 0;

            this.reset = function() {
                this.count = 50;
                this.page = -1;
                this.totalPages = 1;
                this.totalCount = 0;
            };
        };

        return paginationHelper;
    });
