package com.tomtom.http

import com.github.tomakehurst.wiremock.WireMockServer
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.tomtom.http.response.ResponseCode.OK

class HttpClientIT extends Specification {

    static service = new WireMockServer()

    def http = new HttpClient()

    def setupSpec() {
        service.start()
    }

    def setup() {
        service.resetAll()
    }

    def 'Executes a get'() {
        given:
        service.givenThat(
                get(urlEqualTo('/path'))
                        .willReturn(
                        aResponse()
                                .withStatus(OK)
                                .withHeader('header', 'value')
                                .withBody('body')))

        when:
        def response = http.get(
                url: "http://localhost:${service.port()}/path")

        then:
        with(response) {
            statusCode == OK
            headers['header'] == ['value']
            body == 'body'
        }
    }

    def 'Executes a get with path and base url'() {
        given:
        service.givenThat(
                get(urlEqualTo('/path'))
                        .willReturn(
                        aResponse()
                                .withStatus(OK)))
        and:
        def http = new HttpClient(
                baseUrl: "http://localhost:${service.port()}")

        when:
        def response = http.get(
                path: '/path')

        then:
        response.statusCode == OK
    }

    def 'Executes a post with a body'() {
        given:
        service.givenThat(
                post(urlEqualTo('/path'))
                        .withRequestBody(equalTo('body'))
                        .willReturn(
                        aResponse()
                                .withStatus(OK)))

        when:
        def response = http.post(
                url: "http://localhost:${service.port()}/path",
                body: 'body')

        then:
        response.statusCode == OK
    }

    def 'Executes a put with a json body'() {
        given:
        service.givenThat(
                put(urlEqualTo('/path'))
                        .withRequestBody(equalTo('{"a":"b"}'))
                        .willReturn(
                        aResponse()
                                .withStatus(OK)))

        when:
        def response = http.put(
                url: "http://localhost:${service.port()}/path",
                body: [a: 'b'])

        then:
        response.statusCode == OK
    }

    def 'Executes a delete with headers'() {
        given:
        service.givenThat(
                delete(urlEqualTo('/path'))
                        .withHeader('header', equalTo('value'))
                        .willReturn(
                        aResponse()
                                .withStatus(OK)))

        when:
        def response = http.delete(
                url: "http://localhost:${service.port()}/path",
                headers: [header: 'value'])

        then:
        response.statusCode == OK
    }

    def 'Parses response'() {
        given:
        service.givenThat(
                get(urlEqualTo('/path'))
                        .willReturn(
                        aResponse()
                                .withStatus(OK)
                                .withBody('{"a": "b"}')))

        when:
        def response = http.get(
                url: "http://localhost:${service.port()}/path",
                expecting: Map)

        then:
        with(response) {
            statusCode == OK
            body == [a: 'b']
        }
    }

    def 'Parses complex response'() {
        given:
        service.givenThat(
                get(urlEqualTo('/path'))
                        .willReturn(
                        aResponse()
                                .withStatus(OK)
                                .withBody('[{"name": "John Doe"}]')))

        when:
        def response = http.get(
                url: "http://localhost:${service.port()}/path",
                expecting: List, of: Person)

        then:
        with(response) {
            statusCode == OK
            body == [
                    new Person(
                            name: 'John Doe')]
        }
    }

    def cleanupSpec() {
        service?.stop()
    }


    def 'Executes an https get'() {
        when:
        def response = http.get(
                url: 'https://httpbin.org/html')

        then:
        response.statusCode == OK
    }

}
