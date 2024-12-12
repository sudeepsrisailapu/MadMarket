package com.cs407.madmarket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.madmarket.ViewHolder.Product

class CartViewModel : ViewModel() {
    private val _cartList = MutableLiveData<MutableList<Product>>(mutableListOf())
    val cartList: LiveData<MutableList<Product>> get() = _cartList

    fun addToCart(product: Product) {
        _cartList.value?.add(product)
        _cartList.value = _cartList.value // Trigger LiveData update
    }
}