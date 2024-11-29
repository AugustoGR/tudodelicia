package com.br.tudodelicia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeCardAdapter(private var foodList: List<Recipe>) : RecyclerView.Adapter<RecipeCardAdapter.RecipeCardViewHolder>() {

    class RecipeCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.item_image)
        val title: TextView = itemView.findViewById(R.id.item_title)
        val description: TextView = itemView.findViewById(R.id.item_description)
        val chat: ImageView = itemView.findViewById(R.id.item_chat)
        val favorite: ImageView = itemView.findViewById(R.id.item_favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_card, parent, false)
        return RecipeCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeCardViewHolder, position: Int) {
        val food = foodList[position]
        holder.title.text = food.title
        holder.description.text = food.description
        holder.image.setImageBitmap(food.imageResourceId)
        holder.chat.setImageResource(R.drawable.chat_bubble)
        holder.favorite.setImageResource(R.drawable.favorites)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun updateList(newList: List<Recipe>) {
        foodList = newList
        notifyDataSetChanged()
    }
}
