package com.cs407.madmarket//package com.cs407.madmarket
//
//
//import android.app.ProgressDialog
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.text.TextUtils
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.gms.tasks.Continuation
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.android.gms.tasks.OnFailureListener
//import com.google.android.gms.tasks.OnSuccessListener
//import com.google.android.gms.tasks.Task
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
//import com.google.firebase.storage.UploadTask
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.HashMap
//
//class AdminAddNewProductActivity : AppCompatActivity() {
//
//    private lateinit var categoryName: String
//    private lateinit var description: String
//    private lateinit var price: String
//    private lateinit var pname: String
//    private lateinit var saveCurrentDate: String
//    private lateinit var saveCurrentTime: String
//    private lateinit var addNewProductButton: Button
//    private lateinit var inputProductImage: ImageView
//    private lateinit var inputProductName: EditText
//    private lateinit var inputProductDescription: EditText
//    private lateinit var inputProductPrice: EditText
//    private val galleryPick = 1
//    private var imageUri: Uri? = null
//    private lateinit var productRandomKey: String
//    private lateinit var downloadImageUrl: String
//    private lateinit var productImagesRef: StorageReference
//    private lateinit var productsRef: DatabaseReference
//    private lateinit var loadingBar: ProgressDialog
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_admin_add_new_product)
//
//        categoryName = intent.extras?.get("category").toString()
//        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images")
//        productsRef = FirebaseDatabase.getInstance().getReference().child("Products")
//
//        addNewProductButton = findViewById(R.id.add_new_product)
//        inputProductImage = findViewById(R.id.select_product_image)
//        inputProductName = findViewById(R.id.product_name)
//        inputProductDescription = findViewById(R.id.product_description)
//        inputProductPrice = findViewById(R.id.product_price)
//        loadingBar = ProgressDialog(this)
//
//        inputProductImage.setOnClickListener { openGallery() }
//
//        addNewProductButton.setOnClickListener { validateProductData() }
//    }
//
//    private fun openGallery() {
//        val galleryIntent = Intent()
//        galleryIntent.action = Intent.ACTION_GET_CONTENT
//        galleryIntent.type = "image/*"
//        startActivityForResult(galleryIntent, galleryPick)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
//            imageUri = data.data
//            inputProductImage.setImageURI(imageUri)
//        }
//    }
//
//    private fun validateProductData() {
//        description = inputProductDescription.text.toString()
//        price = inputProductPrice.text.toString()
//        pname = inputProductName.text.toString()
//
//        when {
//            imageUri == null -> Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show()
//            TextUtils.isEmpty(description) -> Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show()
//            TextUtils.isEmpty(price) -> Toast.makeText(this, "Please write product Price...", Toast.LENGTH_SHORT).show()
//            TextUtils.isEmpty(pname) -> Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show()
//            else -> storeProductInformation()
//        }
//    }
//
//    private fun storeProductInformation() {
//        loadingBar.setTitle("Add New Product")
//        loadingBar.setMessage("Dear Admin, please wait while we are adding the new product.")
//        loadingBar.setCanceledOnTouchOutside(false)
//        loadingBar.show()
//
//        val calendar = Calendar.getInstance()
//
//        val currentDate = SimpleDateFormat("MMM dd, yyyy")
//        saveCurrentDate = currentDate.format(calendar.time)
//
//        val currentTime = SimpleDateFormat("HH:mm:ss a")
//        saveCurrentTime = currentTime.format(calendar.time)
//
//        productRandomKey = saveCurrentDate + saveCurrentTime
//
//        val filePath = productImagesRef.child(imageUri!!.lastPathSegment + productRandomKey + ".jpg")
//        val uploadTask = filePath.putFile(imageUri!!)
//
//        uploadTask.addOnFailureListener(OnFailureListener { e ->
//            val message = e.toString()
//            Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
//            loadingBar.dismiss()
//        }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
//            Toast.makeText(this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show()
//
//            val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
//                if (!task.isSuccessful) {
//                    throw task.exception!!
//                }
//
//                downloadImageUrl = filePath.downloadUrl.toString()
//                filePath.downloadUrl
//            }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
//                if (task.isSuccessful) {
//                    downloadImageUrl = task.result.toString()
//                    Toast.makeText(this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show()
//                    saveProductInfoToDatabase()
//                }
//            })
//        })
//    }
//
//    private fun saveProductInfoToDatabase() {
//        val productMap = HashMap<String, Any>()
//        productMap["pid"] = productRandomKey
//        productMap["date"] = saveCurrentDate
//        productMap["time"] = saveCurrentTime
//        productMap["description"] = description
//        productMap["image"] = downloadImageUrl
//        productMap["category"] = categoryName
//        productMap["price"] = price
//        productMap["pname"] = pname
//
//        productsRef.child(productRandomKey).updateChildren(productMap)
//            .addOnCompleteListener(OnCompleteListener<Void> { task ->
//                if (task.isSuccessful) {
//                    val intent = Intent(this, AdminCategoryActivity::class.java)
//                    startActivity(intent)
//
//                    loadingBar.dismiss()
//                    Toast.makeText(this, "Product is added successfully..", Toast.LENGTH_SHORT).show()
//                } else {
//                    loadingBar.dismiss()
//                    val message = task.exception.toString()
//                    Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//}