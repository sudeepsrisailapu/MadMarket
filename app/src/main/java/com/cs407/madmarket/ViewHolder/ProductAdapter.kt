package com.cs407.madmarket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cs407.madmarket.ViewHolder.Product

class ProductAdapter(
    private val productList: List<Product>,
    private val onAddToCartClick: ((Product) -> Unit)? = null,
    private val showAddToCartButton: Boolean = true
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productDescriptionTextView: TextView = itemView.findViewById(R.id.productDescriptionTextView)
        val productContactTextView: TextView = itemView.findViewById(R.id.productContactTextView)
        val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productNameTextView.text = product.name
        holder.productPriceTextView.text = product.price.toString()
        holder.productDescriptionTextView.text = product.description
        holder.productContactTextView.text = product.contact
        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .into(holder.productImageView)

        if (showAddToCartButton) {
            holder.addToCartButton.visibility = View.VISIBLE
            holder.addToCartButton.setOnClickListener {
                onAddToCartClick?.invoke(product)
            }
        } else {
            holder.addToCartButton.visibility = View.GONE
        }
    }

    override fun getItemCount() = productList.size
}