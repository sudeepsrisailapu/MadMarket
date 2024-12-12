package com.cs407.madmarket

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import com.cs407.madmarket.ViewHolder.Product

class HomeFragment : Fragment() {

    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productDescriptionEditText: EditText
    private lateinit var takePictureButton: Button
    private lateinit var selectFromGalleryButton: Button
    private lateinit var productImageView: ImageView
    private lateinit var addProductButton: Button
    private lateinit var statusTextView: TextView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var productAdapter: ProductAdapter
    private lateinit var addProductLayout: LinearLayout
    private lateinit var toggleAddProductButton: Button
    private val productList = mutableListOf<Product>()
    private var currentPhotoPath: String? = null
    private var imageUri: Uri? = null

    companion object {
        private const val REQUEST_TAKE_PHOTO = 1
        private const val REQUEST_SELECT_FROM_GALLERY = 2
        private const val REQUEST_PERMISSIONS = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        productNameEditText = view.findViewById(R.id.productNameEditText)
        productPriceEditText = view.findViewById(R.id.productPriceEditText)
        productDescriptionEditText = view.findViewById(R.id.productDescriptionEditText)
        takePictureButton = view.findViewById(R.id.takePictureButton)
        selectFromGalleryButton = view.findViewById(R.id.selectFromGalleryButton)
        productImageView = view.findViewById(R.id.productImageView)
        addProductButton = view.findViewById(R.id.addProductButton)
        statusTextView = view.findViewById(R.id.statusTextView)
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView)
        addProductLayout = view.findViewById(R.id.addProductLayout)
        toggleAddProductButton = view.findViewById(R.id.toggleAddProductButton)
        db = FirebaseFirestore.getInstance()

        productAdapter = ProductAdapter(productList)
        productsRecyclerView.layoutManager = LinearLayoutManager(context)
        productsRecyclerView.adapter = productAdapter

        requestPermissions()

        toggleAddProductButton.setOnClickListener {
            if (addProductLayout.visibility == View.GONE) {
                addProductLayout.visibility = View.VISIBLE
                toggleAddProductButton.text = "Hide Add Product"
            } else {
                addProductLayout.visibility = View.GONE
                toggleAddProductButton.text = "Add Product"
            }
        }

        takePictureButton.setOnClickListener { dispatchTakePictureIntent() }
        selectFromGalleryButton.setOnClickListener { dispatchSelectFromGalleryIntent() }

        addProductButton.setOnClickListener {
            val productName = productNameEditText.text.toString()
            val productPrice = productPriceEditText.text.toString().toDoubleOrNull()
            val productDescription = productDescriptionEditText.text.toString()
            val productImageUrl = imageUri.toString()

            if (productName.isNotEmpty() && productPrice != null && productDescription.isNotEmpty() && productImageUrl.isNotEmpty()) {
                addProductToFirestore(productName, productPrice, productDescription, productImageUrl)
            } else {
                statusTextView.text = "Please enter valid product details."
            }
        }

        fetchProductsFromFirestore()

        return view
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_PERMISSIONS
        )
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.cs407.madmarket.provider",
                        it
                    )
                    imageUri = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun dispatchSelectFromGalleryIntent() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { selectFromGalleryIntent ->
            selectFromGalleryIntent.type = "image/*"
            startActivityForResult(selectFromGalleryIntent, REQUEST_SELECT_FROM_GALLERY)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    productImageView.setImageURI(imageUri)
                }
                REQUEST_SELECT_FROM_GALLERY -> {
                    imageUri = data?.data
                    productImageView.setImageURI(imageUri)
                }
            }
        }
    }

    private fun addProductToFirestore(name: String, price: Double, description: String, imageUrl: String) {
        val product = hashMapOf(
            "name" to name,
            "price" to price,
            "description" to description,
            "imageUrl" to imageUrl
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