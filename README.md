# BibliotecaEFT — Sistema de Gestión de Biblioteca Escolar

Aplicación de escritorio desarrollada en **Java 26**, utilizando **Java Swing** para la interfaz gráfica y **MySQL** para la persistencia de datos.

El sistema permite administrar libros, categorías, estudiantes, préstamos y devoluciones, incorporando autenticación de usuarios, permisos por rol y reportes de gestión.

## Funcionalidades

### Bibliotecario
- Inicio de sesión.
- Gestión de libros: crear, consultar, actualizar y eliminar.
- Gestión de categorías: crear, consultar, actualizar y eliminar.
- Gestión de estudiantes y sus cuentas de usuario.
- Registro de préstamos y devoluciones.
- Consulta de libros disponibles.
- Reportes de libros más prestados.
- Historial de préstamos por estudiante.
- Reportes de préstamos activos y atrasados.

### Estudiante
- Inicio de sesión.
- Consulta del catálogo de libros.
- Registro de préstamos y devoluciones según sus permisos.
- Visualización de sus préstamos pendientes de devolución.

## Tecnologías utilizadas

- Java 26
- Java Swing
- MySQL
- JDBC
- Maven
- MySQL Connector/J 9.5.0
- Maven Shade Plugin
- Git y GitHub

## Arquitectura del proyecto

El sistema organiza sus responsabilidades mediante una estructura basada en MVC, DAO y servicios.

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

**Modelo:** representa las entidades del sistema y sus comportamientos.

**Vista:** contiene las ventanas, formularios y tablas implementadas con Java Swing.

**Controlador:** coordina las acciones entre la interfaz y la lógica del sistema.

**DAO:** encapsula las operaciones SQL y el acceso a MySQL mediante JDBC.

**Servicio:** contiene la lógica de negocio para operaciones como préstamos, devoluciones y registro de estudiantes.

**Util:** incluye funcionalidades auxiliares, como el tratamiento seguro de contraseñas.

### Principios y patrones implementados

- Encapsulamiento.
- Herencia y clases abstractas.
- Polimorfismo.
- Interfaces.
- Patrón Singleton para la configuración de conexión.
- Patrón DAO para persistencia.
- Transacciones JDBC.
- Control de concurrencia en operaciones de préstamos.
- Operaciones asíncronas mediante `SwingWorker`.

## Base de datos

La aplicación utiliza una base de datos MySQL llamada `biblioteca`.

Tablas principales:

- `usuarios`
- `estudiantes`
- `libros`
- `categorias`
- `prestamos`

Los scripts SQL de creación y carga de datos se encuentran en la carpeta `scripts/`.

## Requisitos

- JDK 26.
- MySQL Server.
- Maven, si se desea compilar desde el código fuente.
- Base de datos `biblioteca` creada mediante los scripts del proyecto.

## Instalación y ejecución

### 1. Preparar MySQL

Ejecutar los scripts SQL incluidos en `scripts/`, comenzando por la creación de la base de datos y continuando con la carga de datos iniciales.

La aplicación está configurada para conectarse a MySQL local mediante el usuario `root`.

### 2. Configurar la contraseña

La aplicación obtiene la contraseña de MySQL desde la variable de entorno:

`BIBLIOTECA_DB_PASSWORD`

En PowerShell:

```powershell
$env:BIBLIOTECA_DB_PASSWORD = Read-Host "Contraseña de MySQL"
```

No es necesario incorporar la contraseña al código fuente.

### 3. Compilar el proyecto

Desde la carpeta raíz:

```powershell
mvn clean package
```

Si Maven no está disponible en el PATH, se puede utilizar Maven desde IntelliJ IDEA.

### 4. Ejecutar la aplicación

Después de compilar, ejecutar:

```powershell
java -jar .\target\BibliotecaEFT.jar
```

También es posible iniciar el proyecto desde IntelliJ ejecutando la clase `main.Main`.

### 5. Credenciales de demostración

Después de importar los scripts SQL, puedes iniciar sesión con:

**Bibliotecario**                           
- Correo: `antonia@correo.cl`               
- Contraseña: `clave123`
- Permisos: administración de libros, categorías, estudiantes, préstamos, devoluciones y reportes.

**Estudiante**
- Correo: `carlos@correo.cl`
- Contraseña: `clave123`
- Permisos: consulta del catálogo, préstamos y devoluciones según las restricciones del sistema.  

Estas credenciales corresponden exclusivamente a los datos de prueba incluidos en el script de poblado.
Las credenciales en el correo de Estudiante varia según el usuario que se conecte.

**NOTA**: La contraseña de MySQL es independiente de estas credenciales y debe configurarse mediante la variable de entorno BIBLIOTECA_DB_PASSWORD.

## Seguridad y control de acceso

El sistema distingue entre los roles de **bibliotecario** y **estudiante**.

Las contraseñas de nuevas cuentas se almacenan mediante PBKDF2 con salt, y el sistema incorpora compatibilidad para migrar las contraseñas iniciales de prueba al iniciar sesión.

Las operaciones de préstamos y devoluciones validan los permisos del usuario y actualizan el stock mediante transacciones.

## Reportes

El módulo de reportes permite consultar:

1. Libros más prestados.
2. Historial de préstamos por estudiante.
3. Préstamos activos.
4. Préstamos atrasados.

## Ejecución y entrega

El proyecto incluye el código fuente Java, configuración Maven, scripts SQL y la posibilidad de generar un JAR ejecutable con sus dependencias.
