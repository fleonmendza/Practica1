package com.eflm.practica1.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.eflm.practica1.application.PlatillosDBApp
import com.eflm.practica1.data.DishRepository
import com.eflm.practica1.data.db.model.PlatilloEntity
import com.eflm.practica1.databinding.DishDialogBinding
import kotlinx.coroutines.launch
import java.io.IOException

class DishDialog (
private val newDish: Boolean = true,
private var dish: PlatilloEntity = PlatilloEntity(
    name = "",
    category = "",
    description = "",
    preparationTime = 0

),
private val updateUI: () -> Unit,
private val message: (String) -> Unit
) : DialogFragment() {

    private var _binding: DishDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: Dialog

    private var saveButton: Button? = null

    private lateinit var repository: DishRepository

    //Se configura el diálogo inicial
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DishDialogBinding.inflate(requireActivity().layoutInflater)

        repository = (requireContext().applicationContext as PlatillosDBApp).repository

        builder = AlertDialog.Builder(requireContext())

        binding.apply {
            tietTitle.setText(dish.name)
            tietGenre.setText(dish.category)
            tietDeveloper.setText(dish.description)
        }

        dialog = if (newDish) {
            buildDialog("Guardar", "Cancelar", {
                //Create (Guardar)
                dish.name = binding.tietTitle.text.toString()
                dish.category = binding.spinner.selectedItem.toString()
                dish.description = binding.tietDeveloper.text.toString()
                dish.preparationTime = 8

                try {
                    lifecycleScope.launch {
                        repository.insertDish(dish.name, dish.category, dish.description, dish.preparationTime)
                    }

                    message("Guardado")

                    //Actualizar la UI
                    updateUI()

                }catch(e: IOException){
                    e.printStackTrace()
                    message("Error al guardar el platillo")
                }
            }, {
                //Cancelar
            })
        } else {
            buildDialog("Actualizar", "Borrar", {
                //Update
                dish.name = binding.tietTitle.text.toString()
                dish.category = binding.tietGenre.text.toString()
                dish.description = binding.tietDeveloper.text.toString()

                try {
                    lifecycleScope.launch {
                        repository.updateDish(dish)
                    }

                    message("Platillo actualizado exitosamente")

                    //Actualizar la UI
                    updateUI()

                }catch(e: IOException){
                    e.printStackTrace()
                    message("Error al actualizar el Platillo")
                }

            }, {
                //Delete

                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmación")
                    .setMessage("¿Realmente deseas eliminar el Platillo ${dish.name}?")
                    .setPositiveButton("Aceptar"){ _,_ ->
                        try {
                            lifecycleScope.launch {
                                repository.deleteDish(dish)
                            }

                            message("Platillo eliminado exitosamente")

                            //Actualizar la UI
                            updateUI()

                        }catch(e: IOException){
                            e.printStackTrace()
                            message("Error al eliminar el Platillo")
                        }
                    }
                    .setNegativeButton("Cancelar"){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()


            })
        }
        return dialog
    }

    //Cuando se destruye el fragment
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //Se llama después de que se muestra el diálogo en pantalla
    override fun onStart() {
        super.onStart()

        val alertDialog =
            dialog as AlertDialog
        saveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
        saveButton?.isEnabled = false

        binding.tietTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })

        binding.tietGenre.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })

        binding.tietDeveloper.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })

    }

    private fun validateFields() =
        (binding.tietTitle.text.toString().isNotEmpty() && binding.tietGenre.text.toString()
            .isNotEmpty() && binding.tietDeveloper.text.toString().isNotEmpty())

    private fun buildDialog(
        btn1Text: String,
        btn2Text: String,
        positiveButton: () -> Unit,
        negativeButton: () -> Unit
    ): Dialog =
        builder.setView(binding.root)
            .setTitle("Platillo")
            .setPositiveButton(btn1Text, DialogInterface.OnClickListener { dialog, which ->
                positiveButton()
            })
            .setNegativeButton(btn2Text) { _, _ ->
                negativeButton()
            }
            .create()


}