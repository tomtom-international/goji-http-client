package com.tomtom.http

class NoUrl extends IllegalArgumentException {

    NoUrl() {
        super('Please provide either url param or baseUrl and path parameters.')
    }

}
