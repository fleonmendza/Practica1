package com.eflm.practica1.data

import com.eflm.practica1.data.db.DishDao
import com.eflm.practica1.data.db.model.PlatilloEntity

class DishRepository(private val dishDao: DishDao) {

    suspend fun insertGame(dish: PlatilloEntity){
        dishDao.insertDish(dish)
    }

    suspend fun insertDish(name: String, category: String, description: String, time: Int){
        dishDao.insertDish(PlatilloEntity(name = name, category = category, description = description, preparationTime = time ))
    }

    suspend fun getAllDishes(): List<PlatilloEntity> = dishDao.getAllDishes()

    suspend fun updateDish(dish: PlatilloEntity){
        dishDao.updateDish(dish)
    }

    suspend fun deleteDish(dish: PlatilloEntity){
        dishDao.deleteDish(dish)
    }

}