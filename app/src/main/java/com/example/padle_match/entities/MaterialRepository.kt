package com.example.padle_match.entities

class MaterialRepository {
    private val materialesList: MutableList<String> = mutableListOf()

    init{
        materialesList.add("Pasto")
        materialesList.add("Cemento")
        materialesList.add("Polvo_de_Ladrillo")
            }

    fun getMaterialesList () : MutableList<String> {
        return materialesList
    }
}