package com.cs407.madmarket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cs407.madmarket.R
import com.cs407.madmarket.ViewHolder.Product

class CartAdapter(private var cartList: List<Product>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productDescriptionTextView: TextView = itemView.findViewById(R.id.productDescriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartList[position]
        holder.productNameTextView.text = product.name
        holder.productPriceTextView.text = product.price.toString()
        holder.productDescriptionTextView.text = product.description
        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .into(holder.productImageView)
    }

    override fun getItemCount() = cartList.size

    fun updateCartList(newCartList: List<Product>) {
        cartList = newCartList
        notifyDataSetChanged()
    }
}