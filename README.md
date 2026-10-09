# BibliotecaEFT — Sistema de Gestión de Biblioteca Escolar

Aplicación de escritorio desarrollada en **Java 26**, utilizando **Java Swing** para la interfaz gráfica, **MySQL 8.0** para la persistencia de datos y **JDBC** para la comunicación con la base de datos.

El sistema permite administrar libros, categorías, estudiantes, préstamos y devoluciones, incorporando autenticación de usuarios, permisos por rol, reportes de gestión y principios de programación orientada a objetos.

Este proyecto fue desarrollado como parte de una **Evaluación Final Transversal (EFT)**.

## Funcionalidades

### Bibliotecario

- Inicio de sesión mediante credenciales.
- Gestión de libros: crear, consultar, actualizar y eliminar.
- Gestión de categorías: crear, consultar, actualizar y eliminar.
- Gestión de estudiantes y sus cuentas de usuario.
- Registro de préstamos y devoluciones.
- Consulta del catálogo y disponibilidad de libros.
- Reportes de libros más prestados.
- Historial de préstamos por estudiante.
- Reportes de préstamos activos y atrasados.

### Estudiante

- Inicio de sesión mediante credenciales.
- Consulta del catálogo de libros.
- Registro de préstamos y devoluciones según los permisos establecidos.
- Consulta de préstamos pendientes de devolución.

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 26 | Lenguaje de programación |
| Java Swing | Interfaz gráfica de escritorio |
| MySQL 8.0 | Base de datos relacional |
| JDBC | Conexión y operaciones SQL |
| Maven | Gestión de dependencias y compilación |
| MySQL Connector/J 9.5.0 | Controlador JDBC para MySQL |
| Maven Shade Plugin | Generación del JAR ejecutable |
| Git y GitHub | Control de versiones |

## Arquitectura del proyecto

El sistema utiliza una arquitectura basada en **MVC (Modelo-Vista-Controlador)**, complementada con el patrón DAO y una capa de servicios.

```text
src/main/java/
├── main/
│   └── Main.java
├── modelo/
├── vista/
├── controlador/
├── dao/
├── servicio/
└── util/
```

### Responsabilidades de cada paquete

**Modelo (`modelo`):** representa las entidades del sistema, sus atributos y comportamientos.

**Vista (`vista`):** contiene las ventanas, formularios, paneles y tablas desarrollados mediante Java Swing.

**Controlador (`controlador`):** coordina las acciones de la interfaz con la lógica de negocio y el acceso a los datos.

**DAO (`dao`):** encapsula las operaciones SQL y la persistencia mediante JDBC.

**Servicio (`servicio`):** implementa la lógica de negocio, incluyendo préstamos, devoluciones y operaciones relacionadas con estudiantes.

**Util (`util`):** contiene funcionalidades auxiliares, como el tratamiento seguro de contraseñas.

**Main (`main`):** contiene el punto de entrada de la aplicación.

## Programación orientada a objetos y patrones de diseño

El proyecto incorpora los siguientes conceptos:

- **Encapsulamiento:** protección y administración de los atributos de las entidades.
- **Herencia:** reutilización de características comunes mediante clases relacionadas.
- **Abstracción:** utilización de clases abstractas para definir comportamientos compartidos.
- **Polimorfismo:** tratamiento de objetos mediante tipos y comportamientos comunes.
- **Interfaces:** definición de contratos, como `Prestable`.
- **Singleton:** administración centralizada de la configuración de conexión mediante `DatabaseConnection`.
- **DAO:** separación de las consultas SQL respecto de la lógica de la aplicación.
- **MVC:** organización de las responsabilidades del sistema.

### Concurrencia y persistencia

- Operaciones JDBC para consultar y modificar registros.
- Transacciones para mantener la consistencia de los datos.
- Validación de disponibilidad durante los préstamos.
- Actualización del stock al prestar y devolver libros.
- Mecanismos de sincronización para operaciones concurrentes.
- Uso de `SwingWorker` para ejecutar operaciones sin bloquear la interfaz gráfica.

## Base de datos

La aplicación utiliza una base de datos MySQL denominada `biblioteca`.

### Tablas principales

| Tabla | Descripción |
|---|---|
| `usuarios` | Credenciales y roles de acceso |
| `estudiantes` | Información de los estudiantes |
| `categorias` | Clasificación de libros |
| `libros` | Catálogo y stock disponible |
| `prestamos` | Registro de préstamos y devoluciones |

### Scripts SQL

Los archivos necesarios para preparar la base de datos se encuentran en la carpeta `scripts/`:

1. `PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql`
2. `PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql`

