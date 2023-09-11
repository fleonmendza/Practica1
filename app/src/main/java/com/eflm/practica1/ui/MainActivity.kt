package com.eflm.practica1.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eflm.practica1.R
import com.eflm.practica1.application.PlatillosDBApp
import com.eflm.practica1.data.DishRepository
import com.eflm.practica1.data.db.model.PlatilloEntity
import com.eflm.practica1.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var dishes: List<PlatilloEntity> = emptyList()
    private lateinit var repository: DishRepository

   private lateinit var dishAdapter: DishAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


       repository = (application as PlatillosDBApp).repository

       dishAdapter = DishAdapter(){ dish ->
           dishClicked(dish)
       }

       binding.rvPlatillos.apply {
           layoutManager = LinearLayoutManager(this@MainActivity)
           adapter = dishAdapter
       }

       updateUI()

    }

    private fun updateUI(){
        lifecycleScope.launch {
            dishes = repository.getAllDishes()

            if(dishes.isNotEmpty()){
                binding.tvSinRegistros.visibility = View.INVISIBLE
            }else{

                binding.tvSinRegistros.visibility = View.VISIBLE
            }
            dishAdapter.updateList(dishes)
        }
    }

    fun click(view: View) {
        val dialog = DishDialog( updateUI = {
            updateUI()
        }, message = { text ->
            message(text)
        })
        dialog.show(supportFragmentManager, "dialog")
    }

    private fun dishClicked(dish: PlatilloEntity){
        //Toast.makeText(this, "Click en el juego con id: ${game.id}", Toast.LENGTH_SHORT).show()
        val dialog = DishDialog(newDish = false, dish = dish, updateUI = {
            updateUI()
        }, message = { text ->
            message(text)
        })
        dialog.show(supportFragmentManager, "dialog")
    }

    private fun message(text: String){
        Snackbar.make(binding.cl, text, Snackbar.LENGTH_SHORT)
            .setTextColor(Color.parseColor("#FFFFFF"))
            .setBackgroundTint(Color.parseColor("#9E1734"))
            .show()
    }




}