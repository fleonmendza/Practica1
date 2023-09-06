package com.eflm.practica1.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eflm.practica1.util.Constants

@Entity(tableName = Constants.DATABASE_DISH_TABLE)
data class PlatilloEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "game_id")
    val id: Long = 0,
    @ColumnInfo(name = "dish_name")
    val name: String,
    @ColumnInfo(name = "dish_img")
    val img: String,
    @ColumnInfo(name = "dish_category")
    val category: String,
    @ColumnInfo(name = "dish_description")
    val description: String,
    @ColumnInfo(name = "dish_preparation_time")
    val preparationTime: Int
)
