package com.webapp

import com.webapp.data.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException

@RestController
class ApplicationController(@Autowired val index: IndexDAO,
                            @Autowired val text: TextRepository) {

    @PostMapping("/find")
    fun searchByToken(@RequestBody request: SearchRequest): Response {

        val indexWords = request.tokens.fold(emptyList<Set<Int>>(), { a, b ->
            a + listOf(index.findById(b).map { it.texts }.orElse(emptyList()).toSet())
        })
        val resultSet = mutableSetOf<Int>()
        resultSet.addAll(indexWords.first())
        indexWords.map {
            resultSet.retainAll(it)
        }
        val result = text.findAllById(resultSet).map { Text(it.id, it.text.substring(255)) }
        return Response(result)
    }

    @GetMapping("/get/{id}")
    fun getTextById(@PathVariable("id") id: Int): Response {
        val findById = text.findById(id)
        if (!findById.isPresent()) {
            throw NoSuchElementException("This text is not presented!")
        } else {
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
        text.findAll().forEach {
            val re = Regex("[-+.^:,]")
            val replace = re.replace(it.text, "")
            val textId = it.id ?: 0

            replace.split(" ").forEach { word ->
                saveWordIndex(textId, word.toLowerCase())
            }
        }
    }
}