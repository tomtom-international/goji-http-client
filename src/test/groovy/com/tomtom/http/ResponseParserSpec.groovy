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

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import spock.lang.Specification

import static com.tomtom.http.response.ResponseCode.OK

class ResponseParserSpec extends Specification {

    def parser = new ResponseParser()

    def 'Parses status code'() {
        given:
        def response = Mock(HttpResponse) {
            getStatusLine() >> Mock(StatusLine) {
                getStatusCode() >> OK
            }
        }

        when:
        def result = parser.parse response, null, null

        then:
        result.statusCode == OK
    }

    def 'Parses headers'() {
        given:
        def response = Mock(HttpResponse) {
            getAllHeaders() >> [
                    Mock(Header) {
                        getName() >> 'header'
                        getValue() >> 'value'
                    },
                    Mock(Header) {
                        getName() >> 'another header'
                        getValue() >> 'another value'
                    }]
        }

        when:
        def result = parser.parse response, null, null

        then:
        result.headers == [
                header          : ['value'],
                'another header': ['another value']]
    }

    def 'Groups headers'() {
        given:
        def response = Mock(HttpResponse) {
            getAllHeaders() >> [
                    Mock(Header) {
                        getName() >> 'name'
                        getValue() >> 'value'
                    },
                    Mock(Header) {
                        getName() >> 'name'
                        getValue() >> 'value2'
                    }]
        }

        when:
        def result = parser.parse response, null, null

        then:
        result.headers == [name: ['value', 'value2']]
    }

    def 'Parses body'() {
        given:
        def response = Mock(HttpResponse) {
            getEntity() >> Mock(HttpEntity) {
                getContent() >> new ByteArrayInputStream('body'.getBytes())
            }
        }

        when:
        def result = parser.parse response, null, null

        then:
        result.body == 'body'
    }

    def 'Parses json'() {
        given:
        def response = Mock(HttpResponse) {
            getEntity() >> Mock(HttpEntity) {
                getContent() >> new ByteArrayInputStream('{"a": "b"}'.getBytes())
            }
        }

        when:
        def result = parser.parse response, Map, null

        then:
        result.body == [a: 'b']
    }

    def 'Returns string if parsing fails'() {
        given:
        def response = Mock(HttpResponse) {
            getEntity() >> Mock(HttpEntity) {
                getContent() >> new ByteArrayInputStream('body'.getBytes())
            }
        }

        when:
        def result = parser.parse response, Map, null

        then:
        result.body == 'body'
    }

    def 'Parses json of persons'() {
        given:
        def response = Mock(HttpResponse) {
            getEntity() >> Mock(HttpEntity) {
                getContent() >> new ByteArrayInputStream('[{"name": "John Doe"}]'.getBytes())
            }
        }

        when:
        def result = parser.parse response, List, Person

        then:
        result.body == [
                new Person(
                        name: 'John Doe')]
    }

}

@EqualsAndHashCode
@ToString
class Person {
    String name
}