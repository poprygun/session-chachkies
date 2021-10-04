# Spring Boot with DB Sessions

## H2 Console can be accessed [here](http://localhost:8080/h2-console)

## Command to store and retrieve data in Session Database

```bash
curl --location --request POST 'http://localhost:8080/save' \
--header 'Content-Type: application/json' \
--header 'Cookie: SESSION=YjJkZDk5ODUtMjJmYy00N2EwLTg3YWMtMTQxYjBjODcxZTVh' \
--data-raw '{"id":"b921f1dd-3cbc-0495-fdab-8cd14d33f0aa","latitude":0.7231742029971469,"longitude":0.9908988967772393}'
```