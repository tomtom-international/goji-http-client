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
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpHead
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader

class RequestBuilder {

    private ObjectMapper mapper = new ObjectMapper()
    private String baseUrl

    HttpRequestBase request(
            Map properties) {
        def method = properties['method']
        def url = urlFrom properties
        def request = requestFor method, url

        def headers = properties['headers'] as Map
        if (headers) addHeaders request, headers

        def body = properties['body']
        if (body) {
            def serialized = serialize body
            addBody request, serialized
        }

        request
    }

    private urlFrom(
            Map properties) {
        def url = properties['url'] as String
        if (url) return url
        def path = properties['path']
        if (baseUrl && path) return "$baseUrl$path"
        throw new NoUrl()
    }

    private static def addHeaders(
            request,
            Map headers) {
        headers
                .collect { new BasicHeader(it.key as String, it.value as String) }
                .forEach { request.addHeader it }
    }

    private String serialize(body) {
        (body instanceof String) ?
                body : mapper.writeValueAsString(body)
    }

    private static addBody(
            request,
            String body) {
        (request as HttpEntityEnclosingRequestBase)
                .setEntity new StringEntity(body)
    }

    private static HttpRequestBase requestFor(
            method, String url) {
        switch (method) {
            case 'head':
                return new HttpHead(url)
            case 'get':
                return new HttpGet(url)
            case 'post':
                return new HttpPost(url)
            case 'put':
                return new HttpPut(url)
            case 'delete':
                return new HttpDelete(url)
            default:
                throw new UnsupportedOperationException(
                        "$method not supported")
        }
    }

    def getMapper() {
        mapper
    }

    def getBaseUrl() {
        baseUrl
    }

}
