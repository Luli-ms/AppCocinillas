package com.utad.appcocinillas.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.utad.appcocinillas.databinding.ItemMealsBinding
import com.utad.appcocinillas.model.Meal

class MealAdapter(
    private val onClickListener: (String) -> Unit
) : ListAdapter<Meal, MealViewHolder>(MealDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MealViewHolder {
        val binding = ItemMealsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MealViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), onClickListener)
    }
}

object MealDiffCallback : DiffUtil.ItemCallback<Meal>() {
    override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
        return oldItem.idMeal == newItem.idMeal
    }

    override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
        return oldItem == newItem
    }
}
