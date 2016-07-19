var my_app = angular.module('my_app', ['ngRoute']);

my_app.config(function($routeProvider) {

    $routeProvider

        // home page
        .when('/', {
            templateUrl: 'home.html',
            controller: 'mainController'
        })

        // product details page
        .when('/:product_id', {
            templateUrl: 'product_details.html',
            controller: 'productController'
        })

});

my_app.controller('mainController', function($scope, $http) {
    $http.get("http://127.0.0.1:8000/api/products/").then(function(response) {
        $scope.product_data = response.data;
        console.log(response.data);
    });

    $http.get("http://127.0.0.1:8000/api/categories/").then(function(response) {
        $scope.categories = response.data;
        console.log(response.data);
    });

    // Function to check the selected category id for filtering
    $scope.filter_by_category = function (category_id) {
        console.log();
        $scope.filter_category = category_id;
    }
});

my_app.controller('productController', function($scope, $routeParams, $http) {
    $scope.product_id = $routeParams.product_id;
    $http.get("http://127.0.0.1:8000/api/products/" + $scope.product_id).then(function(response) {
        $scope.product = response.data;
        console.log(response.data);
    });
});