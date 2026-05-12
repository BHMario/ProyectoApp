# MangUP — App Android

Aplicación Android de ecommerce temático de anime/manga. Desarrollada en **Kotlin** con Material Design 3.

## Compilar y ejecutar

1. Abrir el proyecto en **Android Studio**
2. Sincronizar Gradle
3. Ejecutar en emulador o dispositivo físico (Android 7.0+)

Para generar APK de release:
```
Build > Generate Signed App Bundle / APK
```

## Estructura del proyecto

```
app/src/main/
├── java/com/example/myapplication/
│   ├── LoginActivity.kt          # Pantalla de inicio de sesión
│   ├── RegisterActivity.kt       # Pantalla de registro
│   ├── ProductCatalogActivity.kt # Catálogo con búsqueda y filtros
│   ├── CartActivity.kt           # Carrito de compras
│   ├── ProfileActivity.kt        # Perfil e historial de pedidos
│   ├── UserSession.kt            # Gestión de sesión (SharedPreferences)
│   ├── Product.kt                # Modelo de producto
│   ├── CartProduct.kt            # Modelo de ítem del carrito
│   ├── Order.kt                  # Modelo de pedido
│   ├── ProductAdapter.kt         # Adaptador del catálogo
│   ├── CartAdapter.kt            # Adaptador del carrito
│   └── OrderAdapter.kt           # Adaptador del historial de pedidos
└── res/
    ├── layout/                   # Layouts de pantallas e ítems
    ├── drawable/                 # Iconos y fondos vectoriales
    ├── menu/                     # Menú de bottom navigation
    ├── color/                    # Selectores de color
    └── values/                   # Colores, temas y strings
```

## Tecnologías

- **Kotlin**
- **Android SDK** — minSdk 24 / targetSdk 36
- **Material Design 3** (`com.google.android.material:material:1.13.0`)
- **AndroidX** (AppCompat, RecyclerView, ConstraintLayout, Activity)
- Gradle con Version Catalog (`libs.versions.toml`)

## Características

### Autenticación
- **Login** con correo y contraseña (persistencia con SharedPreferences)
- **Registro** de nuevos usuarios con nombre, correo, teléfono y contraseña
- Auto-login si ya existe sesión activa
- Cierre de sesión desde el perfil

### Catálogo de productos
- Grid de productos en 2 columnas
- **Barra de búsqueda** en tiempo real (filtra por nombre, descripción y categoría)
- **Filtros por categoría**: Todos / Manga / Figuras / Merchandising
- Estado vacío cuando no hay resultados

### Carrito de compras
- Añadir/eliminar productos y modificar cantidades
- Cálculo dinámico del total
- **Finalizar compra** guarda el pedido en el historial del usuario

### Perfil de usuario
- Visualización de nombre, correo y teléfono
- **Historial de pedidos** con fecha, descripción, total y estado
- Botón de cerrar sesión

### Navegación
- **Bottom Navigation Bar** presente en Catálogo, Carrito y Perfil
- Flujo completo: Login → Registro / Catálogo → Carrito → Perfil


## Trabajo realizado por alumnos de Ilerna

Marco Antonio Cardo Caballero, Luis Capel Velázquez , Mario Sanchez Ruiz y Lorenzo Cruz Fernandez.