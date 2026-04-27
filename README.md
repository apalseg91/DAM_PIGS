# FitManage  
Aplicación de escritorio para la gestión de centros deportivos

---

## Descripción

FitManage es una aplicación de escritorio desarrollada en Java que permite gestionar de forma integral un centro deportivo. Facilita la administración de clientes, actividades, reservas y generación de informes, proporcionando una solución completa tanto para administradores como para empleados.

El sistema ha sido diseñado siguiendo buenas prácticas de arquitectura software, priorizando la escalabilidad, mantenibilidad y separación de responsabilidades.

---

## Características principales

- Gestión de clientes (altas, bajas, modificaciones)
- Gestión de actividades deportivas
- Sistema de reservas con control de aforo
- Prevención de reservas duplicadas
- Control de estado de pago de clientes
- Generación de informes con JasperReports
- Sistema de autenticación de usuarios
- Arquitectura modular basada en MVC

---

## Arquitectura

La aplicación sigue una arquitectura basada en el patrón MVC (Modelo-Vista-Controlador), combinada con una organización en capas:

- Model: Entidades del sistema (Cliente, Actividad, Reserva, etc.)
- DAO: Acceso a datos y persistencia en base de datos
- Service: Lógica de negocio
- Controller: Interacción entre vista y lógica
- View: Interfaz gráfica desarrollada en Swing

---

## Tecnologías utilizadas

- Java 8  
- Swing (interfaz gráfica)  
- Oracle Database  
- JDBC  
- JasperReports  
- Docker (para la base de datos)  
- Maven / Ant (según configuración)

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/fitmanage.git
cd fitmanage
```
## Instalación y ejecución

### 2. Levantar la base de datos con Docker
```bash
docker-compose up -d
```
Para comprobar que la base de datos está lista:
```bash
docker logs -f oracle-fitmanage
```
Cuando aparezca el mensaje:

DATABASE IS READY TO USE!

La base de datos estará completamente inicializada y lista para conexiones.

### 3. Ejecutar la aplicación
```bash
java -jar FitManage-fat.jar
```

## Pruebas

Se han implementado pruebas unitarias utilizando JUnit, cubriendo la lógica de negocio en los servicios.

Ejemplos de validaciones testeadas:

- Creación de actividad con datos inválidos
- Control de reservas duplicadas
- Validación de clientes inexistentes

---

## Informes

Los informes se generan mediante JasperReports, utilizando plantillas `.jasper` precompiladas para mejorar el rendimiento.

La lógica de generación está encapsulada en un servicio independiente siguiendo el patrón MVC.

---

## Decisiones técnicas

- Se utiliza Java 8 para garantizar compatibilidad con iReport 5.6.0
- Las consultas SQL están construidas mediante concatenación clásica debido a esta limitación tecnológica
- La integridad referencial se gestiona en la base de datos (FK con ON DELETE CASCADE)

---

## Casos destacables

- Control de aforo máximo en actividades
- Prevención de reservas duplicadas mediante lógica y base de datos
- Reutilización de usuarios mediante trigger en base de datos
- Gestión de errores controlados (por ejemplo, aforo lleno o cliente sin pagar)

---

## Estructura del proyecto

``` id="jz7x2r"
src/
 ├── model/
 ├── dao/
 ├── service/
 ├── controller/
 ├── view/
 └── util/
```
## Autor

Alejandro Palomeque Segura
CFGS Desarrollo de Aplicaciones Multiplataforma (DAM)

## Licencia

Este proyecto se desarrolla con fines educativos.
