package com.webapp.data

import org.springframework.data.annotation.Id

data class Text(@Id val id: Int? = null, val text: String)