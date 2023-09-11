package com.eflm.practica1.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eflm.practica1.data.db.model.PlatilloEntity
import com.eflm.practica1.util.Constants


@Database(
    entities = [PlatilloEntity::class],
    version = 1,  //versión de la BD. Importante para las migraciones
    exportSchema = true //por defecto es true
)
abstract class DishDatabase: RoomDatabase() {

    abstract fun DishDao(): DishDao

    companion object{
        @Volatile //lo que se escriba en este campo, será inmediatamente visible a otros hilos
        private var INSTANCE: DishDatabase? = null

        fun getDatabase(context: Context): DishDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DishDatabase::class.java,
                    Constants.DATABASE_NAME
                ).fallbackToDestructiveMigration() //Permite a Room recrear las tablas de la BD si las migraciones no se encuentran
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }

}