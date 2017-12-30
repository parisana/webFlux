Run DemoApplicationServer

Run the Client or
Sample curl commands
$ curl -v 'http://localhost:8080/users'
$ curl -v 'http://localhost:8080/users/1'
$ curl -d '{"name":"nanao", "age":"22"}' -H 'Content-Type:application/json' -v 'http://localhost:8080/users'
