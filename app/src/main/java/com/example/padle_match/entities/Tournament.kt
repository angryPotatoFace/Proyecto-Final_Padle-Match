package com.example.padle_match.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Tournament(
    var titulo: String,
    var club: String,
    var fecha: String,
    var hora: String,
    var categoría: String,
    var materialCancha: String,
    var cupos: Number,
    var costoInscripción:Number,
    var premios: String,
    var imagenTorneo: String
) : Parcelable
