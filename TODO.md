
# TODO

## Locale-aware conversion

This is actually container-level stuff, container must provide current locale.
Pass through samson-core from HttpContext as converter method parameter or
bypass samson-core and let converter "components" inject current locale.

## Resource bean/method validation

This cannot be done within samson-core.
It requires an interceptor / resource-method-dispatcher.

## multipart/form-data

# MAYBE

## Binder configuration

Provide some basic bean binder configuration:

* bean access strategy: field vs. property
* do not bind field
* parameter name to bind to

Reuse some basic annotations from JAXB/JSON frameworks.

## Port to RESTEasy

## Use resource method parameter name as default request parameter name
