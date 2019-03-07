/*
 * Copyright (C) 2017. TomTom International BV (http://tomtom.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tomtom.http

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import static com.tomtom.http.response.ResponseCode.OK

class HttpClientIT extends Specification {

    def service = new WireMockServer(wireMockConfig().dynamicPort())
    def http = new HttpClient()
    @Rule
    TemporaryFolder tmp

    def setup() {
        service.start()
    }

    def 'executes a get'() {
        given:
        service.givenThat(
                get(urlEqualTo('/path'))
                        .willReturn(ok()
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

    def 'executes a get with path and base url'() {
        given:
        service.givenThat(
                get(urlEqualTo('/path'))
                        .willReturn(ok()))
        and:
        def http = new HttpClient(baseUrl: "http://localhost:${service.port()}")

        when:
        def response = http.get(path: '/path')

        then:
        response.statusCode == OK
    }

    def 'executes a post with a body'() {
        given:
        service.givenThat(
                post(urlEqualTo('/path'))
                        .withRequestBody(equalTo('body'))
                        .willReturn(ok()))

        when:
        def response = http.post(
                url: "http://localhost:${service.port()}/path",
                body: 'body')

        then:
        response.statusCode == OK
    }

    def 'executes a post with a file body'() {
        given:
        def file = tmp.newFile() << 'foo'
        service.givenThat(
                post(urlEqualTo('/path'))
                        .withMultipartRequestBody(
                        aMultipart().withBody(equalTo('foo')))
                        .willReturn(ok()))

        when:
        def response = http.post(
                url: "http://localhost:${service.port()}/path",
                body: file)

        then:
        response.statusCode == OK
    }

    def 'executes a put with a json body'() {
        given:
        service.givenThat(
                put(urlEqualTo('/path'))
                        .withRequestBody(equalTo('{"a":"b"}'))
                        .willReturn(ok()))

        when:
        def response = http.put(
                url: "http://localhost:${service.port()}/path",
                body: [a: 'b'])

        then:
        response.statusCode == OK
    }

    def 'executes a delete with headers'() {
        given:
        service.givenThat(
                delete(urlEqualTo('/path'))
                        .withHeader('header', equalTo('value'))
                        .willReturn(ok()))

        when:
        def response = http.delete(
                url: "http://localhost:${service.port()}/path",
                headers: [header: 'value'])

        then:
        response.statusCode == OK
    }

    def 'executes options'() {
        given:
        service.givenThat(
                options(urlEqualTo('/path'))
                        .willReturn(ok()))

        when:
        def response = http.options(url: "http://localhost:${service.port()}/path")

        then:
        response.statusCode == OK
    }

    def 'executes a trace'() {
        given:
        service.givenThat(
                trace(urlEqualTo('/path'))
                        .willReturn(ok()))

        when:
        def response = http.trace(url: "http://localhost:${service.port()}/path")

        then:
        response.statusCode == OK
    }

    def 'executes a patch'() {
        given:
        service.givenThat(
                patch(urlEqualTo('/path'))
                        .willReturn(ok()))

        when:
        def response = http.patch(url: "http://localhost:${service.port()}/path")

        then:
        response.statusCode == OK
    }

    def 'parses responses'() {
        given:
        service.givenThat(
                get(urlEqualTo('/path'))
                        .willReturn(ok()
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

    def 'parses generic responses'() {
        given:
        service.givenThat(
                get(urlEqualTo('/path'))
                        .willReturn(ok()
                                .withBody('[{"name": "John Doe"}]')))

        when:
        def response = http.get(
                url: "http://localhost:${service.port()}/path",
                expecting: List, of: Person)

        then:
        with(response) {
            statusCode == OK
            body == [new Person(name: 'John Doe')]
        }
    }

    def cleanup() {
        service?.stop()
    }


    def 'executes an https get'() {
        when:
        def response = http.get(url: 'https://httpbin.org/html')

        then:
        response.statusCode == OK
    }

}
