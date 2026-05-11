package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDescription: EditText
    private lateinit var ivPreview: ImageView
    private lateinit var tvImageLabel: TextView
    private lateinit var btnPickImage: Button
    private lateinit var btnCreate: Button
    private lateinit var btnViewCatalog: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adminAdapter: ProductAdapter

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                // Tomar permiso persistente para poder usar el URI
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) { /* No todos los proveedores lo soportan */ }
                selectedImageUri = uri
                ivPreview.setImageURI(uri)
                tvImageLabel.text = "Imagen seleccionada"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etName        = findViewById(R.id.etName)
        etPrice       = findViewById(R.id.etPrice)
        etCategory    = findViewById(R.id.etCategory)
        etDescription = findViewById(R.id.etDescription)
        ivPreview     = findViewById(R.id.ivImagePreview)
        tvImageLabel  = findViewById(R.id.tvImageLabel)
        btnPickImage  = findViewById(R.id.btnPickImage)
        btnCreate     = findViewById(R.id.btnCreateProduct)
        btnViewCatalog = findViewById(R.id.btnViewCatalog)
        recyclerView  = findViewById(R.id.adminProductsRecycler)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adminAdapter = ProductAdapter(ProductRepository.products) { /* sin carrito en admin */ }
        recyclerView.adapter = adminAdapter

        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnCreate.setOnClickListener {
            createProduct()
        }

        btnViewCatalog.setOnClickListener {
            val intent = Intent(this, ProductCatalogActivity::class.java)
            intent.putExtra("userEmail", "admin")
            startActivity(intent)
        }
    }

    private fun createProduct() {
        val name        = etName.text.toString().trim()
        val priceText   = etPrice.text.toString().trim()
        val category    = etCategory.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (name.isEmpty() || priceText.isEmpty() || category.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null || price <= 0) {
            Toast.makeText(this, "Introduce un precio válido", Toast.LENGTH_SHORT).show()
            return
        }

        val newProduct = Product(
            id          = ProductRepository.getNextId(),
            name        = name,
            price       = price,
            category    = category,
            description = description,
            imageUri    = selectedImageUri?.toString() ?: ""
        )

        ProductRepository.addProduct(newProduct)
        adminAdapter.notifyItemInserted(ProductRepository.products.size - 1)

        // Limpiar formulario
        etName.text.clear()
        etPrice.text.clear()
        etCategory.text.clear()
        etDescription.text.clear()
        ivPreview.setImageDrawable(null)
        tvImageLabel.text = "Sin imagen seleccionada"
        selectedImageUri = null

        Toast.makeText(this, "Producto \"$name\" creado correctamente", Toast.LENGTH_SHORT).show()
    }
}
