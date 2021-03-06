## Start

The local-shibbo enabled backend can be started by executing:

`./gradlew bootRun -PmainClass=fi.helsinki.opintoni.ApplicationWithLocalShibboProfile`

or by running the class `fi.helsinki.opintoni.ApplicationWithLocalShibboProfile`
from IDE.

The frontend also needs to be started with special options so that it uses https, See the README of the oo frontend
project to figure out how to do that.

## Keys and keystore

In this test configuration both student and teacher use the same key + certificate so only one set is required.

When keys need to be renewed or changed the following procedure can be followed:

```
openssl req -new -x509 -nodes -newkey rsa:4096 -keyout local.student.helsinki.fi-shib-key.pem -days 3650 -subj '/CN=local.student.helsinki.fi' -out local.student.helsinki.fi-shib-cert.pem
openssl pkcs12 -export -in local.student.helsinki.fi-shib-cert.pem -inkey local.student.helsinki.fi-shib-key.pem -name student -out local.student.helsinki.fi-shib.p12
keytool -importkeystore -deststorepass salasana -destkeystore keystore.jks -srckeystore local.student.helsinki.fi-shib.p12 -srcstoretype PKCS12
keytool -importcert -alias idp.domain -file login-test.it.helsinki.fi.pem  -keystore keystore.jks
```

The new local.student.helsinki.fi-shib-cert.pem must be updated in SP Registry (https://sp-registry.it.helsinki.fi)
for the following services: 
 - https://local.student.helsinki.fi
 - https://local.teacher.helsinki.fi:3000