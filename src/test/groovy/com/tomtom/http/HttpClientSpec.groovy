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
import com.tomtom.http.response.Response
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import spock.lang.Specification

class HttpClientSpec extends Specification {

    def 'Builds with no custom mapper'() {
        expect:
        with(new HttpClient()) {
            builder.mapper
            parser.mapper
        }
    }

    def 'Builds with custom mapper'() {
        given:
        def mapper = new ObjectMapper()

        when:
        def http = new HttpClient(
                mapper: mapper)

        then:
        http.builder.mapper == mapper
        http.parser.mapper == mapper
    }

    @SuppressWarnings('GroovyAssignabilityCheck')
    def 'Executes a get request'() {
        given:
        def request = Mock HttpGet
        def httpResponse = Mock HttpResponse
        def response = Mock Response
        and:
        def builder = Mock RequestBuilder
        def parser = Mock ResponseParser
        def client = Mock org.apache.http.client.HttpClient
        and:
        def http = new HttpClient(
                client: client,
                builder: builder,
                parser: parser)

        when:
        def actual = http.get(
                url: 'url')

        then:
        actual == response
        1 * builder.request([
                url   : 'url',
                method: 'get']
        ) >> request
        1 * client.execute(request) >> httpResponse
        1 * parser.parse(httpResponse, null, null) >> response
        0 * _
    }

    def 'Executes a post request'() {
        given:
        def builder = Mock RequestBuilder
        def client = Mock org.apache.http.client.HttpClient
        def parser = Mock ResponseParser
        and:
        def http = new HttpClient(
                builder: builder,
                client: client,
                parser: parser)

        when:
        http.post(
                url: 'url')

        then:
        1 * builder.request([
                url   : 'url',
                method: 'post'])
    }

    def 'Executes a head request'() {
        given:
        def builder = Mock RequestBuilder
        def client = Mock org.apache.http.client.HttpClient
        def parser = Mock ResponseParser
        and:
        def http = new HttpClient(
                builder: builder,
                client: client,
                parser: parser)

        when:
        http.head(
                url: 'url')

        then:
        1 * builder.request([
                url   : 'url',
                method: 'head'])
    }

    def 'Executes a put request'() {
        given:
        def builder = Mock RequestBuilder
        def client = Mock org.apache.http.client.HttpClient
        def parser = Mock ResponseParser
        and:
        def http = new HttpClient(
                builder: builder,
                client: client,
                parser: parser)

        when:
        http.put(
                url: 'url')

        then:
        1 * builder.request([
                url   : 'url',
                method: 'put'])
    }

    def 'Executes a delete request'() {
        given:
        def builder = Mock RequestBuilder
        def client = Mock org.apache.http.client.HttpClient
        def parser = Mock ResponseParser
        and:
        def http = new HttpClient(
                builder: builder,
                client: client,
                parser: parser)

        when:
        http.delete(
                url: 'url')

        then:
        1 * builder.request([
                url   : 'url',
                method: 'delete'])
    }

    def 'Passes expected value'() {
        given:
        def builder = Mock RequestBuilder
        def client = Mock org.apache.http.client.HttpClient
        def parser = Mock ResponseParser
        and:
        def http = new HttpClient(
                builder: builder,
                client: client,
                parser: parser)

        when:
        http.get(
                url: 'url',
                expecting: Map)

        then:
        1 * parser.parse(null, Map, null)
    }

    def 'Passes of value'() {
        given:
        def builder = Mock RequestBuilder
        def client = Mock org.apache.http.client.HttpClient
        def parser = Mock ResponseParser
        and:
        def http = new HttpClient(
                builder: builder,
                client: client,
                parser: parser)

        when:
        http.get(
                url: 'url',
                expecting: List, of: Map)

        then:
        1 * parser.parse(null, List, Map)
    }

    def 'Allows to set base url'() {
        when:
        def http = new HttpClient(
                baseUrl: 'base')

        then:
        http.builder.baseUrl == 'base'
    }

}
