package com.webapp.data

import com.webapp.InvertedIndex
import org.springframework.data.mongodb.repository.MongoRepository

interface IndexDAO : MongoRepository<InvertedIndex, String>

interface TextRepository : MongoRepository<Text, Int>