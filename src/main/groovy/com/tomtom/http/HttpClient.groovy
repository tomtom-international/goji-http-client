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
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.ssl.SSLContexts

import java.security.cert.CertificateException
import java.security.cert.X509Certificate

class HttpClient {

    private final context = SSLContexts
            .custom()
            .loadTrustMaterial(null, new TrustStrategy() {
        @Override
        boolean isTrusted(
                X509Certificate[] chain,
                String authType) throws CertificateException {
            true
        }
    }).build()
    private final org.apache.http.client.HttpClient client

    final RequestBuilder builder
    final ResponseParser parser

    HttpClient() {
        this([:])
    }

    /**
     * Builds {@link HttpClient} with a number of (optional) parameters.
     * @param baseUrl a url prefix for request {@code path} parameter
     * @param client a custom {@link org.apache.http.client.HttpClient}
     * @param mapper a custom {@link ObjectMapper}
     */
    HttpClient(
            Map properties) {
        client = properties['client'] as org.apache.http.client.HttpClient ?:
                defaultClient()

        def mapper = properties['mapper'] as ObjectMapper ?:
                new ObjectMapper()
        def builderParams = [mapper: mapper]
        def baseUrl = properties['baseUrl'] as String
        if (baseUrl) builderParams += [baseUrl: baseUrl]

        builder = properties['builder'] as RequestBuilder ?:
                new RequestBuilder(builderParams)
        parser = properties['parser'] as ResponseParser ?:
                new ResponseParser(mapper: mapper)
    }

    private defaultClient() {
        HttpClientBuilder
                .create()
                .setSSLContext(context)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .disableAuthCaching()
                .disableAutomaticRetries()
                .disableCookieManagement()
                .disableRedirectHandling()
                .build()
    }

    /**
     * Possible properties:<br/>
     * &emsp;<b>path</b> - a request path (appended to a base url defined at {@link #HttpClient(Map)} constructor)<br/>
     * &emsp;<b>url</b> - a full request url (overrides path and base url)<br/>
     * &emsp;<b>headers</b> as {@link Map}<br/>
     * &emsp;<b>body</b> - a request body. Anything except {@link String} is serialized to a <a href="https://en.wikipedia.org/wiki/JSON">json</a>.<br/>
     * &emsp;<b>expecting</b> - a class to deserialize response body to. If not specified, response body is a {@link String}<br/>
     * &emsp;<b>of</b> - a subclass to deserialize response body to. Use to deserialize generic responses like {@link Collection}<{@link Map}>.
     */
    Response head(
            Map properties) {
        def all = properties +
                [method: 'head']
        performRequest all
    }

    /**
     * Possible properties:<br/>
     * &emsp;<b>path</b> - a request path (appended to a base url defined at {@link #HttpClient(Map)} constructor)<br/>
     * &emsp;<b>url</b> - a full request url (overrides path and base url)<br/>
     * &emsp;<b>headers</b> as {@link Map}<br/>
     * &emsp;<b>body</b> - a request body. Anything except {@link String} is serialized to a <a href="https://en.wikipedia.org/wiki/JSON">json</a>.<br/>
     * &emsp;<b>expecting</b> - a class to deserialize response body to. If not specified, response body is a {@link String}<br/>
     * &emsp;<b>of</b> - a subclass to deserialize response body to. Use to deserialize generic responses like {@link Collection}<{@link Map}>.
     */
    Response get(
            Map properties) {
        def all = properties +
                [method: 'get']
        performRequest all
    }

    /**
     * Possible properties:<br/>
     * &emsp;<b>path</b> - a request path (appended to a base url defined at {@link #HttpClient(Map)} constructor)<br/>
     * &emsp;<b>url</b> - a full request url (overrides path and base url)<br/>
     * &emsp;<b>headers</b> as {@link Map}<br/>
     * &emsp;<b>body</b> - a request body. Anything except {@link String} is serialized to a <a href="https://en.wikipedia.org/wiki/JSON">json</a>.<br/>
     * &emsp;<b>expecting</b> - a class to deserialize response body to. If not specified, response body is a {@link String}<br/>
     * &emsp;<b>of</b> - a subclass to deserialize response body to. Use to deserialize generic responses like {@link Collection}<{@link Map}>.
     */
    Response post(
            Map properties) {
        def all = properties +
                [method: 'post']
        performRequest all
    }

    /**
     * Possible properties:<br/>
     * &emsp;<b>path</b> - a request path (appended to a base url defined at {@link #HttpClient(Map)} constructor)<br/>
     * &emsp;<b>url</b> - a full request url (overrides path and base url)<br/>
     * &emsp;<b>headers</b> as {@link Map}<br/>
     * &emsp;<b>body</b> - a request body. Anything except {@link String} is serialized to a <a href="https://en.wikipedia.org/wiki/JSON">json</a>.<br/>
     * &emsp;<b>expecting</b> - a class to deserialize response body to. If not specified, response body is a {@link String}<br/>
     * &emsp;<b>of</b> - a subclass to deserialize response body to. Use to deserialize generic responses like {@link Collection}<{@link Map}>.
     */
    Response put(
            Map properties) {
        def all = properties +
                [method: 'put']
        performRequest all
    }

    /**
     * Possible properties:<br/>
     * &emsp;<b>path</b> - a request path (appended to a base url defined at {@link #HttpClient(Map)} constructor)<br/>
     * &emsp;<b>url</b> - a full request url (overrides path and base url)<br/>
     * &emsp;<b>headers</b> as {@link Map}<br/>
     * &emsp;<b>body</b> - a request body. Anything except {@link String} is serialized to a <a href="https://en.wikipedia.org/wiki/JSON">json</a>.<br/>
     * &emsp;<b>expecting</b> - a class to deserialize response body to. If not specified, response body is a {@link String}<br/>
     * &emsp;<b>of</b> - a subclass to deserialize response body to. Use to deserialize generic responses like {@link Collection}<{@link Map}>.
     */
    Response delete(
            Map properties) {
        def all = properties +
                [method: 'delete']
        performRequest all
    }

    private Response performRequest(
            Map properties) {
        def request = builder.request properties
        def response = client.execute request
        parser.parse(
                response,
                properties['expecting'] as Class,
                properties['of'] as Class)
    }

}
