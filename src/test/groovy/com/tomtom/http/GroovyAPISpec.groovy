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

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import spock.lang.Unroll

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.tomtom.http.response.ResponseCode.OK

class GroovyAPISpec extends HttpClientSpec {

    @Unroll
    def 'executes a #name with via full URL'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer')).willReturn(ok()))

        when:
        def response = clientMethod(url: "http://localhost:${mock.port()}/freezer")

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
        def response = clientMethod(path: '/freezer')

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
        def response = clientMethod(path: '/freezer', body: 'ice-cream')

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
        def response = clientMethod(path: '/freezer', body: file)

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
        def response = clientMethod(path: '/freezer', body: [type: 'ice-cream'])

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
                .willReturn(ok()))

        when:
        def response = clientMethod(path: '/freezer', headers: [shelve: 'top'])

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
        def response = clientMethod(path: '/freezer')

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
        def response = clientMethod(path: '/freezer', expecting: Map)

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
        def response = clientMethod(path: '/freezer', expecting: List, of: Map)

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

    @Unroll
    def 'ignores unknown JSON properties by default for #name'() {
        given:
        mock.givenThat(mockMethod(urlEqualTo('/freezer'))
                .willReturn(ok('{"flavor": "vanilla", "chocolate_coated": false}')))

        when:
        def response = clientMethod(path: '/freezer', expecting: IceCream)

        then:
        response.statusCode == OK
        response.body == new IceCream(flavor: 'vanilla')

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

    def 'allows providing custom ObjectMapper and falls back to body as string for #name'() {
        given:
        def mapper = new ObjectMapper()
        def http = new HttpClient(mapper: mapper, baseUrl: "http://localhost:${mock.port()}")
        mock.givenThat(get(urlEqualTo('/freezer'))
                .willReturn(ok('{"flavor": "vanilla", "chocolate_coated": false}')))

        when:
        def response = http.get(path: '/freezer', expecting: IceCream)

        then:
        with(response) {
            statusCode == OK
            body == '{"flavor": "vanilla", "chocolate_coated": false}'
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
        def response = clientMethod(path: '/freezer')

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

}
