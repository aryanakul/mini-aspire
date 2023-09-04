# mini-aspire

## How to build
Java Version: 17

`./gradlew clean build`

## How to run
`java -jar ./build/libs/mini-0.0.1-SNAPSHOT.jar`

## Authenticate User API
```json
curl --location 'http://localhost:8080/api/v1/users/authenticate' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "admin@mini-aspire.com",
    "password": "admin"
}'
```
