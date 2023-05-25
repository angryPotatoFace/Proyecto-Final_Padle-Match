package com.example.padle_match.fragments


import android.icu.text.SimpleDateFormat
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.lifecycle.ViewModel
import java.util.*


class AddTournamentViewModel : ViewModel() {

    companion object{
        private val MSG_CAMPO_REQUERIDO = "Campo requerido"
    }
    fun validateFields(
        editName: EditText,
        editClub: AutoCompleteTextView,
        editFecha: EditText,
        editHorario: EditText,
        editCategorias: AutoCompleteTextView,
        editMateriales: AutoCompleteTextView,
        editCupo: EditText,
        editCosto: EditText,
        editPremios: EditText,
        editImagen: EditText
    ) : Boolean {
        return notEmpty(editName) && notEmpty(editClub) && notEmpty(editFecha) && notEmpty(editHorario) && notEmpty(editCategorias)
                && notEmpty(editMateriales) && notEmpty(editCupo) && notEmpty(editCosto) && notEmpty(editPremios) && notEmpty(editImagen)
    }

    private fun notEmpty(editText: EditText) : Boolean {
        var isValid : Boolean = true
        val value = editText.text.toString().trim()
        if(value.isNullOrEmpty()){
            editText.error = MSG_CAMPO_REQUERIDO
            isValid = false
        }
        return isValid
    }
    private fun notEmpty(autoCompleteTextView: AutoCompleteTextView): Boolean {
        val editText: EditText = autoCompleteTextView
        return notEmpty(editText)
    }

}