package com.example.padle_match.entities

import android.media.Image
import android.os.Parcelable
import java.util.Date
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tournament(
    var idTorneo: String,
    var club: Club,
    var titulo: String,
    var fecha: String,
    var hora: String,
    var categoría:Categoria ,
    var cupos: Number,
    var costoInscripción:Number,
    var premios: String,
    var imagenTorneo: String
) : Parcelable
