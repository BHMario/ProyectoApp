package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productCountText: TextView
    private lateinit var viewModel: ProductViewModel

    companion object {
        val CATEGORIES = arrayOf("Manga", "Figuras", "Merchandising")
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

        viewModel        = ViewModelProvider(this)[ProductViewModel::class.java]
        recyclerView     = findViewById(R.id.adminProductsRecycler)
        productCountText = findViewById(R.id.productCountText)

        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddProduct)

        recyclerView.layoutManager = LinearLayoutManager(this)
        refreshList()

        fabAdd.setOnClickListener { showProductDialog(null) }

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.adminToolbar).apply {
            setNavigationOnClickListener { finish() }
        }
    }

    // ── List ──────────────────────────────────────────────────────────────── //

    private fun refreshList() {
        val products = viewModel.getAllForAdmin()
        productCountText.text = "${products.size} productos en inventario"
        recyclerView.adapter = AdminProductAdapter(
            products,
            onEdit   = { showProductDialog(it) },
            onDelete = { confirmDelete(it) }
        )
    }

    // ── Delete dialog ─────────────────────────────────────────────────────── //

    private fun confirmDelete(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Eliminar '${product.name}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteProduct(product.id)
                refreshList()
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ── Add / Edit dialog ─────────────────────────────────────────────────── //

    private fun showProductDialog(existing: Product?) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_product, null)
        val nameEt     = view.findViewById<EditText>(R.id.dialogProductName)
        val priceEt    = view.findViewById<EditText>(R.id.dialogProductPrice)
        val spinner    = view.findViewById<Spinner>(R.id.dialogProductCategory)
        val descEt     = view.findViewById<EditText>(R.id.dialogProductDesc)
        val stockEt    = view.findViewById<EditText>(R.id.dialogProductStock)

        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, CATEGORIES)

        existing?.let {
            nameEt.setText(it.name)
            priceEt.setText(it.price.toString())
            spinner.setSelection(CATEGORIES.indexOf(it.category).coerceAtLeast(0))
            descEt.setText(it.description)
            stockEt.setText(it.stock.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(if (existing == null) "Añadir producto" else "Editar producto")
            .setView(view)
            .setPositiveButton(if (existing == null) "Añadir" else "Guardar") { _, _ ->
                val name  = nameEt.text.toString().trim()
                val price = priceEt.text.toString().toDoubleOrNull() ?: 0.0
                val cat   = spinner.selectedItem.toString()
                val desc  = descEt.text.toString().trim()
                val stock = stockEt.text.toString().toIntOrNull() ?: 0

                if (name.isEmpty()) {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (price <= 0) {
                    Toast.makeText(this, "El precio debe ser mayor que 0", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (existing == null) {
                    viewModel.insertProduct(Product(0, name, price, cat, desc, stock))
                    Toast.makeText(this, "Producto añadido", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.updateProduct(Product(existing.id, name, price, cat, desc, stock))
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                }
                refreshList()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
