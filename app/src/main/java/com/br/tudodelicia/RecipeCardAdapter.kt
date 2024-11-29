package com.br.tudodelicia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RecipeCardAdapter(
    private var foodList: List<Recipe>,
    private val onItemClicked: (String) -> Unit,
    private val currentUserId: String
) : RecyclerView.Adapter<RecipeCardAdapter.RecipeCardViewHolder>() {
    class RecipeCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.item_image)
        val title: TextView = itemView.findViewById(R.id.item_title)
        val description: TextView = itemView.findViewById(R.id.item_description)
        val chat: ImageView = itemView.findViewById(R.id.item_chat)
        val favorite: ImageView = itemView.findViewById(R.id.item_favorite)
        val likeCount: TextView = itemView.findViewById(R.id.item_like_count)
        val commentsCount: TextView = itemView.findViewById(R.id.item_comments_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_card, parent, false)
        return RecipeCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeCardViewHolder, position: Int) {
        val database = FirebaseFirestore.getInstance()
        val food = foodList[position]
        holder.title.text = food.title
        holder.description.text = food.description
        holder.image.setImageBitmap(food.imageResourceId)
        holder.chat.setImageResource(R.drawable.chat_bubble)
        holder.favorite.setImageResource(R.drawable.favorites)
        holder.itemView.setOnClickListener {
            onItemClicked(food.id)
        }
        holder.likeCount.text = food.likeCount.toString()
        holder.commentsCount.text = food.commentsCount.toString()

        val recipeRef = database.collection("recipes").document(food.id)

        updateFavoriteIcon(holder.favorite, food.likedByUsers.contains(currentUserId))

        holder.favorite.setOnClickListener {
            if (food.likedByUsers.contains(currentUserId)) {
                food.likedByUsers.remove(currentUserId)
                food.likeCount--

            } else {
                food.likedByUsers.add(currentUserId)
                food.likeCount++

            }
            recipeRef.update("likedByUsers", food.likedByUsers, "likeCount", food.likeCount)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun updateList(newList: List<Recipe>) {
        foodList = newList
        notifyDataSetChanged()
    }

    private fun updateFavoriteIcon(favorite: ImageView, isLiked: Boolean) {
        if (isLiked) {
            favorite.setColorFilter(favorite.context.getColor(R.color.colorLiked)) // Cor normal
        } else {
            favorite.setColorFilter(favorite.context.getColor(R.color.colorUnliked)) // Cinza
        }
    }
}
