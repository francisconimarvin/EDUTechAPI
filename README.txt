   ___      ___      _  _     ___     ___     ___      ___     ___       _       _____     ___      ___      _  _     ___ 
  / __|    / _ \    | \| |   / __|   |_ _|   |   \    | __|   | _ \     /_\     |_   _|   |_ _|    / _ \    | \| |   / __|
 | (__    | (_) |   | .` |   \__ \    | |    | |) |   | _|    |   /    / _ \      | |      | |    | (_) |   | .` |   \__ \
  \___|    \___/    |_|\_|   |___/   |___|   |___/    |___|   |_|_\   /_/ \_\     |_|     |___|    \___/    |_|\_|   |___/
                                                                                                                                                                                                                          
Le dejo este archivo para que tenga algunas consideraciones para que pueda funcionar el proyecto.

    _                     
   (_)  __ _  __ __  __ _ 
   | | / _` | \ V / / _` |
  _/ | \__,_|  \_/  \__,_|
 |__/                     

Versión de Java: 
-> openjdk 17.0.15 2025-04-15
-> OpenJDK Runtime Environment (build 17.0.15+6)
-> OpenJDK 64-Bit Server VM (build 17.0.15+6, mixed mode, sharing)
                                        
                                         _ 
  _ __   ___   _ __       __ __  _ __   | |
 | '_ \ / _ \ | '  \   _  \ \ / | '  \  | |
 | .__/ \___/ |_|_|_| (_) /_\_\ |_|_|_| |_|
 |_|                                       

-> Revisar el archivo pom.xml y de tener las extensiones necesarias para poder correr sus dependencias <-



  _   _   ___   ___   ___       _     _  _   ___      ___     _     ___   ___  __      __   ___    ___   ___    ___ 
 | | | | / __| | __| | _ \     /_\   | \| | |   \    | _ \   /_\   / __| / __| \ \    / /  / _ \  | _ \ |   \  / __|
 | |_| | \__ \ | _|  |   /    / _ \  | .` | | |) |   |  _/  / _ \  \__ \ \__ \  \ \/\/ /  | (_) | |   / | |) | \__ \
  \___/  |___/ |___| |_|_\   /_/ \_\ |_|\_| |___/    |_|   /_/ \_\ |___/ |___/   \_/\_/    \___/  |_|_\ |___/  |___/
                                                                                                                    
Para poder utilizar la API, debe utilizar uno de los siguientes usuarios (hay 49 en total pero le dejaré algunos ejemplos solo por no colocarlos todos, cualquier sirve):

	U S E R							P A S S W O R D 
-> maria.fernanda.gomez@example.com     $2a$10$IOT/O4JA918359YrvIwNbOxWTumnBU16/19gw12rn8sNUx9nCVhaG
-> ana.rodriguez@example.com            $2a$10$IOT/O4JA918359YrvIwNbOxWTumnBU16/19gw12rn8sNUx9nCVhaG
-> pedro.martinez@example.com           $2a$10$IOT/O4JA918359YrvIwNbOxWTumnBU16/19gw12rn8sNUx9nCVhaG
-> laura.hernandez@example.com          $2a$10$IOT/O4JA918359YrvIwNbOxWTumnBU16/19gw12rn8sNUx9nCVhaG
-> david.garcia@example.com             $2a$10$IOT/O4JA918359YrvIwNbOxWTumnBU16/19gw12rn8sNUx9nCVhaG
-> sofia.diaz@example.com               $2a$10$IOT/O4JA918359YrvIwNbOxWTumnBU16/19gw12rn8sNUx9nCVhaG
-> fernando.ruiz@example.com            $2a$10$IOT/O4JA918359YrvIwNbOxWTumnBU16/19gw12rn8sNUx9nCVhaG
-> elena.torres@example.com             $2a$10$IOT/O4JA918359YrvIwNbOxWTumnBU16/19gw12rn8sNUx9nCVhaG
-> gabriel.ramirez@example.com          $2a$10$IOT/O4JA918359YrvIwNbOxWTumnBU16/19gw12rn8sNUx9nCVhaG
-> isabel.flores@example.com            $2a$10$IOT/O4JA918359YrvIwNbOxWTumnBU16/19gw12rn8sNUx9nCVhaG

Como puede ver, las passwords están encriptadas, la contraseña que debe utilizar para poder acceder es:
--> Password123

Un ejemplo del  cuerpo del json para poder entrar es el siguiente:		
	
	{
    	  "email": "maria.fernanda.gomez@example.com",
    	  "password": "Password123"
	}

En Postman debería entrar como:
-> Body -> raw -> json

Usando la siguiente URL en método POST:
-> http://localhost:8080/auth/login

Debería devolverle el cuerpo con el token JWT encriptado en AES con un cuerpo así:
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXJpYS5mZXJuYW5kYS5nb21lekBleGFtcGxlLmNvbSIsImlhdCI6MTc1MTg0Mzg4NCwiZXhwIjoxNzUxODc5ODg0fQ.0d6Z4cKrbi6gaFWRgDTODHevYlHb4ZhjtiZo9AbaroM",
    "type": "Bearer",
    "id": 2,
    "email": "maria.fernanda.gomez@example.com",
    "roles": [
        "ROLE_ESTUDIANTE"
    ]
}

En Authorization el Auth Type es Bearer Token.
El token tiene una duración de 24 horas.

  _  _   _____   __  __   _    
 | || | |_   _| |  \/  | | |   
 | __ |   | |   | |\/| | | |__ 
 |_||_|   |_|   |_|  |_| |____|
                               
--> En la entrega anterior, le había dejado un README explicando como funcionaban los controladores de 
las APIs, sin embargo, como esta entrega posee integración de documentación OpenAPI soportada por SMARTBEAR,
no creo que sea necesario dejarle ese documento acá. 

Sí le voy a dejar el enlace para poder conectarse al Postman 
y poder obtener la documentación, adicionalmente en la capa de seguridad, hice una excepción, que permite usar 
el web browser de su predilección para que pueda abrir el enlace .HTML y pueda visualizarlo más fácilmente.

NOTA: no hace falta autenticarse para poder abrir el Swagger-UI. 

El URL para ver la documentación es el siguiente:
-> http://localhost:8080/swagger-ui/index.html

 
 __      __    _     _      _      ___   _____ 
 \ \    / /   /_\   | |    | |    | __| |_   _|
  \ \/\/ /   / _ \  | |__  | |__  | _|    | |  
   \_/\_/   /_/ \_\ |____| |____| |___|   |_|  
                                               

Muy importante verificar las rutas en el application.properties para que el programa pueda correr.

Si está corriendo en Linux, puede ser necesario hacer un export del TNS ADMIN, en mi caso fue:
	
   bash:
-> export TNS_ADMIN=/home/marvin/hdd/to_arch/EdutechAPI/EdutechAPI/src/main/resources/Wallet_edutechapis                           

Tuve algún problema de ORA-17957, dicho problema se debía a que la wallet debe estar correctamente enrutada,
además el ojdbc.properties debe estar correctamente configurado también.

Otro problema relacionado al mismo ORA-17957 fue que por defecto, las wallet traen una configuración que no 
especifica el tipo de método a utilizar, para solucionarlo, tuve que modificar el sql.net de la wallet a:

WALLET_LOCATION = (SOURCE = (METHOD = SSO))
SSL_SERVER_DN_MATCH=yes

Esto específica que el método es SSO. 


  _   _   ___   _      _      
 | | | | | _ \ | |    ( )  ___
 | |_| | |   / | |__  |/  (_-<
  \___/  |_|_\ |____|     /__/
                              

Aquí le dejo otras URL para ver la documentación en otros formatos.

JSON:
GET
->http://localhost:8080/v3/api-docs

YAML:
GET
-> http://localhost:8080/v3/api-docs.yaml


  _                     
 | |_   _ _   ___   ___ 
 |  _| | '_| / -_) / -_)
  \__| |_|   \___| \___|
                        

Para terminar, le dejo este tree, que es el árbol organizativo de todo el proyecto.

                  ╭───────────╮ 
      /\          │  user    │ marvin
     /  \         │ 󰇅 hname   │ archmarvin
    /    \        │ 󰅐 uptime  │ 2 hours, 20 mins
   /      \       │  distro  │ Arch Linux x86_64
  /   ,,   \      │  kernel  │ Linux 6.12.35-1-lts
 /   |  |   \     │  term    │ kitty 0.42.1
/_-''    ''-_\    │  shell   │ bash 5.2.37
                  │ 󰍛 cpu     │ AMD Ryzen 5 2600X (12) @ 3.60 GHz
                  │ 󰉉 disk    │ 22.36 GiB / 48.91 GiB (46%) - ext4
                  │  memory  │ 8.58 GiB / 31.26 GiB (27%)
                  │ 󰩟 network │ 192.168.1.93/24 (enp4s0)
                  ├───────────┤ 
                  │  colors  │ ● ● ● ● ● ● ● ●
                  ╰───────────╯ 

╭─marvin   󰉖 ~/hdd/to_arch/EdutechAPI/EdutechAPI                                                                                                                                                                            17.0.15
╰─ ❯❯ tree
.
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── EdutechAPI
│   │   │               ├── api
│   │   │               │   ├── cursos
│   │   │               │   │   ├── controller
│   │   │               │   │   │   └── CursoController.java
│   │   │               │   │   ├── model
│   │   │               │   │   │   └── Curso.java
│   │   │               │   │   ├── repository
│   │   │               │   │   │   └── CursoRepository.java
│   │   │               │   │   └── service
│   │   │               │   │       └── CursoService.java
│   │   │               │   ├── inscripciones
│   │   │               │   │   ├── controller
│   │   │               │   │   │   └── InscripcionController.java
│   │   │               │   │   ├── dto
│   │   │               │   │   │   ├── InscripcionRequest.java
│   │   │               │   │   │   └── InscripcionResponse.java
│   │   │               │   │   ├── model
│   │   │               │   │   │   └── Inscripcion.java
│   │   │               │   │   ├── repository
│   │   │               │   │   │   └── InscripcionRepository.java
│   │   │               │   │   └── service
│   │   │               │   │       └── InscripcionService.java
│   │   │               │   └── usuarios
│   │   │               │       ├── controller
│   │   │               │       │   └── UsuarioController.java
│   │   │               │       ├── model
│   │   │               │       │   ├── Rol.java
│   │   │               │       │   └── Usuario.java
│   │   │               │       ├── repository
│   │   │               │       │   ├── RolRepository.java
│   │   │               │       │   └── UsuarioRepository.java
│   │   │               │       └── service
│   │   │               │           └── UsuarioService.java
│   │   │               ├── config
│   │   │               │   ├── AESUtil.java
│   │   │               │   └── JacksonConfig.java
│   │   │               ├── EdutechApiApplication.java
│   │   │               └── security
│   │   │                   ├── AuthController.java
│   │   │                   ├── dto
│   │   │                   │   ├── JwtResponse.java
│   │   │                   │   └── LoginRequest.java
│   │   │                   ├── JwtAuthenticationFilter.java
│   │   │                   ├── PasswordGenerator.java
│   │   │                   ├── SecurityConfig.java
│   │   │                   └── UserDetailsServiceImpl.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── static
│   │       ├── templates
│   │       └── Wallet_edutechapis
│   │           ├── cwallet.sso
│   │           ├── ewallet.p12
│   │           ├── ewallet.pem
│   │           ├── keystore.jks
│   │           ├── ojdbc.properties
│   │           ├── README
│   │           ├── sqlnet.ora
│   │           ├── tnsnames.ora
│   │           └── truststore.jks
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── EdutechAPI
│                       ├── api
│                       │   ├── cursos
│                       │   │   └── service
│                       │   │       └── CursoServiceTest.java
│                       │   ├── inscripciones
│                       │   │   └── service
│                       │   │       └── InscripcionServiceTest.java
│                       │   └── usuarios
│                       │       └── service
│                       │           └── UsuarioServiceTest.java
│                       ├── EdutechApiApplicationTests.java
│                       └── KeyGenerator.java
└── target
    ├── classes
    │   ├── application.properties
    │   ├── com
    │   │   └── example
    │   │       └── EdutechAPI
    │   │           ├── api
    │   │           │   ├── cursos
    │   │           │   │   ├── controller
    │   │           │   │   │   └── CursoController.class
    │   │           │   │   ├── model
    │   │           │   │   │   └── Curso.class
    │   │           │   │   ├── repository
    │   │           │   │   │   └── CursoRepository.class
    │   │           │   │   └── service
    │   │           │   │       └── CursoService.class
    │   │           │   ├── inscripciones
    │   │           │   │   ├── controller
    │   │           │   │   │   └── InscripcionController.class
    │   │           │   │   ├── dto
    │   │           │   │   │   ├── InscripcionRequest.class
    │   │           │   │   │   └── InscripcionResponse.class
    │   │           │   │   ├── model
    │   │           │   │   │   └── Inscripcion.class
    │   │           │   │   ├── repository
    │   │           │   │   │   └── InscripcionRepository.class
    │   │           │   │   └── service
    │   │           │   │       └── InscripcionService.class
    │   │           │   └── usuarios
    │   │           │       ├── controller
    │   │           │       │   └── UsuarioController.class
    │   │           │       ├── model
    │   │           │       │   ├── Rol.class
    │   │           │       │   └── Usuario.class
    │   │           │       ├── repository
    │   │           │       │   ├── RolRepository.class
    │   │           │       │   └── UsuarioRepository.class
    │   │           │       └── service
    │   │           │           └── UsuarioService.class
    │   │           ├── config
    │   │           │   ├── AESUtil.class
    │   │           │   └── JacksonConfig.class
    │   │           ├── EdutechApiApplication.class
    │   │           └── security
    │   │               ├── AuthController.class
    │   │               ├── dto
    │   │               │   ├── JwtResponse.class
    │   │               │   └── LoginRequest.class
    │   │               ├── JwtAuthenticationFilter.class
    │   │               ├── PasswordGenerator.class
    │   │               ├── SecurityConfig.class
    │   │               └── UserDetailsServiceImpl.class
    │   └── Wallet_edutechapis
    │       ├── cwallet.sso
    │       ├── ewallet.p12
    │       ├── ewallet.pem
    │       ├── keystore.jks
    │       ├── ojdbc.properties
    │       ├── README
    │       ├── sqlnet.ora
    │       ├── tnsnames.ora
    │       └── truststore.jks
    ├── EdutechAPI-0.0.1-SNAPSHOT.jar
    ├── EdutechAPI-0.0.1-SNAPSHOT.jar.original
    ├── generated-sources
    │   └── annotations
    ├── generated-test-sources
    │   └── test-annotations
    ├── maven-archiver
    │   └── pom.properties
    ├── maven-status
    │   └── maven-compiler-plugin
    │       ├── compile
    │       │   └── default-compile
    │       │       ├── createdFiles.lst
    │       │       └── inputFiles.lst
    │       └── testCompile
    │           └── default-testCompile
    │               ├── createdFiles.lst
    │               └── inputFiles.lst
    ├── surefire-reports
    │   ├── com.example.EdutechAPI.api.cursos.service.CursoServiceTest.txt
    │   ├── com.example.EdutechAPI.api.inscripciones.service.InscripcionServiceTest.txt
    │   ├── com.example.EdutechAPI.api.usuarios.service.UsuarioServiceTest.txt
    │   ├── com.example.EdutechAPI.EdutechApiApplicationTests.txt
    │   ├── TEST-com.example.EdutechAPI.api.cursos.service.CursoServiceTest.xml
    │   ├── TEST-com.example.EdutechAPI.api.inscripciones.service.InscripcionServiceTest.xml
    │   ├── TEST-com.example.EdutechAPI.api.usuarios.service.UsuarioServiceTest.xml
    │   └── TEST-com.example.EdutechAPI.EdutechApiApplicationTests.xml
    └── test-classes
        └── com
            └── example
                └── EdutechAPI
                    ├── api
                    │   ├── cursos
                    │   │   └── service
                    │   │       └── CursoServiceTest.class
                    │   ├── inscripciones
                    │   │   └── service
                    │   │       └── InscripcionServiceTest.class
                    │   └── usuarios
                    │       └── service
                    │           └── UsuarioServiceTest.class
                    └── EdutechApiApplicationTests.class

92 directories, 100 files


Al final están los tests también para que les eche un vistazo y los pueda correr.
