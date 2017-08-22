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

    Response head(
            Map properties) {
        def all = properties +
                [method: 'head']
        performRequest all
    }

    Response get(
            Map properties) {
        def all = properties +
                [method: 'get']
        performRequest all
    }

    Response post(
            Map properties) {
        def all = properties +
                [method: 'post']
        performRequest all
    }

    Response put(
            Map properties) {
        def all = properties +
                [method: 'put']
        performRequest all
    }

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
