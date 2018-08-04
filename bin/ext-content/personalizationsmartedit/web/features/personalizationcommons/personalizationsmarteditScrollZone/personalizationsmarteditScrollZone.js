angular.module('personalizationsmarteditScrollZone', ['yjqueryModule'])
    .controller('personalizationsmarteditScrollZoneController', function($scope, $timeout, $compile, yjQuery) {
        var self = this;

        //Properties
        var scrollZoneTop = true;
        Object.defineProperty(this, 'scrollZoneTop', {
            get: function() {
                return scrollZoneTop;
            },
            set: function(newVal) {
                scrollZoneTop = newVal;
            }
        });

        var scrollZoneBottom = true;
        Object.defineProperty(this, 'scrollZoneBottom', {
            get: function() {
                return scrollZoneBottom;
            },
            set: function(newVal) {
                scrollZoneBottom = newVal;
            }
        });

        var start = false;
        Object.defineProperty(this, 'start', {
            get: function() {
                return start;
            },
            set: function(newVal) {
                start = newVal;
            }
        });

        var elementToScroll = {};
        Object.defineProperty(this, 'elementToScroll', {
            get: function() {
                return elementToScroll;
            },
            set: function(newVal) {
                elementToScroll = newVal;
            }
        });

        var scrollZoneVisible = false;
        Object.defineProperty(this, 'scrollZoneVisible', {
            get: function() {
                return scrollZoneVisible;
            },
            set: function(newVal) {
                scrollZoneVisible = newVal;
            }
        });

        //Methods
        this.stopScroll = function() {
            self.start = false;
        };

        this.scrollTop = function() {
            if (!self.start) {
                return;
            }
            self.scrollZoneTop = self.elementToScroll.scrollTop() <= 2 ? false : true;
            self.scrollZoneBottom = true;

            self.elementToScroll.scrollTop(self.elementToScroll.scrollTop() - 15);
            $timeout(function() {
                self.scrollTop();
            }, 100);
        };

        this.scrollBottom = function() {
            if (!self.start) {
                return;
            }
            self.scrollZoneTop = true;
            var heightVisibleFromTop = self.elementToScroll.get(0).scrollHeight - self.elementToScroll.scrollTop();
            self.scrollZoneBottom = Math.abs(heightVisibleFromTop - self.elementToScroll.outerHeight()) < 2 ? false : true;

            self.elementToScroll.scrollTop(self.elementToScroll.scrollTop() + 15);
            $timeout(function() {
                self.scrollBottom();
            }, 100);
        };

        //Lifecycle methods
        this.$onChanges = function(changes) {
            if (changes.scrollZoneVisible) {
                self.start = changes.scrollZoneVisible.currentValue;
                self.scrollZoneTop = true;
                self.scrollZoneBottom = true;
            }
        };

        this.$onInit = function() {
            var topScrollZone = $compile("<div id=\"sliderTopScrollZone\" data-ng-include=\"'personalizationsmarteditScrollZoneTopTemplate.html'\"></div>")($scope);
            angular.element("body").append(topScrollZone);
            var bottomScrollZone = $compile("<div id=\"sliderBottomScrollZone\" data-ng-include=\"'personalizationsmarteditScrollZoneBottomTemplate.html'\"></div>")($scope);
            angular.element("body").append(bottomScrollZone);
            self.elementToScroll = self.getElementToScroll();
        };

        this.$onDestroy = function() {
            angular.element("#sliderTopScrollZone").scope().$destroy();
            angular.element("#sliderTopScrollZone").remove();
            angular.element("#sliderBottomScrollZone").scope().$destroy();
            angular.element("#sliderBottomScrollZone").remove();
            angular.element("body").contents().each(function() {
                if (this.nodeType === Node.COMMENT_NODE && this.data.indexOf('personalizationsmarteditScrollZone') > -1) {
                    yjQuery(this).remove();
                }
            });
        };
    })
    .component('personalizationsmarteditScrollZone', {
        controller: 'personalizationsmarteditScrollZoneController',
        controllerAs: 'ctrl',
        transclude: true,
        bindings: {
            scrollZoneVisible: '<',
            getElementToScroll: '&',
            isTransparent: '<?'
        }
    });
