Configuration

dev
copy src/main/resources/application.example.properties -> src/main/resources/application.properties
copy src/main/resources/liquibase.example.properties -> src/main/resources/liquibase.properties

production
application use environment variables


How to create certificates
`openssl genrsa -out keypair.pem 2048`
`openssl rsa -in keypair.pem -outform DER -pubout -out public.der`
`openssl pkcs8 -topk8 -nocrypt -in keypair.pem -outform DER -out private.der`