# Overview

Spring WebFlux logging trace id example.

Referring to this repository: https://github.com/archie-swif/webflux-mdc

## Run

```bash
$ ./gradlew bootRun
# Generate random trace-id
$ curl -D - http://127.0.0.1:8080/mdc-trace-example/hello
# Set trace-id
$ curl -H "X-Trace-Id: foo" -D - http://127.0.0.1:8080/mdc-trace-example/hello
# Set Cookie: SESSION=<session-key> header to get saved trace-id 
$ curl -H "Cookie: SESSION=<session-key>" -D - http://127.0.0.1:8080/mdc-trace-example/hello
```

