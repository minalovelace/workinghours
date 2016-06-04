'use strict';

/* Services */

var services = angular.module('services', ['ngResource']);

services.factory('UserFactory', function ($resource) {
    return $resource('/users', {}, {
        query: {
            method: 'GET',
            params: {},
            isArray: false
        }
    })
});
