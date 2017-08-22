package com.tomtom.http.response

class Response<T> {

    Integer statusCode
    T body
    Map<String, List<String>> headers

    @Override
    String toString() {
        "$statusCode: $body"
    }

}
