package com.eflm.practica1.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.eflm.practica1.data.db.model.PlatilloEntity
import com.eflm.practica1.util.Constants.DATABASE_DISH_TABLE

@Dao
interface DishDao {

    //Create
    @Insert
    suspend fun insertDish(dish: PlatilloEntity)

    @Insert
    suspend fun insertDish(dish: List<PlatilloEntity>)

    //Read
    @Query(value = "SELECT * FROM platillos_data_table")
    suspend fun getAllDishes(): List<PlatilloEntity>


    @Update
    suspend fun updateDish(dish: PlatilloEntity)

    //Delete
    @Delete
    suspend fun deleteDish(dish: PlatilloEntity)

}