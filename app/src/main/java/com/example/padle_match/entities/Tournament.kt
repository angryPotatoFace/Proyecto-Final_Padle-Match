package com.example.padle_match.entities

import android.media.Image
import java.util.Date

data class Tournament(
    var idTorneo: String,
    var fecha: Date,
    var hora: Int,
    var categoría: Categoria,
    var cupos: Integer,
    var costoInscripción:Double,
    var premios: String,
    var imagenTorneo: Image
)
