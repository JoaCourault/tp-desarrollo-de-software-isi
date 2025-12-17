# tp-desarrollo-de-software-isi
Sistema de Gestión Hotelera – Guía de Uso

Este proyecto corresponde a un Sistema de Gestión Hotelera, desarrollado como trabajo práctico, que permite administrar huéspedes, habitaciones, reservas, estadías y facturación mediante una arquitectura backend + frontend.

🧩 Estructura del Proyecto

El sistema está compuesto por dos partes principales:

Backend: desarrollado en Java con Spring Boot

Frontend: desarrollado con JavaScript utilizando un framework frontend (ejecutado con npm)

Ambas partes se ejecutan de forma independiente pero se comunican entre sí.

🚀 Cómo ejecutar el sistema
1️⃣ Backend (Java – Spring Boot)

Abrir el proyecto backend en IntelliJ IDEA (o cualquier IDE compatible con Java).

Asegurarse de estar en la rama develop.

Ejecutar la aplicación desde el IDE:

Ejecutar la clase principal de Spring Boot

O utilizar el botón Run de IntelliJ

Esto levantará el backend y dejará disponibles los servicios necesarios para el frontend.

2️⃣ Frontend (Node / npm)

Abrir una terminal en el directorio del proyecto frontend.

Asegurarse de estar en la rama origin/develop.

Ejecutar el siguiente comando:

npm run dev


Una vez iniciado, el frontend quedará disponible en el navegador.

🌐 Acceso al sistema

Con el backend y frontend en ejecución, ingresar desde el navegador a:

http://localhost:3000


Desde esta interfaz gráfica se puede acceder a todos los Casos de Uso del sistema, tales como:

Gestión de huéspedes

Gestión de habitaciones

Reservas

Estadías

Facturación

✅ Notas importantes

Ambos servicios (backend y frontend) deben estar ejecutándose simultáneamente.

El sistema está preparado para ejecutarse en entorno local.

La lógica de negocio se encuentra implementada en la capa de servicios del backend.

El proyecto incluye tests unitarios en la capa de servicio, ejecutables mediante Gradle y Jacoco.
