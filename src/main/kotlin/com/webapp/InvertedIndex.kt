package com.webapp

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "inverted_index")
data class InvertedIndex(
        @Id
        val word: String? = null,
        val texts: List<Int>)