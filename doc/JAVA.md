[<<back](../README.md)

# Java API

## Request examples

### Supported HTTP methods:

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

### A request to an arbitrary url:

```groovy
http.get().url("http://pizza-delivery.org/margheritas").execute();
```

### Base url:

If you want to make a number of requests to a given service, you can specify the `baseUrl` constructor parameter:
```groovy
HttpClient http = new HttpClient("http://water-melon.com");
    
http.get().path("/slice").execute();
```

### Request headers:

```groovy
http.put()
    .path("/put")
    .header("Accept", "application/json")
    .header("Content-Type", "application/json")
    .execute();
```

### Request body:

Any non-string body is serialized as JSON.

`String`:
```groovy
http.delete()
    .path("/delete")
    .body("<xml></xml>")
    .execute();
```
`Map`:
```groovy
http.put()
    .path("/put")
    .body(Map.of("key", "value"))
    .execute() 
```

#### Uploading a file

If an instance of `java.io.File` is provided as `body` argument, it will be wrapped into a `MultipartFile`:
```groovy
http.put()
    .path("/post")
    .body(new File("/tmp/input.json"))
    .execute(); 
``` 

## Handling responses

### Response status code

```groovy
Response response = http.get().path("/get").execute();
    
assert response.statusCode() == ResponseCode.OK;
```

### Response headers

```groovy
Response response = http.get().path("/get").execute();
    
assert response.headers().equals(Map.of(
    "Content-Type", List.of("application/json", "application/vnd.tomtom+json"),
    "Connection", "keep-alive"));
```

### Response body

By default, the response body is a String:
```groovy
Response<String> response = http.get().path("/get").execute();
    
assert response.body().equals("A string");
```

### Deserializing JSON responses to Java objects

A valid JSON response body can be deserialized into a Java object.
```groovy
Response<Map> response = http.get().path("/get").expecting(Map.class).execute();

assert response.body().equals(Map.of("key", "value"));
```

```groovy
Response<BananaIceCream> response = http.get()
    .path("/ice-cream?banana=true")
    .expecting(BananaIceCream.class);
    
assert response.body() instanceof BananaIceCream
```

### Deserializing JSON responses to Java generics

```groovy
Response<List<Map>> response = http.get().path("/get")
    .expecting(List.class).of(Map.class);
    
assert response.body().equals(List.of(
    Map.of("key", "value"),
    Map.of("another-key", "another value")));
```

[<<back](../README.md)