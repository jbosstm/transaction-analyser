angular.module('demoService', ['ngResource']).
    factory('Demo', function ($resource) {
        return $resource('rest/demos/:demoId', {});
    });
