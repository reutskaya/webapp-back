package com.webapp.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field

data class Text(@Id val id: Int? = null, val text: String)