package com.tomtom.http

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.tomtom.http.response.Response
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils

class ResponseParser {

    private mapper = new ObjectMapper()

    Response parse(
            HttpResponse response,
            Class type,
            Class subtype) {
        new Response(
                statusCode: response.statusLine?.statusCode,
                headers: headersOf(response) as Map<String, List<String>>,
                body: bodyOf(response, type, subtype))
    }

    private bodyOf(
            HttpResponse response,
            Class type,
            Class subtype) {
        def entity = response.entity as HttpEntity
        entity ? readEntity(entity, type, subtype) : null
    }

    private readEntity(
            HttpEntity entity,
            Class rawType,
            Class subtype) {
        def content = EntityUtils.toString entity
        if (rawType) {
            def type = subtype ?
                    typeOf(rawType, subtype) : typeOf(rawType)
            try {
                return mapper.readValue(content, type)
            } catch (Exception ignored) {
            }
        }
        content
    }

    private JavaType typeOf(
            Class type,
            Class subtype) {
        mapper.getTypeFactory()
                .constructParametricType type, subtype
    }

    private JavaType typeOf(
            Class type) {
        mapper.getTypeFactory()
                .constructType(new TypeReference<Object>() {
            @Override
            Class getType() {
                type
            }
        })
    }

    private static headersOf(
            HttpResponse response) {
        def allHeaders = response.allHeaders
        allHeaders ? allHeaders
                .groupBy { it.name }
                .collectEntries { [(it.key): it.value.value] } : [:]
    }

    def getMapper() {
        mapper
    }

}
