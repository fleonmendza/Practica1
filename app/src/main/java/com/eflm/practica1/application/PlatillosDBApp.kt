package com.eflm.practica1.application

import android.app.Application
import com.eflm.practica1.data.DishRepository
import com.eflm.practica1.data.db.DishDatabase

class PlatillosDBApp(): Application() {
    private val database by lazy{
        DishDatabase.getDatabase(this)
    }

    val repository by lazy{
        DishRepository(database.DishDao())
    }
}