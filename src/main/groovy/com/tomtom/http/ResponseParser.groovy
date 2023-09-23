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

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.tomtom.http.response.Response
import groovy.transform.PackageScope
import org.apache.hc.core5.http.HttpEntity
import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@PackageScope
class ResponseParser {

    private static Logger logger = LogManager.getLogger(HttpClient)
    private mapper = new ObjectMapper()

    Response parse(HttpResponse response, Class type, Class subtype) {
        logger.info "=> response: ${response.code}"
        new Response(
                statusCode: response.code,
                headers: headersOf(response) as Map<String, List<String>>,
                body: bodyOf(response, type, subtype))
    }

    private bodyOf(HttpResponse response, Class type, Class subtype) {
        def entity = response.entity as HttpEntity
        entity ? readEntity(entity, type, subtype) : null
    }

    private readEntity(HttpEntity entity, Class rawType, Class subtype) {
        def content = EntityUtils.toString entity
        if (content) logger.info "    body: ${content}"
        if (rawType) {
            def type = subtype ?
                    typeOf(rawType, subtype) : typeOf(rawType)
            try {
                return mapper.readValue(content, type)
            } catch (Exception e) {
                logger.warn "Failed to deserialize $content to $rawType due to $e.message, defaulting to string"
            }
        }
        content
    }

    private JavaType typeOf(Class type, Class subtype) {
        mapper.typeFactory
                .constructParametricType type, subtype
    }

    private JavaType typeOf(Class type) {
        mapper.typeFactory
                .constructType(new TypeReference<Object>() {
                    @Override
                    Class getType() {
                        type
                    }
                })
    }

    private static headersOf(HttpResponse response) {
        def headers = response.headers
        if (headers) {
            logger.info "    headers: ${response.headers}"
            headers
                    .groupBy { it.name }
                    .collectEntries { [(it.key): it.value.value] }
        } else [:]
    }

}
