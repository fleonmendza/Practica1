package com.eflm.practica1.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eflm.practica1.R
import com.eflm.practica1.data.db.model.PlatilloEntity
import com.eflm.practica1.databinding.DishElementBinding

class DishAdapter(private val onGameClick: (PlatilloEntity) -> Unit): RecyclerView.Adapter<DishAdapter.ViewHolder>() {

    private var dishes: List<PlatilloEntity> = emptyList()

    class ViewHolder(private val binding: DishElementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dish: PlatilloEntity) {
            if (dish.category.equals("Entrada")){
                binding.ivIcon.setImageResource(R.drawable.entrada)
            }
            else if (dish.category.equals("Plato Fuerte") ){
                binding.ivIcon.setImageResource(R.drawable.platofuerte)
            }
            else if (dish.category.equals("Postre")){
                binding.ivIcon.setImageResource(R.drawable.postres)
            }
            binding.apply {
                tvname.text = dish.name
                tvcategory.text = dish.category
                tvtime.text = dish.description

                }
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DishElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dishes.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dishes[position])

        holder.itemView.setOnClickListener {
            //Aquí va el click del elemento
            onGameClick(dishes[position])
        }


    }

    fun updateList(list: List<PlatilloEntity>) {
        dishes = list
        notifyDataSetChanged()
    }

}