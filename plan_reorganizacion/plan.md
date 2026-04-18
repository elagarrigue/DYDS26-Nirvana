# Proyecto: Plan de Reorganización de Estructura

Este plan describe cómo reorganizar el proyecto para lograr una arquitectura limpia y escalable, siguiendo las capas de presentación, dominio, datos y dependencia.

---

## 1. Capa de Presentación (`presentation`)
- Crear el paquete `presentation`.
- Dentro, crear subpaquetes `home` y `detail` para cada pantalla.
- Mover `HomeScreen`, `DetailScreen` y composables relacionados a sus respectivos subpaquetes.
- Crear un subpaquete `utils` para componentes reutilizables de UI (por ejemplo, `CommonComposables`).
- Mover las clases de estado de UI y definiciones de eventos al subpaquete de presentación correspondiente.

## 2. Capa de Dominio (`domain`)
- Crear el paquete `domain`.
- Dentro, crear:
  - Subpaquete `entity`: Mover `Movie`, `QualifiedMovie` y modelos de dominio relacionados aquí.
  - Subpaquete `repository`: Definir la interfaz `MoviesRepository` que especifique el contrato para operaciones de datos.
  - Subpaquete `usecase`: Implementar clases de casos de uso (por ejemplo, `GetPopularMovies`, `GetMovieDetail`) que encapsulen la lógica de negocio e interactúen con el repositorio.

## 3. Capa de Datos (`data`)
- Crear el paquete `data`.
- Implementar fuentes de datos:
  - Para datos remotos (por ejemplo, API TMDB), crear un subpaquete `remote`.
  - Para datos locales (si es necesario), crear un subpaquete `local`.
- Implementar clases concretas para la interfaz `MoviesRepository` (por ejemplo, `RemoteMoviesRepository`, `LocalMoviesRepository`).
- Manejar el mapeo de datos entre modelos remotos/locales y entidades de dominio.

## 4. Inyección de Dependencias (`di`)
- Crear el paquete `di`.
- Mover la lógica de inyección de dependencias (por ejemplo, `MoviesDependencyInjector`) aquí.
- Configurar proveedores para ViewModels, repositorios, casos de uso y fuentes de datos.

## 5. Refactorización de ViewModels
- Mover los ViewModels a la capa de presentación, en el paquete de pantalla correspondiente.
- Refactorizar los ViewModels para depender de casos de uso del dominio, no directamente de fuentes de datos.

## 6. Actualizar Navegación y Punto de Entrada
- Actualizar `Navigation` y `App` para usar la nueva estructura de paquetes y ubicaciones de ViewModel.

## 7. Actualizar Imports y Usos
- Gradualmente actualizar imports y usos en todo el proyecto para coincidir con la nueva estructura.

---

Este plan te permitirá lograr una clara separación de responsabilidades, facilitando la escalabilidad y el mantenimiento del proyecto.
