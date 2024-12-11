package com.cs407.madmarket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.madmarket.ViewHolder.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class HomeFragment : Fragment() {

    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var addProductButton: Button
    private lateinit var statusTextView: TextView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        productNameEditText = view.findViewById(R.id.productNameEditText)
        productPriceEditText = view.findViewById(R.id.productPriceEditText)
        addProductButton = view.findViewById(R.id.addProductButton)
        statusTextView = view.findViewById(R.id.statusTextView)
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView)
        db = FirebaseFirestore.getInstance()

        productAdapter = ProductAdapter(productList)
        productsRecyclerView.layoutManager = LinearLayoutManager(context)
        productsRecyclerView.adapter = productAdapter

        addProductButton.setOnClickListener {
            val productName = productNameEditText.text.toString()
            val productPrice = productPriceEditText.text.toString().toDoubleOrNull()

            if (productName.isNotEmpty() && productPrice != null) {
                addProductToFirestore(productName, productPrice)
            } else {
                statusTextView.text = "Please enter valid product details."
            }
        }

        fetchProductsFromFirestore()

        return view
    }

    private fun addProductToFirestore(name: String, price: Double) {
        val product = hashMapOf(
            "name" to name,
            "price" to price
        )

        db.collection("products")
            .add(product)
            .addOnSuccessListener {
                statusTextView.text = "Product added successfully!"
                fetchProductsFromFirestore() // Refresh the list
            }
            .addOnFailureListener { e ->
                statusTextView.text = "Error adding product: ${e.message}"
            }
    }

    private fun fetchProductsFromFirestore() {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (document in result) {
                    val product = document.toObject<Product>()
                    productList.add(product)
                }
                productAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                statusTextView.text = "Error fetching products: ${e.message}"
            }
    }
}