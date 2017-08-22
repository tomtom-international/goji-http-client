# GOJI HTTP Client

**GOJI** stands for: **Groovy-oriented** and **JSON-implying.**

This clients wraps around [Apache httpclient](https://hc.apache.org/httpcomponents-client-ga/) and [Jackson 'databind'](https://github.com/FasterXML/jackson-databind) libraries with lean Groovy syntax:
```groovy
given:
def http = new HttpClient(
    url: 'http://localhost')
    
when:
def response = http.post(
    path: '/ice-cream?banana=true',
    headers: [
        'Content-Type': 'application/json'],
    body: [
        sprinkles: true],
    expecting: BananaIceCream)
    
then:
response.statusCode == ResponseCode.OK
response.body == new BananaIceCream(
    sprinkles: true)
```

See more use-cases in [integration tests](src/integration-test/groovy)

_**Disclaimer:**_ Our primary use-case of this http client is testing our REST services. The client has not been tested for any production use. The client has no verification of https certificates et al.