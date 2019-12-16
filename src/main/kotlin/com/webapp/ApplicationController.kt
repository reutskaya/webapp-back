package com.webapp

import com.webapp.data.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException

@RestController
class ApplicationController(@Autowired val index: IndexDAO,
                            @Autowired val text: TextRepository) {
    val logger = LoggerFactory.getLogger(this.javaClass.name)

    @PostMapping("/find")
    fun searchByToken(@RequestBody request: SearchRequest): Response {
        logger.info("Request to /find path recieved")
        logger.info("Tokens: " + request.tokens.toString())
        val indexWords = request.tokens.fold(emptyList<Set<Int>>(), { a, b ->
            a + listOf(index.findById(b.toLowerCase()).map { it.texts }.orElse(emptyList()).toSet())
        })
        val resultSet = mutableSetOf<Int>()
        resultSet.addAll(indexWords.first())
        indexWords.map {
            resultSet.retainAll(it)
        }
        logger.info("Found texts: " + resultSet.toString())
        val result = text.findAllById(resultSet).map { Text(it.id, it.text.substring(0, 255) + " ...") }
        logger.info("Successful search")
        return Response(result)
    }

    @GetMapping("/get/{id}")
    fun getTextById(@PathVariable("id") id: Int): Response {
        logger.info("Request to path /get with id: " + id + " recieved")
        val findById = text.findById(id)
        if (!findById.isPresent()) {
            logger.info("No element with id: " + id)
            throw NoSuchElementException("There is no such text!")
        } else {
            logger.info("Successful get")
            return Response(listOf(findById.get()))
        }
    }

    private fun saveWordIndex(textId: Int, word: String) {
        if (!index.existsById(word)) {
            index.insert(InvertedIndex(word, mutableListOf(textId)))
        } else {
            val currentIndex = index.findById(word).get()
            val currentIndexValue: List<Int> = currentIndex.texts
            if (!currentIndexValue.contains(textId)) {

                index.save(InvertedIndex(word, currentIndexValue + textId))
            }
        }
    }

    @PostMapping("/reindex")
    fun reindex() {
        index.deleteAll()
        logger.info("Clear indexes")
        text.findAll().forEach {
            val re = Regex("[-+.^:,]")
            val replace = re.replace(it.text, "")
            val textId = it.id ?: 0

            replace.split(" ").forEach { word ->
                saveWordIndex(textId, word.toLowerCase())
            }
        }
        logger.info("Successful reindex")
    }
}