\# ReservaMS - Availability Service



\## Descripcion



Este microservicio administra la disponibilidad de habitaciones por fecha.



Permite saber si una habitacion esta disponible, reservada o bloqueada en un dia especifico.



\## Responsabilidades



\- Crear disponibilidad por habitacion.

\- Listar disponibilidad.

\- Buscar disponibilidad por habitacion.

\- Buscar disponibilidad por fecha.

\- Verificar disponibilidad en un rango de fechas.

\- Cambiar estado de disponibilidad.



\## Puerto



8085



\## Base de datos



reservams\_availability\_db



\## Endpoints principales



\- GET /api/v1/availability

\- GET /api/v1/availability/{id}

\- GET /api/v1/availability/room/{roomId}

\- GET /api/v1/availability/room/{roomId}/date/{date}

\- POST /api/v1/availability

\- POST /api/v1/availability/check

\- PUT /api/v1/availability/{id}

\- PUT /api/v1/availability/{id}/status/{status}



\## Ejecucion



1\. Crear la base de datos reservams\_availability\_db.

2\. Ejecutar el script SQL ubicado en la carpeta database.

3\. Levantar Eureka Server.

4\. Ejecutar el availability-service.

5\. Probar los endpoints desde Postman o desde el API Gateway.

