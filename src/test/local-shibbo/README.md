## generate required keys and keystore:

```
keytool -dname cn=local.student.helsinki.fi -genkeypair -alias student  -keystore keystore.jks
keytool -importcert -alias idp.signing -file sign-login.helsinki.fi.crt  -keystore keystore.jks
keytool -importcert -alias idp.domain -file login-test.it.helsinki.fi.cert  -keystore keystore.jks
```

## start
`CONFIG_DIR=src/test/local-shibbo SPRING_PROFILES_ACTIVE=local-shibbo ./gradlew bootRun`

