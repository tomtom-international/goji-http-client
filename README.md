# GOJI HTTP Client [![Maven](https://github.com/tomtom-international/goji-http-client/actions/workflows/maven.yml/badge.svg)](https://github.com/tomtom-international/goji-http-client/actions/workflows/maven.yml)

**GOJI** stands for: **Groovy-oriented** and **JSON-implying.** 

## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


## Table of contents

* [Intro](#intro)
* [Changelog](#changelog)
* [Usage](#usage)
* [Request examples](#request-examples)
   * [Supported HTTP methods](#http-methods)
   * [A request to an arbitrary url](#url)
   * [Base url](#base-url)
   * [Query](#query)
   * [Request headers](#request-headers)
     * [Default headers](#default-headers)
   * [Request body](#request-body)
      * [File upload](#file-upload)
* [Handling responses](#responses)
   * [Response status code](#status)
   * [Response headers](#response-headers)
   * [Response body](#response-body)
   * [Deserializing JSON responses to Java objects](#jsons)
   * [Deserializing JSON responses to Java generics](#generics)
* [Logging](#logging)
* [Future work](#todo)
* [Known bugs](#bugs)
* [Contact](#contact)
* [Disclaimer](#disclaimer)
    
## Intro
This client wraps around [Apache HttpClient](https://hc.apache.org/httpcomponents-client-ga/) and [Jackson Databind](https://github.com/FasterXML/jackson-databind) libraries providing lean Groovy syntax:
```groovy
given:
def http = new HttpClient(baseUrl: 'http://ice-cream-service.be')
    
when:
def response = http.post(
    path: '/ice-cream?banana=true',
    headers: ['Content-Type': 'application/json'],
    body: [sprinkles: true],
    expecting: BananaIceCream)
    
then:
response.statusCode == ResponseCode.OK
response.body == new BananaIceCream(sprinkles: true)
```

The library is initially intended for writing easily readable unit-tests but can also but used in other Groovy scripts.

There's also a Java-friendly API available:
```java
class IceCreamTest {
    HttpClient http = new HttpClient("http://ice-cream-service.be");

    @Test
    public void returnsAnIceCream() {
        //when:
        Response<BananaIceCream> response = http
            .post()
            .path("/ice-cream?banana=true")
            .header("Content-Type", "application/json")
            .body(Map.of("sprinkles", true))
            .expecting(BananaIceCream.class);
     
        //then:
        response.getStatusCode() == ResponseCode.OK;
        response.getBody().equals(new BananaIceCream(true));
    }
}
```
Full Java API reference is available [here](doc/JAVA.md)

## Changelog
**[3.3.0](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/3.3.0)**: 
  * (feat) [logging](#logging)
  * (chore) migrate to [org.wiremock](https://central.sonatype.com/artifact/org.wiremock/wiremock) & [httclient5](https://central.sonatype.com/artifact/org.apache.httpcomponents.client5/httpclient5)

**[3.2.0](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/3.2.0)**: (feat) default headers

**[3.1.1](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/3.1.1)**: (security) remove vulnerable dependencies

**[3.1.0](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/3.1.0)**: (feat) query parameter support

**[3.0.0](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/3.0.0)**: (chore) Groovy 4 and other dependency updates

**[2.0.0](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/2.0.0)**: (feature) Java-friendly API

**[1.4.0](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/1.4.0)**: (chore) updated dependencies, including Groovy v2 -> v3 and Jackson (addressing [CVE-2019-17531](https://github.com/advisories/GHSA-gjmw-vf9h-g25v))

**[1.3.1](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/1.3.1)**: (chore) updated dependencies, including jackson-databind version with vulnerabilities

**[1.3.0](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/1.3.0)**: (feat) file upload

**[1.2.3](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/1.2.3)**: (chore) updated dependencies, including jackson-databind version with vulnerabilities

**[1.2.0](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/1.2.0)**: (feature) support for TRACE, OPTIONS and PATCH methods

**[1.1.0](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/1.1.0)**: (doc) maven usage and javadocs

**[1.0.0](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/1.0.0)**: initial release

## Usage

GOJI HTTP uses the [semantic versioning](http://semver.org/) strategy: MAJOR.MINOR.PATCH.

Check [Maven central repository](https://central.sonatype.com/artifact/com.tomtom.http/goji-http-client/3.3.0) for snippets to add the client as a dependency for your build system.  

<a id='requests'></a>
## Request examples

<a id='http-methods'></a>
### Supported HTTP methods

`GET`, `POST`, `PUT` and `DELETE` are supported:
```groovy
http.get()
http.post()
http.put()
http.delete()
http.trace()
http.patch()
http.options()
```

<a id='url'></a>
### A request to an arbitrary url

```groovy
http.get(url: 'http://pizza-delivery.org/margheritas')
```

### Base url

If you want to make a number of requests to a given service, you can specify the `baseUrl` constructor parameter:
```groovy
def http = new HttpClient(baseUrl: 'http://water-melon.com')
    
http.get(path: '/slice')
```

### Query

You can either specify a query in `url` or `path`, or via `query` parameter:

```groovy
http.put(path: '/put?some=query&other=one&other=two')
```

```groovy
http.put(path: '/put', query: [some: 'query', other: ['one', 'two']])
```

> _NB!_ if `url` or `path` contains a query already, `query` parameter is ignored.

### Request headers

```groovy
http.put(
    path: '/put',
    headers: [
        Accept: 'application/json',
        'Content-Type': 'application/json'])
```

#### Default headers

Default headers are sent with each request. It is also possible to override default headers per request: 

```groovy
def http = new HttpClient(defaultHeaders: [
        Accept: 'application/json',
        'Content-Type': 'application/json']) 
http.put(path: '/put', headers: ['Content-Type': 'application/xml'])
```

### Request body

Any non-string body is serialized as JSON.

`String`:
```groovy
http.delete(
    path: '/delete',
    body: '<xml></xml>')
```
`Map`:
```groovy
http.put(
    path: '/put',
    body: [key: 'value']) 
```

<a id='file-upload'></a>
#### Uploading a file

If an instance of `java.io.File` is provided as `body` argument, it will be wrapped into a `MultipartFile`:
```groovy
http.put(
    path: '/post',
    body: '/tmp/input.json' as File) 
``` 

<a id='responses'></a>
## Handling responses

<a id='status'></a>
### Response status code

```groovy
def response = http.get(path: '/get')
    
assert response.statusCode == ResponseCode.OK
```

### Response headers

```groovy
def response = http.get(path: '/get')
    
assert response.headers == [
    'Content-Type': [
        'application/json',
        'application/vnd.tomtom+json'],
    Connection: 'keep-alive'] 
```

### Response body

By default, the response body is a String:
```groovy
Response<String> response = http.get(path: '/get')
    
assert response.body == 'A string'
```

<a id='jsons'></a>
### Deserializing JSON responses to Java objects

A valid JSON response body can be deserialized into a Java object.
```groovy
Response<Map> response = http.get(
    path: '/get',
    expecting: Map)
    
assert response.body == [key: 'value']
```

```groovy
Response<BananaIceCream> response = http.get(
    path: '/ice-cream?banana=true',
    expecting: BananaIceCream)
    
assert response.body instanceof BananaIceCream
```

<a id='generics'></a>
### Deserializing JSON responses to Java generics

```groovy
Response<List<Map>> response = http.get(
    path: '/get',
    expecting: List, of: Map)
    
assert response.body == [
    [key: 'value'],
    ['another-key': 'another value']]
```

See more use-cases in [tests](src/test/groovy)

## Logging

The client uses [log4j v2](https://logging.apache.org/log4j/2.x/), sample output:
```
2023-09-23 14:54:20 INFO POST http://localhost:53611/freezer
2023-09-23 14:54:20 INFO     headers: [shelve: top]
2023-09-23 14:54:20 INFO     body: ice-cream
2023-09-23 14:54:20 INFO => response: 200
2023-09-23 14:54:20 INFO     headers: [Content-Type: application/json]
2023-09-23 14:54:20 INFO     body: {"contents": ["ice-cream"]}
```

Example configuration can be found [here](./src/test/resources/log4j2.xml).

<a id='todo'></a>
## Future work

- Verify HTTPS certificates
- Document URL encoding behavior

<a id='bugs'></a>
## Known bugs

None yet!

<a id='release'></a>
## Creating a release and deploying to Maven Central

Creating a release is usually done from `main`. Before the release:

- Make sure the version is updated.
- Make sure the `Changelog` (above) is updated.

And then deploy it to Maven Central (not auto-released):
```
mvn clean deploy -Prelease
```

And release (when successful):
```
mvn nexus-staging:release
```

## Contact

[artamonov.kirill@gmail.com](mailto:artamonov.kirill@gmail.com)

## Disclaimer

Our primary use-case of this http client is testing our REST services. The client has not been tested for any production use though we don't expect big issues there.
