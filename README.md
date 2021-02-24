# WhisperJson

Parse JSON without leaking sensitive data to `String` instances

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.bannmann.whisperjson/whisperjson/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.bannmann.whisperjson/whisperjson)
[![javadoc](https://javadoc.io/badge2/com.github.bannmann.whisperjson/whisperjson/javadoc.svg)](https://javadoc.io/doc/com.github.bannmann.whisperjson/whisperjson)

![Github Actions](https://github.com/bannmann/whisperjson/actions/workflows/build.yml/badge.svg?branch=develop)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.bannmann.whisperjson%3Awhisperjson&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.bannmann.whisperjson%3Awhisperjson)

## Introduction

When dealing with credentials in Java, established practice is to use character arrays instead of `String` instances.
This way, one can wipe their contents so that they don't linger in memory longer than needed.

Unfortunately, for login or signup operations in REST APIs, commonly used JSON parsers pose an obstacle to this: they
always create `String` instances for any JSON string property - even when you use an object mapping facility to map it
to a `char[]` field.

WhisperJson takes a different approach: it only creates objects when necessary.
[Parsing](https://javadoc.io/doc/com.github.bannmann.whisperjson/whisperjson/latest/com/github/bannmann/whisperjson/WhisperJson.html)
an `InputStream`, `Reader` or character array results in a `SafeJson` object which wipes the JSON source text from
memory when it is closed. Additionally, `SafeJson` does not allow access to JSON string properties via `String`
instances, but only via character arrays (which the calling code can wipe after use) or instances of the specialized
[`SensitiveText`](https://javadoc.io/doc/com.github.bannmann.whisperjson/whisperjson/latest/com/github/bannmann/whisperjson/SensitiveText.html)
class (which implements `AutoCloseable`).

For details, see the latest [Javadoc](https://javadoc.io/doc/com.github.bannmann.whisperjson/whisperjson/latest/index.html).

## Acknowledgements

WhisperJson's parser is based on [flatjson](https://github.com/zalando-incubator/flatjson) which pioneered the use of an
"overlay" of indexes to access the underlying JSON source text on demand. Also, the majority of WhisperJson's unit tests
stem from flatjson.
