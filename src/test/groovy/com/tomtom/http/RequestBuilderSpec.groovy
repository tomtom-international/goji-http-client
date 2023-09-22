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


import spock.lang.Specification

class RequestBuilderSpec extends Specification {

    def builder = new RequestBuilder()

    def 'URL property is preferred over path'() {
        given:
        def builder = new RequestBuilder(baseUrl: 'foo')

        when:
        def request = builder.request(
                method: 'get',
                url: 'bar',
                path: '/coverage')

        then:
        request.uri == 'bar'.toURI()
    }

    def 'either url or base url and path is required'() {
        when:
        builder.request([:])

        then:
        def e = thrown NoUrl
        e.message == 'Please provide either url param or baseUrl and path parameters.'
    }

}
