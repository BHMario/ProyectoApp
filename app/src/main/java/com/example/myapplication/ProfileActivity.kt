package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileAvatar: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileNameData: TextView
    private lateinit var profileEmailData: TextView
    private lateinit var profilePhoneData: TextView
    private lateinit var db: DatabaseHelper

    // Launcher para seleccionar foto de la galería
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Persistir permiso para leer la URI después de reiniciar
            contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            UserSession.savePhotoUri(this, it.toString())
            loadAvatar(it.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DatabaseHelper(this)

        profileAvatar    = findViewById(R.id.profileAvatar)
        profileName      = findViewById(R.id.profileName)
        profileEmail     = findViewById(R.id.profileEmail)
        profileNameData  = findViewById(R.id.profileNameData)
        profileEmailData = findViewById(R.id.profileEmailData)
        profilePhoneData = findViewById(R.id.profilePhoneData)

        val logoutButton   = findViewById<Button>(R.id.logoutButton)
        val adminButton    = findViewById<Button>(R.id.adminButton)
        val ordersRecycler = findViewById<RecyclerView>(R.id.ordersRecyclerView)
        val emptyOrdersText = findViewById<TextView>(R.id.emptyOrdersText)
        val bottomNav      = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val changePhotoBtn = findViewById<ImageView>(R.id.changePhotoButton)
        val editNameBtn    = findViewById<ImageView>(R.id.editNameBtn)
        val editPhoneBtn   = findViewById<ImageView>(R.id.editPhoneBtn)

        BottomNavHelper.apply(bottomNav)

        // Cargar datos del usuario
        refreshUserData()

        // Cargar foto de perfil guardada
        val savedUri = UserSession.getPhotoUri(this)
        if (savedUri.isNotEmpty()) loadAvatar(savedUri)

        // Cambiar foto de perfil
        val openGallery = { pickImage.launch("image/*") }
        changePhotoBtn.setOnClickListener { openGallery() }
        profileAvatar.setOnClickListener  { openGallery() }

        // Editar nombre
        editNameBtn.setOnClickListener {
            showEditDialog(
                title   = "Editar nombre",
                current = UserSession.getName(this),
                hint    = "Nuevo nombre",
                inputType = android.text.InputType.TYPE_CLASS_TEXT or
                            android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
            ) { newValue ->
                val userId = UserSession.getUserId(this)
                if (db.updateUserName(userId, newValue)) {
                    UserSession.updateName(this, newValue)
                    refreshUserData()
                    Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Editar teléfono
        editPhoneBtn.setOnClickListener {
            showEditDialog(
                title   = "Editar teléfono",
                current = UserSession.getPhone(this),
                hint    = "Nuevo teléfono",
                inputType = android.text.InputType.TYPE_CLASS_PHONE
            ) { newValue ->
                val userId = UserSession.getUserId(this)
                if (db.updateUserPhone(userId, newValue)) {
                    UserSession.updatePhone(this, newValue)
                    refreshUserData()
                    Toast.makeText(this, "Teléfono actualizado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Historial de pedidos
        val userId = UserSession.getUserId(this)
        val orders = if (userId != -1) db.getOrdersForUser(userId) else emptyList()
        if (orders.isEmpty()) {
            emptyOrdersText.visibility = View.VISIBLE
            ordersRecycler.visibility  = View.GONE
        } else {
            emptyOrdersText.visibility = View.GONE
            ordersRecycler.visibility  = View.VISIBLE
            ordersRecycler.layoutManager = LinearLayoutManager(this)
            ordersRecycler.adapter = OrderAdapter(orders)
        }

        // Botón admin
        if (UserSession.isAdmin(this)) {
            adminButton.visibility = View.VISIBLE
            adminButton.setOnClickListener {
                startActivity(Intent(this, AdminActivity::class.java))
            }
        } else {
            adminButton.visibility = View.GONE
        }

        // Cerrar sesión
        logoutButton.setOnClickListener {
            UserSession.logout(this)
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        // Barra inferior
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, ProductCatalogActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    /** Refresca los TextViews con los datos actuales de sesión */
    private fun refreshUserData() {
        val name  = UserSession.getName(this)
        val email = UserSession.getEmail(this)
        val phone = UserSession.getPhone(this)

        profileName.text      = name
        profileEmail.text     = email
        profileNameData.text  = name
        profileEmailData.text = email
        profilePhoneData.text = if (phone.isNotEmpty()) phone else "No especificado"
    }

    /** Carga la foto de perfil desde la URI guardada */
    private fun loadAvatar(uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            val stream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(stream)
            stream?.close()
            profileAvatar.setImageBitmap(bitmap)
            profileAvatar.setPadding(0, 0, 0, 0)
            profileAvatar.scaleType = ImageView.ScaleType.CENTER_CROP
        } catch (e: Exception) {
            // Si falla, mantiene el icono por defecto
        }
    }

    /** Muestra un diálogo de edición con un campo de texto */
    private fun showEditDialog(
        title: String,
        current: String,
        hint: String,
        inputType: Int,
        onSave: (String) -> Unit
    ) {
        val editText = EditText(this).apply {
            setText(current)
            this.hint      = hint
            this.inputType = inputType
            setPadding(48, 32, 48, 8)
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val value = editText.text.toString().trim()
                if (value.isNotEmpty()) onSave(value)
                else Toast.makeText(this, "El campo no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
