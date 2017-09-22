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

import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import spock.lang.Specification

class RequestBuilderSpec extends Specification {

    def builder = new RequestBuilder()

    def 'Builds get'() {
        given:
        def properties = [
                method: 'get',
                url   : 'url']

        when:
        def request = builder
                .request properties

        then:
        with(request) {
            method == 'GET'
            getURI() == 'url'.toURI()
        }
    }

    def 'Builds post with a body'() {
        given:
        def properties = [
                method: 'post',
                url   : 'url',
                body  : 'body']

        when:
        def request = builder
                .request properties

        then:
        request.method == 'POST'
        (request as HttpPost).entity.content.text == 'body'
    }

    def 'Builds head'() {
        given:
        def properties = [
                method: 'head',
                url   : 'url']

        when:
        def request = builder
                .request properties

        then:
        request.method == 'HEAD'
    }

    def 'Builds put with a json body'() {
        given:
        def properties = [
                method: 'put',
                url   : 'url',
                body  : [a: 'b']]

        when:
        def request = builder
                .request properties

        then:
        request.method == 'PUT'
        (request as HttpPut).entity.content.text == '{"a":"b"}'
    }

    @SuppressWarnings('GrEqualsBetweenInconvertibleTypes')
    def 'Builds delete with headers'() {
        given:
        def properties = [
                method : 'delete',
                url    : 'url',
                headers: [a: 'b', c: 'd']]

        when:
        def request = builder
                .request properties

        then:
        with(request) {
            method == 'DELETE'
            allHeaders.toList().name == ['a', 'c']
            allHeaders.toList().value == ['b', 'd']
        }
    }

    def 'Specifies url by base url and path'() {
        given:
        def builder = new RequestBuilder(
                baseUrl: 'base')

        when:
        def request = builder.request(
                method: 'get',
                path: '/path')

        then:
        request.getURI() == 'base/path'.toURI()
    }

    def 'Url property is preferred over path'() {
        given:
        def builder = new RequestBuilder(
                baseUrl: 'base')

        when:
        def request = builder.request(
                method: 'get',
                url: 'url',
                path: '/path')

        then:
        request.getURI() == 'url'.toURI()
    }

    def 'Either url or base url and path is required'() {
        when:
        builder.request([:])

        then:
        def e = thrown NoUrl
        e.message == 'Please provide either url param or baseUrl and path parameters.'
    }

}
