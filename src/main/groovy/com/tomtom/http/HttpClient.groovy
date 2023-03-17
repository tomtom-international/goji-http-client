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
import org.apache.http.client.HttpClient as ApacheHttpClient
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.ssl.SSLContexts

import java.security.cert.CertificateException
import java.security.cert.X509Certificate

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES

class HttpClient {

    private static ApacheHttpClient client

    final RequestBuilder builder
    final ResponseParser parser

    /**
     * Builds {@link HttpClient} with a number of (optional) parameters.
     * @param baseUrl a url prefix for request {@code path} parameter
     * @param client a custom {@link org.apache.http.client.HttpClient}
     * @param mapper a custom {@link ObjectMapper}
     */
    HttpClient(Map properties = [:]) {
        client = properties['client'] as ApacheHttpClient ?: defaultClient()

        def mapper = properties['mapper'] as ObjectMapper ?: defaultMapper()
        def builderParams = [mapper: mapper]
        def baseUrl = properties['baseUrl'] as String
        if (baseUrl) builderParams += [baseUrl: baseUrl]
        def defaultHeaders = properties['defaultHeaders']
        if (defaultHeaders) builderParams += [defaultHeaders: defaultHeaders]

        builder = properties['builder'] as RequestBuilder ?: new RequestBuilder(builderParams)
        parser = properties['parser'] as ResponseParser ?: new ResponseParser(mapper: mapper)
    }

    /**
     * Builds {@link HttpClient} with a base URL.
     * @param baseUrl a url prefix for request {@code path} parameter
     */
    HttpClient(String baseUrl) {
        this(baseUrl: baseUrl)
    }

    /**
     * Builds {@link HttpClient} with a base URL and default headers.
     * @param baseUrl a url prefix for request {@code path} parameter
     * @param defaultHeaders default headers
     */
    HttpClient(String baseUrl, Map defaultHeaders) {
        this(baseUrl: baseUrl, defaultHeaders: defaultHeaders)
    }

    /**
     * Builds {@link HttpClient} with a base URL.
     * @param baseUrl a url prefix for request {@code path} parameter
     */
    HttpClient(URI baseUrl) {
        this(baseUrl.toString())
    }

    /**
     * Builds {@link HttpClient} with a base URL and default headers.
     * @param baseUrl a url prefix for request {@code path} parameter
     * @param defaultHeaders default headers
     */
    HttpClient(URI baseUrl, Map defaultHeaders) {
        this(baseUrl: baseUrl, defaultHeaders: defaultHeaders)
    }

    /**
     * Builds {@link HttpClient} with a base URL.
     * @param baseUrl a url prefix for request {@code path} parameter
     */
    HttpClient(URL baseUrl) {
        this(baseUrl.toString())
    }

    /**
     * Builds {@link HttpClient} with a base URL and default headers.
     * @param baseUrl a url prefix for request {@code path} parameter
     * @param defaultHeaders default headers
     */
    HttpClient(URL baseUrl, Map defaultHeaders) {
        this(baseUrl: baseUrl, defaultHeaders: defaultHeaders)
    }

    private static ObjectMapper defaultMapper() {
        new ObjectMapper().disable(FAIL_ON_UNKNOWN_PROPERTIES)
    }

    private static ApacheHttpClient defaultClient() {
        def context = SSLContexts
                .custom()
                .loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    boolean isTrusted(
                            X509Certificate[] chain,
                            String authType) throws CertificateException {
                        true
                    }
                }).build()

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
    Response head(Map properties) {
        def all = properties + [method: 'head']
        performRequest all
    }

    def <T> RequestBuilder.UrlBuilder<T> head() {
        new RequestBuilder.UrlBuilder<>(method: { Map p -> head(p) })
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
    Response get(Map properties) {
        def all = properties + [method: 'get']
        performRequest all
    }

    def <T> RequestBuilder.UrlBuilder<T> get() {
        new RequestBuilder.UrlBuilder<>(method: { Map p -> get(p) })
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
    Response post(Map properties) {
        def all = properties + [method: 'post']
        performRequest all
    }

    def <T> RequestBuilder.UrlBuilder<T> post() {
        new RequestBuilder.UrlBuilder<>(method: { Map p -> post(p) })
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
    Response put(Map properties) {
        def all = properties + [method: 'put']
        performRequest all
    }

    def <T> RequestBuilder.UrlBuilder<T> put() {
        new RequestBuilder.UrlBuilder<>(method: { Map p -> put(p) })
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
    Response delete(Map properties) {
        def all = properties + [method: 'delete']
        performRequest all
    }

    def <T> RequestBuilder.UrlBuilder<T> delete() {
        new RequestBuilder.UrlBuilder<>(method: { Map p -> delete(p) })
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
    Response trace(Map properties) {
        def all = properties + [method: 'trace']
        performRequest all
    }

    def <T> RequestBuilder.UrlBuilder<T> trace() {
        new RequestBuilder.UrlBuilder<>(method: { Map p -> trace(p) })
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
    Response patch(Map properties) {
        def all = properties + [method: 'patch']
        performRequest all
    }

    def <T> RequestBuilder.UrlBuilder<T> patch() {
        new RequestBuilder.UrlBuilder<>(method: { Map p -> patch(p) })
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
    Response options(Map properties) {
        def all = properties + [method: 'options']
        performRequest all
    }

    def <T> RequestBuilder.UrlBuilder<T> options() {
        new RequestBuilder.UrlBuilder<>(method: { Map p -> options(p) })
    }

    private Response performRequest(Map properties) {
        def request = builder.request properties
        def response = client.execute request
        parser.parse(
                response,
                properties['expecting'] as Class,
                properties['of'] as Class)
    }

}
