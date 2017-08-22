package com.tomtom.http.response

import spock.lang.Specification
import spock.lang.Unroll

import static com.tomtom.http.response.ResponseCode.*

class ResponseCodeSpec extends Specification {

    @Unroll
    def '#name is #value'(name, code, value) {
        expect:
        code == value

        where:
        name                     | code                                                                     | value
        'OK'                     | OK                     | 200
        'CREATED'                | CREATED                | 201
        'ACCEPTED'               | ACCEPTED               | 202
        'NO_CONTENT'             | NO_CONTENT             | 204
        'MOVED_PERMANENTLY'      | MOVED_PERMANENTLY      | 301
        'MOVED_TEMPORARILY'      | MOVED_TEMPORARILY      | 302
        'SEE_OTHER'              | SEE_OTHER              | 303
        'BAD_REQUEST'            | BAD_REQUEST            | 400
        'UNAUTHORIZED'           | UNAUTHORIZED           | 401
        'FORBIDDEN'              | FORBIDDEN              | 403
        'NOT_FOUND'              | NOT_FOUND              | 404
        'METHOD_NOT_ALLOWED'     | METHOD_NOT_ALLOWED     | 405
        'NOT_ACCEPTABLE'         | NOT_ACCEPTABLE         | 406
        'CONFLICT'               | CONFLICT               | 409
        'UNSUPPORTED_MEDIA_TYPE' | UNSUPPORTED_MEDIA_TYPE | 415
        'INTERNAL_SERVER_ERROR'  | INTERNAL_SERVER_ERROR  | 500
        'NOT_IMPLEMENTED'        | NOT_IMPLEMENTED        | 501
        'BAD_GATEWAY'            | BAD_GATEWAY            | 502
        'SERVICE_UNAVAILABLE'    | SERVICE_UNAVAILABLE    | 503
        'GATEWAY_TIMEOUT'        | GATEWAY_TIMEOUT        | 504
    }

}
