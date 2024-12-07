package com.cs407.madmarket

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.annotation.Nonnull

class AddNewProductActivity : AppCompatActivity() {

    private val galleryPick = 1
    private lateinit var imageUri : Uri
    private lateinit var AddNewProductButton : Button
    private lateinit var InputProductName : EditText
    private lateinit var InputProductDescription: EditText
    private lateinit var InputProductImage : ImageView
    private lateinit var InputProductPrice : EditText
    private lateinit var description : String
    private lateinit var price : String
    private lateinit var productName : String
    private lateinit var saveCurrentDate : String
    private lateinit var saveCurrentTime : String
    private lateinit var productKey: String
    private lateinit var productImagesRef: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_new_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images")

        AddNewProductButton = findViewById(R.id.add_new_product)
        InputProductImage = findViewById(R.id.select_product_image)
        InputProductPrice = findViewById(R.id.product_price)
        InputProductName = findViewById(R.id.product_name)
        InputProductDescription = findViewById(R.id.product_description)

        InputProductImage.setOnClickListener( {
            openGallery();
        })

        AddNewProductButton.setOnClickListener ({
            validateProductData()
        })
    }
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        ActivityResult(galleryPick, intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == galleryPick  && resultCode == RESULT_OK && data != null){
            imageUri = data.data!!
            InputProductImage.setImageURI(imageUri)

        }
    }

    private fun validateProductData(){
        description = InputProductDescription.getText().toString()
        price = InputProductPrice.getText().toString()
        productName = InputProductName.getText().toString()

        if(imageUri == null){
            Toast.makeText(this, "Product image required", Toast.LENGTH_SHORT )
        }
        else if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "No description added", Toast.LENGTH_SHORT )
        }
        else if (TextUtils.isEmpty(price)){
            Toast.makeText(this, "No price added", Toast.LENGTH_SHORT )
        }
        else if (TextUtils.isEmpty(productName)){
            Toast.makeText(this, "No product name added", Toast.LENGTH_SHORT )
        }
        else{
            storeProductInformation()
        }

    }

    private fun storeProductInformation() {
        val calendar: Calendar = Calendar.getInstance()

        val currentDate = SimpleDateFormat("MMM dd, yyyy")
        saveCurrentDate = currentDate.format(calendar.getTime())

        val currentTime = SimpleDateFormat("HH:mm:ss a")
        saveCurrentTime = currentTime.format(calendar.getTime())

        productKey = saveCurrentDate + saveCurrentTime

        val filepath = productImagesRef.child(imageUri.getLastPathSegment() + productKey)

        val uploadTask = filepath.putFile(imageUri)



    }

}