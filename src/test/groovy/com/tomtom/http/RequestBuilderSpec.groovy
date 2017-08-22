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
