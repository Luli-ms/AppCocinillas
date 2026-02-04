package com.utad.appcocinillas.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.utad.appcocinillas.databinding.ItemMealsBinding
import com.utad.appcocinillas.model.Meal

class MealViewHolder(
    private val binding: ItemMealsBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Meal, onClickListener: (idMeal: String) -> Unit) {
        Picasso.get()
            .load(item.strMealThumb)
            .into(binding.ivMeal)
        binding.tvMeal.text = item.strMeal
        binding.itemMeal.setOnClickListener {
            onClickListener(item.idMeal)
        }
    }
}