package com.cs407.madmarket

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddNewProductActivity : AppCompatActivity() {

    private lateinit var AddNewProductButton : Button
    private lateinit var InputProductName : EditText
    private lateinit var InputProductDescription: EditText
    private lateinit var InputProductImage : ImageView
    private lateinit var InputProductPrice : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_new_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AddNewProductButton = findViewById(R.id.add_new_product)
        InputProductImage = findViewById(R.id.select_product_image)
        InputProductPrice = findViewById(R.id.product_price)
        InputProductName = findViewById(R.id.product_name)
        InputProductDescription = findViewById(R.id.product_description)

    }
}