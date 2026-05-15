# MangUP — App Android

Aplicación Android de ecommerce temático de anime y manga, desarrollada en **Kotlin**. Permite registrar usuarios, iniciar sesión, explorar un catálogo de productos, ver fichas de detalle, gestionar el carrito, finalizar compras, consultar pedidos y dejar reseñas.

## Descripción general

MangUP simula una tienda online especializada en productos relacionados con el universo anime/manga:

- **Mangas**
- **Figuras**
- **Merchandising**

Además del flujo de compra para usuarios, la aplicación incorpora un **panel de administración** para gestionar el inventario de productos.

## Funcionalidades principales

### Autenticación y sesión

- Inicio de sesión con correo y contraseña
- Registro de nuevos usuarios con nombre, correo, teléfono y contraseña
- Auto-login si existe una sesión válida
- Cierre de sesión desde el perfil
- Persistencia de sesión mediante `SharedPreferences`

> La sesión activa se guarda en `UserSession.kt`, mientras que los datos de usuarios, productos, pedidos y reseñas se almacenan en SQLite.

### Catálogo de productos

- Visualización en **grid de 2 columnas**
- **Búsqueda en tiempo real** por nombre, descripción y categoría
- **Filtros por categoría**: Todos / Manga / Figuras / Merchandising
- Estado vacío cuando no hay resultados
- Acceso a la pantalla de detalle del producto
- Contador de productos en carrito

### Detalle de producto

- Imagen del producto
- Nombre, categoría, precio, stock y descripción
- Añadir al carrito desde la ficha de detalle
- Visualización de reseñas
- Creación o edición de reseñas por parte de usuarios que ya compraron el producto

### Carrito de compra

- Añadir productos al carrito
- Modificar cantidades y eliminar artículos
- Cálculo dinámico del total
- Finalizar compra y guardar el pedido en el historial del usuario

### Perfil de usuario

- Visualización de nombre, correo y teléfono
- Edición de nombre y teléfono
- Imagen de perfil desde galería
- Historial de pedidos con fecha, descripción, total y estado
- Cierre de sesión

### Administración

- Acceso al panel de administración para usuarios con rol admin
- Alta de productos
- Edición de productos
- Eliminación de productos
- Listado de inventario

### Navegación

- `BottomNavigationView` en Catálogo, Carrito y Perfil
- Flujo principal: **Login → Registro / Catálogo → Detalle → Carrito → Perfil**

## Tecnologías utilizadas

- **Kotlin**
- **Android SDK**
  - `minSdk = 24`
  - `targetSdk = 36`
  - `compileSdk = 36`
- **Material Design 3**
- **AndroidX**
  - Core KTX
  - AppCompat
  - Activity
  - ConstraintLayout
  - Lifecycle ViewModel
- **SQLite** mediante `SQLiteOpenHelper`
- **Gradle Kotlin DSL**
- **Version Catalog** con `gradle/libs.versions.toml`

## Dependencias principales

Definidas en `gradle/libs.versions.toml`:

- `com.google.android.material:material:1.13.0`
- `androidx.appcompat:appcompat:1.7.1`
- `androidx.core:core-ktx:1.17.0`
- `androidx.activity:activity:1.12.4`
- `androidx.constraintlayout:constraintlayout:2.2.1`
- `androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0`

## Requisitos

- **Android Studio**
- **JDK 11**
- Emulador o dispositivo físico con **Android 7.0 o superior**

## Compilar y ejecutar

1. Abrir el proyecto en **Android Studio**
2. Sincronizar Gradle
3. Ejecutar la app en un emulador o dispositivo físico

Para generar un APK de release:

```text
Build > Generate Signed App Bundle / APK
```

## Estructura principal del proyecto

```text
app/src/main/
├── AndroidManifest.xml
├── java/com/example/myapplication/
│   ├── AdminActivity.kt
│   ├── AdminProductAdapter.kt
│   ├── BottomNavHelper.kt
│   ├── CartActivity.kt
│   ├── CartAdapter.kt
│   ├── CartManager.kt
│   ├── CartProduct.kt
│   ├── DatabaseHelper.kt
│   ├── LoginActivity.kt
│   ├── Order.kt
│   ├── OrderAdapter.kt
│   ├── OrderItem.kt
│   ├── Product.kt
│   ├── ProductAdapter.kt
│   ├── ProductCatalogActivity.kt
│   ├── ProductDetailActivity.kt
│   ├── ProductRepository.kt
│   ├── ProductViewModel.kt
│   ├── ProfileActivity.kt
│   ├── RegisterActivity.kt
│   ├── Review.kt
│   ├── ReviewAdapter.kt
│   ├── User.kt
│   └── UserSession.kt
├── res/
│   ├── color/
│   ├── drawable/
│   ├── layout/
│   ├── menu/
│   ├── mipmap/
│   ├── values/
│   └── xml/
└── assets/images/
```

## Persistencia de datos

La aplicación utiliza una base de datos local SQLite gestionada desde `DatabaseHelper.kt`.

### Tablas principales

- `users`
- `products`
- `orders`
- `order_items`
- `reviews`

### Datos iniciales

La base de datos inserta automáticamente:

- Productos precargados
- Un usuario administrador por defecto

### Usuario administrador por defecto

- **Email:** `admin@mangup.com`
- **Contraseña:** `admin123`

## Actividades declaradas en el manifiesto

En `app/src/main/AndroidManifest.xml` se encuentran registradas las siguientes pantallas:

- `LoginActivity`
- `RegisterActivity`
- `ProductCatalogActivity`
- `CartActivity`
- `ProfileActivity`
- `AdminActivity`
- `ProductDetailActivity`

## Recursos multimedia

Las imágenes de productos se cargan principalmente desde:

- `app/src/main/assets/images/`

Y los recursos visuales de la interfaz se encuentran en:

- `app/src/main/res/drawable/`
- `app/src/main/res/mipmap/`
- `app/src/main/res/layout/`

## Notas

- La app funciona con almacenamiento local y no depende de un backend remoto.
- El nombre visible del proyecto se presenta como **MangUP**, aunque en `settings.gradle.kts` el nombre raíz aún aparece como `"My Application"`.
- La arquitectura del proyecto está basada en Activities, adapters y persistencia SQLite local.

## Trabajo realizado por alumnos de Ilerna

- Marco Antonio Cardo Caballero
- Luis Capel Velázquez
- Mario Sanchez Ruiz
- Lorenzo Cruz Fernandez
