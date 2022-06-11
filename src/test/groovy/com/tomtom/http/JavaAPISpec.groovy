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


import com.github.tomakehurst.wiremock.client.WireMock
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.tomtom.http.response.ResponseCode.OK

class JavaAPISpec extends HttpClientSpec {

    def 'base URL can be #baseUrl.class'() {
        when:
        http = new HttpClient(baseUrl)

        then:
        noExceptionThrown()

        where:
        baseUrl << ["http://localhost:${mock.port()}" as String,
                    "http://localhost:${mock.port()}".toURL(),
                    "http://localhost:${mock.port()}".toURI()]
    }

    @Unroll
    def 'executes a #name with via full URL as String'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer')).willReturn(ok()))

        when:
        def response = clientMethod()
                .url("http://localhost:${mock.port()}/freezer")
                .execute()

        then:
        response.statusCode == OK

        where:
        name      | mockMethod        | clientMethod
        'GET'     | WireMock.&get     | http.&get
        'HEAD'    | WireMock.&head    | http.&head
        'POST'    | WireMock.&post    | http.&post
        'PUT'     | WireMock.&put     | http.&put
        'DELETE'  | WireMock.&delete  | http.&delete
        'TRACE'   | WireMock.&trace   | http.&trace
        'PATCH'   | WireMock.&patch   | http.&patch
        'OPTIONS' | WireMock.&options | http.&options
    }

    @Unroll
    def 'executes a #name with via full URL as URI'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer')).willReturn(ok()))

        when:
        def response = clientMethod()
                .url("http://localhost:${mock.port()}/freezer".toURI())
                .execute()

        then:
        response.statusCode == OK

        where:
        name      | mockMethod        | clientMethod
        'GET'     | WireMock.&get     | http.&get
        'HEAD'    | WireMock.&head    | http.&head
        'POST'    | WireMock.&post    | http.&post
        'PUT'     | WireMock.&put     | http.&put
        'DELETE'  | WireMock.&delete  | http.&delete
        'TRACE'   | WireMock.&trace   | http.&trace
        'PATCH'   | WireMock.&patch   | http.&patch
        'OPTIONS' | WireMock.&options | http.&options
    }

    @Unroll
    def 'executes a #name with via full URL as URL'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer')).willReturn(ok()))

        when:
        def response = clientMethod()
                .url("http://localhost:${mock.port()}/freezer".toURL())
                .execute()

        then:
        response.statusCode == OK

        where:
        name      | mockMethod        | clientMethod
        'GET'     | WireMock.&get     | http.&get
        'HEAD'    | WireMock.&head    | http.&head
        'POST'    | WireMock.&post    | http.&post
        'PUT'     | WireMock.&put     | http.&put
        'DELETE'  | WireMock.&delete  | http.&delete
        'TRACE'   | WireMock.&trace   | http.&trace
        'PATCH'   | WireMock.&patch   | http.&patch
        'OPTIONS' | WireMock.&options | http.&options
    }

    @Unroll
    def 'executes a #name with via relative path'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer')).willReturn(ok()))

        when:
        def response = clientMethod()
                .path('/freezer')
                .execute()

        then:
        response.statusCode == OK

        where:
        name      | mockMethod        | clientMethod
        'GET'     | WireMock.&get     | http.&get
        'HEAD'    | WireMock.&head    | http.&head
        'POST'    | WireMock.&post    | http.&post
        'PUT'     | WireMock.&put     | http.&put
        'DELETE'  | WireMock.&delete  | http.&delete
        'TRACE'   | WireMock.&trace   | http.&trace
        'PATCH'   | WireMock.&patch   | http.&patch
        'OPTIONS' | WireMock.&options | http.&options
    }

    @Unroll
    def 'sends plain text body via #name'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer'))
                .withRequestBody(equalTo('ice-cream'))
                .willReturn(ok()))

        when:
        def response = clientMethod()
                .path('/freezer')
                .body('ice-cream')
                .execute()

        then:
        response.statusCode == OK

        where:
        name    | mockMethod      | clientMethod
        'POST'  | WireMock.&post  | http.&post
        'PUT'   | WireMock.&put   | http.&put
        'PATCH' | WireMock.&patch | http.&patch
    }

    @Unroll
    def 'sends body as file via #name'() {
        given:
        def file = new File('src/test/resources/ice-cream.txt')
        mock.givenThat(mockMethod(urlEqualTo('/freezer'))
                .withMultipartRequestBody(aMultipart().withBody(equalTo(file.text)))
                .willReturn(ok()))

        when:
        def response = clientMethod()
                .path('/freezer')
                .body(file)
                .execute()

        then:
        response.statusCode == OK

        where:
        name    | mockMethod      | clientMethod
        'POST'  | WireMock.&post  | http.&post
        'PUT'   | WireMock.&put   | http.&put
        'PATCH' | WireMock.&patch | http.&patch
    }

    @Unroll
    def 'sends body as JSON via #name'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer'))
                .withRequestBody(equalTo('{"type":"ice-cream"}'))
                .willReturn(ok()))

        when:
        def response = clientMethod()
                .path('/freezer')
                .body([type: 'ice-cream'])
                .execute()

        then:
        response.statusCode == OK

        where:
        name    | mockMethod      | clientMethod
        'POST'  | WireMock.&post  | http.&post
        'PUT'   | WireMock.&put   | http.&put
        'PATCH' | WireMock.&patch | http.&patch
    }

    @Unroll
    def 'sends headers via #name'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer'))
                .withHeader('shelve', equalTo('top'))
                .withHeader('row', equalTo('last'))
                .willReturn(ok()))

        when:
        def response = clientMethod()
                .path('/freezer')
                .header('shelve', 'top')
                .header('row', 'last')
                .execute()

        then:
        response.statusCode == OK

        where:
        name      | mockMethod        | clientMethod
        'GET'     | WireMock.&get     | http.&get
        'HEAD'    | WireMock.&head    | http.&head
        'POST'    | WireMock.&post    | http.&post
        'PUT'     | WireMock.&put     | http.&put
        'DELETE'  | WireMock.&delete  | http.&delete
        'TRACE'   | WireMock.&trace   | http.&trace
        'PATCH'   | WireMock.&patch   | http.&patch
        'OPTIONS' | WireMock.&options | http.&options
    }

    @Unroll
    def 'extracts response body for #name'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer'))
                .willReturn(ok('ice-cream')))

        when:
        def response = clientMethod()
                .path('/freezer')
                .execute()

        then:
        response.statusCode == OK
        response.body == 'ice-cream'

        where:
        name      | mockMethod        | clientMethod
        'GET'     | WireMock.&get     | http.&get
        'POST'    | WireMock.&post    | http.&post
        'PUT'     | WireMock.&put     | http.&put
        'DELETE'  | WireMock.&delete  | http.&delete
        'TRACE'   | WireMock.&trace   | http.&trace
        'PATCH'   | WireMock.&patch   | http.&patch
        'OPTIONS' | WireMock.&options | http.&options
    }

    @Unroll
    def 'parses response body for #name'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer'))
                .willReturn(ok('{"contents": ["ice-cream"]}')))

        when:
        def response = clientMethod()
                .path('/freezer')
                .expecting(Map)
                .execute()

        then:
        response.statusCode == OK
        response.body == [contents: ['ice-cream']]

        where:
        name      | mockMethod        | clientMethod
        'GET'     | WireMock.&get     | http.&get
        'POST'    | WireMock.&post    | http.&post
        'PUT'     | WireMock.&put     | http.&put
        'DELETE'  | WireMock.&delete  | http.&delete
        'TRACE'   | WireMock.&trace   | http.&trace
        'PATCH'   | WireMock.&patch   | http.&patch
        'OPTIONS' | WireMock.&options | http.&options
    }

    @Unroll
    def 'parses response body as generic for #name'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer'))
                .willReturn(ok('[{"type": "ice-cream"}]')))

        when:
        def response = clientMethod()
                .path('/freezer')
                .expecting(List)
                .of(Map)
                .execute()

        then:
        response.statusCode == OK
        response.body == [[type: 'ice-cream']]

        where:
        name      | mockMethod        | clientMethod
        'GET'     | WireMock.&get     | http.&get
        'POST'    | WireMock.&post    | http.&post
        'PUT'     | WireMock.&put     | http.&put
        'DELETE'  | WireMock.&delete  | http.&delete
        'TRACE'   | WireMock.&trace   | http.&trace
        'PATCH'   | WireMock.&patch   | http.&patch
        'OPTIONS' | WireMock.&options | http.&options
    }

    def 'falls back to body as string for #name'() {
        given:
        mock.givenThat(get(urlEqualTo('/freezer'))
                .willReturn(ok('Vanilla-flavored without chocolate coating')))

        when:
        def response = http.get()
                .path('/freezer')
                .expecting(IceCream)
                .execute()

        then:
        with(response) {
            statusCode == OK
            body == 'Vanilla-flavored without chocolate coating'
        }
    }

    @Unroll
    def 'extracts response headers for #name'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer'))
                .willReturn(ok()
                        .withHeader('content', 'ice-cream')
                        .withHeader('content', 'frozen kale')
                        .withHeader('temperature', '-5')))

        when:
        def response = clientMethod()
                .path('/freezer')
                .execute()

        then:
        response.statusCode == OK
        with(response.headers) {
            temperature == ['-5']
            content == ['ice-cream', 'frozen kale']
        }

        where:
        name      | mockMethod        | clientMethod
        'GET'     | WireMock.&get     | http.&get
        'HEAD'    | WireMock.&head    | http.&head
        'POST'    | WireMock.&post    | http.&post
        'PUT'     | WireMock.&put     | http.&put
        'DELETE'  | WireMock.&delete  | http.&delete
        'TRACE'   | WireMock.&trace   | http.&trace
        'PATCH'   | WireMock.&patch   | http.&patch
        'OPTIONS' | WireMock.&options | http.&options
    }

    @Unroll
    def 'executes a #name with query map'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer?foo=bar&foo=baz')).willReturn(ok()))

        when:
        def response = clientMethod()
                .path('/freezer')
                .query('foo', 'bar')
                .query('foo', 'baz')
                .execute()

        then:
        response.statusCode == OK

        where:
        name      | mockMethod        | clientMethod
        'GET'     | WireMock.&get     | http.&get
        'HEAD'    | WireMock.&head    | http.&head
        'POST'    | WireMock.&post    | http.&post
        'PUT'     | WireMock.&put     | http.&put
        'DELETE'  | WireMock.&delete  | http.&delete
        'TRACE'   | WireMock.&trace   | http.&trace
        'PATCH'   | WireMock.&patch   | http.&patch
        'OPTIONS' | WireMock.&options | http.&options
    }

}
