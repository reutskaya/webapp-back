package com.webapp

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ApplicationController {

    @PostMapping("/find")
    fun searchByToken(@RequestBody request: Request) {
    println(request.tokens)

    }
}