package com.tomtom.http.response

import spock.lang.Specification

class ResponseSpec extends Specification {

    def 'Has toString'() {
        expect:
        new Response().toString() == 'null: null'
    }

}
