package com.example.padle_match.entities

import android.media.Image
import java.util.Date

data class Tournament(
    var idTorneo: String,
    var titulo: String,
    var fecha: String,
    var hora: String,
    var categoría: Categoria,
    var cupos: Int,
    var costoInscripción:Double,
    var premios: String,
    var imagenTorneo: String
)
