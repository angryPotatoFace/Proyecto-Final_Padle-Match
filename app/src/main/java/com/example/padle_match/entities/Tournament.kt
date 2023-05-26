package com.example.padle_match.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tournament(
    var id: String,
    var titulo: String,
    var club: String,
    var fecha: String,
    var hora: String,
    var categoría: String,
    var materialCancha: String,
    var cupos: Number,
    var costoInscripción: Number,
    var premios: String,
    var imagenTorneo: String,
    var userId: String,
    var idClub: String,
) : Parcelable
