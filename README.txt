Consideraciones: 
JDK 17
Tiene que tener Maven instalado y sus dependencias.

Correr en la terminal:

EdutechAPI/EdutechAPI: mvn clean install

EdutechAPI/EdutechAPI: mvn spring-boot:run

DEMO de uso para las API's
-------------------------------------------------------------------------
Usuarios: <--

GET
Obtener todos los usuarios:
http://localhost:8080/api/usuarios

Obtener usuario por id:
http://localhost:8080/api/usuarios/{id}

Obtener usuario por email:
http://localhost:8080/api/usuarios/email/{email}

POST:
http://localhost:8080/api/usuarios
{
    "idUsuario": 1, // El usuario lo debe colocar, utilizar uno que este disponible. Me asegure que el 1 lo este     
    "nombre": "Jon Snow",
    "apellido": "Jon Snow",
    "email": "jon.snow@example.com", // Debe ser unico
    "contrasena": "hash_password_1",
    "estado": "inactivo",
    "idOficina": 105
}

PUT:
http://localhost:8080/api/usuarios/{id}
Body para actualizar: solo se pueden actualizar estos datos, la contraseña pertenece a otro servicio y el ID por obvios motivos no se puede modificar
{             
    "nombre": "Aegon Targaryen",          
    "apellido": "Aegon Targaryen",  
    "email": "aegon.targaryen@example.com",  
    "estado": "activo",             
    "idOficina": 101              
}

DELETE (solo con URL) 
(No borrar el ID de Jon Snow para poder usar el easter egg más abajo.)
http://localhost:8080/api/usuarios/{id}

MANEJO DE ROLES DEL USUARIO (misma API Usuario) No hay PUT, se pueden colocar 1 o mas roles a la vez, no repetidos.
GET
http://localhost:8080/api/usuarios/{id}/roles

DELETE
http://localhost:8080/api/usuarios/{id}/roles
Body para poder borrar: 
{
    "nombreRol": "Estudiante"
}

POST (pueden ser todas incluso pero no repetidas)
http://localhost:8080/api/usuarios/{id}/roles
{
    "nombreRol": "Lord Commander of the Night's Watch" // Estudiante, Profesor, Administrador y Lord Commander of the Night's Watch son los roles disponibles. Son case sensitive
}
(En el GET http://localhost:8080/api/usuarios/{id} puede ver toda la información completa, con rol incluido).
---------------------------------------------------------------------------------------------------

Cursos
GET
http://localhost:8080/api/cursos

Tambien por ID

POST
http://localhost:8080/api/cursos
Body
{
        
	// ID AUTOGENERADO POR LA BASE DE DATOS
        "nombreCurso": "Introduccion a Spring Boot Avanzado",
        "descripcion": "Aprende a construir microservicios robustos con Spring Boot y Spring Cloud.",
        // FECHA GENERADA AUTOMATICAMENTE EN BD CON SYSDATE
        "estado": "activo"
}

GET por ID
http://localhost:8080/api/cursos/{id}

PUT
http://localhost:8080/api/cursos/{id}
Body
{
    "nombreCurso": "Python para uso en Ciencias de Datos",
    "descripcion": "Uso de Python para ciencia de datos",
    "estado": "activo" // O inactivo. Solo pueden ser esos dos
}

DELETE 
He decidido no hacer un delete de los cursos, ya que es una informacion que puede ser relevante mantener, solo se puede cambiar el estado de "activo" a "inactivo", case sensitive


-----------------------------------------------------------------------------

INSCRIPCION

GET
TODOS
http://localhost:8080/api/inscripciones
Por ID de curso
http://localhost:8080/api/inscripciones/curso/{id}
Por ID de usuario
http://localhost:8080/api/inscripciones/usuario/{id}


POST
http://localhost:8080/api/inscripciones
Body: Consideracion, un usuario se puede inscribir en varios cursos, pero no dos veces en el mismo.
{
    	"idUsuario": 4,  // ID del usuario que se inscribe
    	"idCurso": 15    // ID del curso en el que se inscribe
	"estado": "en curso" // O "completado"
}
PUT ESTADO
Prueba de funcionalidad: Actualización de estado de inscripción a través de URL: solo dos (en curso) (completado)
http://localhost:8080/api/inscripciones/{id}/estado?nuevoEstado=en curso

PUT para cambiar los datos de inscripcion
http://localhost:8080/api/inscripciones/17 
Body:
{
    	"idUsuario": 4,  // ID del usuario que se inscribe
    	"idCurso": 16    // ID del curso en el que se inscribe
	"estado": "en curso" // O "completado"
}

DELETE
Solo por URL:
http://localhost:8080/api/inscripciones/22