El primer script crea la base de datos y sus tablas. El segundo incorpora los datos iniciales de demostración.

Ambos scripts fueron comprobados en MySQL 8.0, verificando la creación de las cinco tablas y la inserción de 45 registros iniciales.

## Requisitos del sistema

Para ejecutar la aplicación se necesita:

- Java Development Kit (JDK) 26.
- MySQL Server 8.0.
- Base de datos `biblioteca` creada mediante los scripts SQL.
- Maven, únicamente si se desea compilar el proyecto desde el código fuente.

Para ejecutar el JAR ya compilado, no es necesario instalar Maven.

## Instalación y ejecución

### 1. Preparar MySQL

Iniciar MySQL Server y ejecutar los scripts SQL incluidos en la carpeta `scripts/`, respetando el siguiente orden:

1. Script de creación de tablas.
2. Script de poblado de tablas.

Estos scripts están preparados para utilizar la base de datos `biblioteca`.

**Importante:** deben ejecutarse sobre una instalación nueva o una base de datos preparada para la demostración. No se recomienda volver a ejecutar el script de poblado sobre una base que ya contiene esos registros.

### 2. Configurar la conexión

La aplicación está configurada para conectarse a MySQL local mediante:

- Servidor: `localhost`
- Puerto: `3306`
- Base de datos: `biblioteca`
- Usuario: `root`

La contraseña se obtiene desde la variable de entorno:

`BIBLIOTECA_DB_PASSWORD`

En PowerShell, ejecutar:

```powershell
$env:BIBLIOTECA_DB_PASSWORD = Read-Host "Contraseña de MySQL"
```

Ingresar la contraseña correspondiente al usuario `root` de MySQL.

Esta variable se configura para la sesión actual de PowerShell y evita incorporar la contraseña directamente en el código fuente.

### 3. Compilar el proyecto

Abrir una terminal en la carpeta raíz del proyecto y ejecutar:

```powershell
mvn clean package
```

Si Maven no está disponible en el PATH, puede utilizarse la distribución de Maven incluida en IntelliJ IDEA.

El proceso genera el archivo ejecutable:

`target/BibliotecaEFT.jar`

### 4. Ejecutar la aplicación

Desde la carpeta raíz del proyecto, ejecutar:

```powershell
java -jar .\target\BibliotecaEFT.jar
```

Si se utiliza el JAR incluido en el ZIP de entrega y se encuentra en la misma carpeta que la terminal, ejecutar:

```powershell
java -jar .\BibliotecaEFT.jar
```

También es posible ejecutar el proyecto desde IntelliJ IDEA mediante la clase `main.Main`, configurando previamente la variable de entorno de MySQL.

### 5. Credenciales de demostración

Después de importar los scripts SQL, se puede iniciar sesión con las siguientes cuentas:

**Bibliotecario**

- Correo: `antonia@correo.cl`
- Contraseña: `clave123`
- Permisos: administración de libros, categorías, estudiantes, préstamos, devoluciones y reportes.

**Estudiante**

- Correo: `carlos@correo.cl`
- Contraseña: `clave123`
- Permisos: consulta del catálogo, préstamos y devoluciones según las restricciones del sistema.

Cada estudiante utiliza su propio correo electrónico registrado en la base de datos para iniciar sesión. El correo `carlos@correo.cl` corresponde únicamente a una cuenta de demostración.

Estas credenciales forman parte de los datos iniciales incluidos en el script de poblado.

**Nota:** La contraseña de MySQL es independiente de las credenciales de acceso a la aplicación y debe configurarse mediante la variable de entorno `BIBLIOTECA_DB_PASSWORD`.

## Seguridad y control de acceso

El sistema distingue entre dos roles:

- **Bibliotecario:** acceso a las funciones administrativas y a los reportes.
- **Estudiante:** acceso limitado a las funciones habilitadas para su rol.

Las contraseñas de nuevas cuentas se almacenan mediante **PBKDF2 con salt**. Además, el sistema incorpora compatibilidad para migrar las contraseñas iniciales de demostración al iniciar sesión.

Las operaciones de préstamos y devoluciones validan los permisos del usuario y utilizan transacciones para mantener la consistencia de los datos.

## Reportes disponibles

El módulo de reportes permite consultar:

1. **Libros más prestados:** identifica los títulos con mayor cantidad de préstamos.
2. **Historial por estudiante:** muestra los préstamos asociados a un estudiante.
3. **Préstamos activos:** presenta los préstamos que aún no han sido devueltos.
4. **Préstamos atrasados:** identifica préstamos cuya fecha de devolución ya venció.

Los reportes utilizan consultas SQL sobre la información almacenada en MySQL.

## Compilación y entrega

El proyecto incluye