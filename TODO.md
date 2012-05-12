
# TODO

## Locale-aware conversion

This is actually container-level stuff, container must provide current locale.
Pass through samson-core from HttpContext as converter method parameter or
bypass samson-core and let converter "components" inject current locale.

## Resource bean/method validation

This needs the validator to support extensions for custom wrapper types (e.g. Optional&lt;T&gt;, Form&lt;T&gt;),
see [HV-565](https://hibernate.onjira.com/browse/HV-565).

## Use Jackson for data-binding

Replace Binder with ObjectMapper/Deser and use FormNode extends JsonNode for the graph-of-nodes overlay.

# MAYBE

## Binder configuration (moot if binding is replaced with jackson-databind)

Provide some basic bean binder configuration:

* bean access strategy: field vs. property
* do not bind field
* parameter name to bind to

Reuse some basic annotations from JAXB/JSON frameworks.

## Port to RESTEasy

## Use resource method parameter name as default request parameter name
