# Organización del trabajo Android

```text
com.example.movil
├── data
│   ├── books       # Persona 2: API, modelos y repositorio de libros
│   ├── cart        # Persona 3: API, modelos y repositorio del carrito
│   ├── model       # Persona 1: modelos compartidos de usuarios
│   ├── orders      # Persona 3: API, modelos y repositorio de pedidos
│   ├── remote      # Persona 1: Retrofit y servicios existentes
│   └── session     # Persona 1: persistencia de la sesión
├── navigation      # Persona 1: rutas y NavHost
└── ui
    ├── admin       # Persona 1
    ├── auth        # Persona 1
    ├── books       # Persona 2
    ├── cart        # Persona 3
    ├── home        # Persona 1
    ├── orders      # Persona 3
    ├── profile     # Persona 1
    └── theme       # Compartido; coordinar cambios
```

Cada módulo nuevo debe contener sus modelos, servicio Retrofit, repositorio, ViewModel y
pantallas dentro de la carpeta asignada. `MainActivity.kt`, `Routes.kt`, `RetrofitClient.kt`
y `build.gradle.kts` pertenecen a la persona 1 para evitar conflictos de integración.
