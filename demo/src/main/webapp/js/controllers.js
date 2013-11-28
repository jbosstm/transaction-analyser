function DemoCtrl($scope, $http, Demo) {
    $scope.refresh = function () {
        $scope.demos = Demo.query();
    };

    $scope.invoke_demo = function ($id) {
        Demo.get({demoId: $id}, function (result) {
            alert(result.msg);
        });
    };

    $scope.refresh();

    $scope.orderBy = 'id';
}
