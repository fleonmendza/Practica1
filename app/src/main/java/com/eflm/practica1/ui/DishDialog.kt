package com.eflm.practica1.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.eflm.practica1.R
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
    description = ""

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

        val dishCategory = dish.category
        val spinnerOptions = resources.getStringArray(R.array.aliment_types)
        var found = 2

        for (i in 0 until spinnerOptions.size) {
            val option = spinnerOptions[i]
            if (option == dishCategory) {
                found = i
                break
            }
        }
        binding.spinner.setSelection(found)


        binding.apply {
            name.setText(dish.name)

            description.setText(dish.description)
        }

        dialog = if (newDish) {
            buildDialog(getString(R.string.guardar), getString(R.string.cancelar), {
                //Create (Guardar)
                dish.name = binding.name.text.toString()
                dish.category = binding.spinner.selectedItem.toString()
                dish.description = binding.description.text.toString()


                try {
                    lifecycleScope.launch {
                        repository.insertDish(dish.name, dish.category, dish.description)
                    }

                    message(getString(R.string.guardado))

                    //Actualizar la UI
                    updateUI()

                }catch(e: IOException){
                    e.printStackTrace()
                    message(getString(R.string.error_al_guardar_el_platillo))
                }
            }, {
                //Cancelar
            })
        } else {
            buildDialog(getString(R.string.actualizar), getString(R.string.borrar), {
                //Update
                dish.name = binding.name.text.toString()
                dish.category = binding.spinner.selectedItem.toString()
                dish.description = binding.description.text.toString()


                try {
                    lifecycleScope.launch {
                        repository.updateDish(dish)
                    }

                    message(getString(R.string.platillo_actualizado_exitosamente))

                    //Actualizar la UI
                    updateUI()

                }catch(e: IOException){
                    e.printStackTrace()
                    message(getString(R.string.error_al_actualizar_el_platillo))
                }

            }, {
                //Delete

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirmaci_n))
                    .setMessage(getString(R.string.realmente_deseas_eliminar_el_platillo, dish.name))
                    .setPositiveButton(getString(R.string.aceptar)){ _, _ ->
                        try {
                            lifecycleScope.launch {
                                repository.deleteDish(dish)
                            }

                            message(getString(R.string.platillo_eliminado_exitosamente))

                            //Actualizar la UI
                            updateUI()

                        }catch(e: IOException){
                            e.printStackTrace()
                            message(getString(R.string.error_al_eliminar_el_platillo))
                        }
                    }
                    .setNegativeButton(getString(R.string.cancelar)){ dialog, _ ->
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

        // Obtener la lista de opciones del recurso XML
        val spinnerOptions = resources.getStringArray(R.array.aliment_types)

        // Configurar el adaptador del Spinner
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el adaptador al Spinner
        binding.spinner.adapter = spinnerAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Verificar si la selección no es "Selecciona el tipo de alimento"
                if (binding.spinner.selectedItem.toString() != getString(R.string.selecciona_el_tipo_de_alimento)) {
                    // Habilitar el botón cuando se selecciona un tipo válido
                    saveButton?.isEnabled = validateFields()
                } else {
                    // Deshabilitar el botón cuando se selecciona "Selecciona el tipo de alimento"
                    saveButton?.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar el caso en el que no se ha seleccionado nada
                saveButton?.isEnabled = false
            }
        }

        binding.name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })

        binding.description.addTextChangedListener(object : TextWatcher {
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
        (binding.name.text.toString().isNotEmpty() && binding.description.text.toString()
            .isNotEmpty() && binding.spinner.selectedItem.toString() != getString(R.string.selecciona_el_tipo_de_alimento))

    private fun buildDialog(
        btn1Text: String,
        btn2Text: String,
        positiveButton: () -> Unit,
        negativeButton: () -> Unit
    ): Dialog =
        builder.setView(binding.root)
            .setTitle(getString(R.string.platillo))
            .setPositiveButton(btn1Text, DialogInterface.OnClickListener { dialog, which ->
                positiveButton()
            })
            .setNegativeButton(btn2Text) { _, _ ->
                negativeButton()
            }
            .create()


}