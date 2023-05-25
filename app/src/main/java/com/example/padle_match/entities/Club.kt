package com.example.padle_match.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Club(
    var cuit:String,
    var nombre:String,
    var partido:String,
    var localidad:String,
    var domicilio:String,
    var email: String,
    var telefonos:String
): Parcelable
