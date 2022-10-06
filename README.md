# RAMO Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/skeleton-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Related guides


## Provided examples

### RESTEasy JSON serialisation using Jackson

This example demonstrate RESTEasy JSON serialisation by letting you list, add and remove quark types from a list. Quarked!

[Related guide section...](https://quarkus.io/guides/rest-json#creating-your-first-json-rest-service)


## Resources
[Scheduler](https://quarkus.io/guides/quartz)
[Dev UI](http://localhost:32650/q/dev/)

Run Dev Server with override parameters `./mnvw quarkus:dev -Ddebug=6000 -D%dev.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/RAMO -Dquarkus.datasource.username=postgres -Dquarkus.datasource.password=Password -Dquarkus.http.port=32650 -D%dev.quarkus.log.category.\"org.apache.kafka\".level=ERROR -DBASE_URL_USER_MICROSERVICE=http://localhost:10001/api/v1/ -D%dev.kafka.bootstrap.servers=mili-dev.sridata.net:9092 -f pom.xml`

### API Request
```bash
curl --location --request POST 'http://localhost:32650/api/v1/skeleton/orm/listWorkProcess' \
--header 'X-Consumer-Custom-ID: {"userId": -99, "userFullname": "User RAMO"}' \
--header 'Content-Type: application/json' \
--data-raw '{
    "offset": 0,
    "limit": 50,
    "ormTableId": "",
    "workProcessCode": "",
    "workProcessName": "",
}'

## Record list
{
    "status": 200,
    "message": "SUCCESS",
    "payload": {
        "totalRecords": 1,
        "data": [
            {
                "workProcessName": "Process Name",
                "createdFullname": "User RAMO",
                "ormTableId": "fad2f622-fd61-4f83-865f-09b4135fd4ec",
                "updatedFullname": "User RAMO",
                "description": "",
                "createdDatetime": "2022-10-05 11:16:29",
                "workProcessCode": "C01",
                "enabled": true,
                "startDate": 1640970000000,
                "updatedDatetime": "2022-10-05 11:18:40"
            }
        ],
        "totalRecordFiltered": 1
    }
}

curl --location --request POST 'http://localhost:32650/api/v1/skeleton/orm/getWorkProcess' \
--header 'X-Consumer-Custom-ID: {"userId": -99, "userFullname": "User RAMO"}' \
--header 'Content-Type: application/json' \
--data-raw '{
    "ormTableId": "fad2f622-fd61-4f83-865f-09b4135fd4ec"
}'

curl --location --request POST 'http://localhost:32650/api/v1/skeleton/orm/createWorkProcess' \
--header 'X-Consumer-Custom-ID: {"userId": -99, "userFullname": "User RAMO"}' \
--header 'Content-Type: application/json' \
--data-raw '{
    "workProcessCode": "C01",
    "workProcessName": "Process Name",
    "startDate": "2022-01-01"
}'


curl --location --request POST 'http://localhost:32650/api/v1/skeleton/orm/updateWorkProcess' \
--header 'X-Consumer-Custom-ID: {"userId": -99, "userFullname": "User RAMO"}' \
--header 'Content-Type: application/json' \
--data-raw '{
    "ormTableId": "fad2f622-fd61-4f83-865f-09b4135fd4ec",
    "workProcessCode": "C01",
    "workProcessName": "Process Name",
    "startDate": "2022-01-01"
}'

curl --location --request POST 'http://localhost:32650/api/v1/skeleton/orm/deleteWorkProcess' \
--header 'X-Consumer-Custom-ID: {"userId": -99, "userFullname": "User RAMO"}' \
--header 'Content-Type: application/json' \
--data-raw '{
    "ormTableId": "fad2f622-fd61-4f83-865f-09b4135fd4ec"
}'
```
