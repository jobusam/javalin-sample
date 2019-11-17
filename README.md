# Secure Javalin App
This application uses Javalin to implement a simple REST endpoint.
Additionally the endpoint use TLS encryption (HTTPS) and user authentication and authorization.

## Setup TLS Support
### Create certificates
```shell script
// Create files in resource folder
$ cd resources

// Create server certificate based on elliptic curve keys with ECDSA
$ keytool -keystore serverkeystore -alias jetty-custom -genkey -keyalg EC -sigalg SHA384withECDSA --dname CN=localhost
    -> Enter keystore password:  jetty-pwd
    -> set correct hostname with CN=[hostname]
	

// Create client truststore with public key of server certificate
$ keytool -export -alias jetty-custom -keystore serverkeystore -rfc -file jetty-custom.cert
$ keytool -importcert -alias jetty-custom -file jetty-custom.cert -keystore clienttruststore
	-> pwd = client-pwd
```
Following steps are optional:
```shell script
// Create client truststore in PKCS#12 format
$ keytool -importkeystore -srckeystore clienttruststore -destkeystore clienttruststore.p12 -srcalias jetty-custom -srcstoretype jks -deststoretype pkcs12
	-> pwd = client-pwd

// Create client truststore in PEM format
$ openssl pkcs12 -in clienttruststore.p12 -out jetty-custom-cert.pem
	-> pwd = client-pwd	
```
Files: 
* `serverkeystore` contains private key und certificate for jetty server
* `jetty-custom.cert`	contains public certificate for java client
* `clienttruststore`	contains public certificate for java client saved in java keystore
* `clienttruststore.p12` contains public certificate in PKCS#12 format
* `jetty-custom-cert.pem` contains public certificate in PEM format for curl and other cli commands

### Create Certificates for Client Authentication (Optional)

```shell script
// Create client certificate based on elliptic curve keys with ECDSA
$ keytool -keystore clientkeystore -alias client-custom -genkey -keyalg EC -sigalg SHA384withECDSA -dname CN=localhost
	-> Enter keystore password:  client-pwd

// Create server truststore with public key of client certificate
$ keytool -export -alias client-custom -keystore clientkeystore -rfc -file client-custom.cert
$ keytool -importcert -alias client-custom -file client-custom.cert -keystore servertruststore
	-> pwd = jetty-pwd

// Create client keystore in PEM format for curl
$ keytool -importkeystore -srckeystore clientkeystore -destkeystore clientkeystore.p12 -srcalias client-custom -srcstoretype jks -deststoretype pkcs12
	-> pwd = client-pwd
$ openssl pkcs12 -in clientkeystore.p12 -out client-custom-cert.pem
	-> pwd = client-pwd
```

### Test TLS
```shell script
// Get TestSSL.sh: 
$ git clone --depth 1 https://github.com/drwetter/testssl.sh.git

// Show local ciphers of openssl
./testssl.sh --local

// Test everything
./testssl.sh https://localhost:8090
  
// Display supported ciphers
./testssl.sh -e https://localhost:8090

// Display supported ciphers and show every tested cypher
./testssl.sh -e --show-each https://localhost:8090
```

## Test the app
With server authentication only:

```shell script
$ curl --cacert resource/jetty-custom-cert https://localhost:8090/users/
```

Use curl with server and client authentication (must be configured in app!):
```shell script
$ curl -v --cert client-custom-cert.pem:client-pwd --cacert jetty-custom-cert.pem https://localhost:8090/users/
```

