angular.module('txdemo', ['ngRoute', 'demoService']).config(
    ['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/demo', {
                templateUrl: 'partials/demo.html',
                controller: 'DemoCtrl'
            }).
            otherwise({
                redirectTo: '/demo'
            });
    }]);
