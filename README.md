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

## Create User API
```json
curl --location 'http://localhost:8080/api/v1/users/createuser' \
--header 'Authorization: eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbWluaS1hc3BpcmUuY29tIiwic3ViIjoiYWRtaW5AbWluaS1hc3BpcmUuY29tIiwiZXhwIjoxNjkzODEyODk2LCJyb2xlIjoiQURNSU4ifQ.LY1K9gUhq7RaZgFwOhV463jKmfCWl6rar9hb4Ni8yA8' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "test@aspire.com",
    "password": "aspire",
    "isAdmin": false
}'
```

## Request a fresh loan
```json
curl --location 'http://localhost:8080/api/v1/loans' \
--header 'Authorization: eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbWluaS1hc3BpcmUuY29tIiwic3ViIjoidGVzdEBhc3BpcmUuY29tIiwiZXhwIjoxNjkzODEzNDIwLCJyb2xlIjoiQkFTSUMifQ.l1wtey7JFJr6HlBXDslRhsR7kRdcp5cUCQuJ7JllLsU' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userEmail": "test@aspire.com",
    "loanAmount": 1000,
    "loanStartDate": "2023-09-03",
    "loanTerm": 3
}'
```

## Find a loan by id
```json
curl --location 'http://localhost:8080/api/v1/loans/1' \
--header 'Authorization: eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbWluaS1hc3BpcmUuY29tIiwic3ViIjoidGVzdEBhc3BpcmUuY29tIiwiZXhwIjoxNjkzODEzNDIwLCJyb2xlIjoiQkFTSUMifQ.l1wtey7JFJr6HlBXDslRhsR7kRdcp5cUCQuJ7JllLsU'
```

## Get all loans of a user
```json
curl --location 'http://localhost:8080/api/v1/loans' \
--header 'Authorization: eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbWluaS1hc3BpcmUuY29tIiwic3ViIjoidGVzdEBhc3BpcmUuY29tIiwiZXhwIjoxNjkzODEzNDIwLCJyb2xlIjoiQkFTSUMifQ.l1wtey7JFJr6HlBXDslRhsR7kRdcp5cUCQuJ7JllLsU'
```

## Get all pending loans for approval (admin only)
```json
curl --location 'http://localhost:8080/api/v1/loans/approve' \
--header 'Authorization: eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbWluaS1hc3BpcmUuY29tIiwic3ViIjoiYWRtaW5AbWluaS1hc3BpcmUuY29tIiwiZXhwIjoxNjkzODEzNjk2LCJyb2xlIjoiQURNSU4ifQ.sfxGelWqNR-Rny60ZVevF1M5JGePL4obeXszgE3e6nY'
```

## Approve a loan (admin only)
```json
curl --location --request PUT 'http://localhost:8080/api/v1/loans/approve/1' \
--header 'Authorization: eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbWluaS1hc3BpcmUuY29tIiwic3ViIjoiYWRtaW5AbWluaS1hc3BpcmUuY29tIiwiZXhwIjoxNjkzODEzNjk2LCJyb2xlIjoiQURNSU4ifQ.sfxGelWqNR-Rny60ZVevF1M5JGePL4obeXszgE3e6nY'
```

## Repay a loan
```json
curl --location 'http://localhost:8080/api/v1/loans/1/repay' \
--header 'Authorization: eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbWluaS1hc3BpcmUuY29tIiwic3ViIjoidGVzdEBhc3BpcmUuY29tIiwiZXhwIjoxNjkzODEzNDIwLCJyb2xlIjoiQkFTSUMifQ.l1wtey7JFJr6HlBXDslRhsR7kRdcp5cUCQuJ7JllLsU' \
--header 'Content-Type: application/json' \
--data '{
    "paymentDate": "2023-09-10",
    "amount": "333.34"
}'
```
