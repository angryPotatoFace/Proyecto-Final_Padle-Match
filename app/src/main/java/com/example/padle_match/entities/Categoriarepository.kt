package com.example.padle_match.entities

class Categoriarepository{
    private val categoriasList: MutableList<String> = mutableListOf()

    init{
        categoriasList.add("C1")
        categoriasList.add("C2")
        categoriasList.add("C3")
        categoriasList.add("D1")
        categoriasList.add("D2")
        categoriasList.add("D3")
        categoriasList.add("M1")
        categoriasList.add("M2")
        categoriasList.add("M3")
    }

    fun getCategoriasList () : MutableList<String> {
        return categoriasList
    }
}
