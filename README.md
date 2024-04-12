## Requirements for launching the application
- Java 21
- Spring boot 3.2.4
- PostgreSQL
- Maven

## Database initialization
Use the Flyway database migration tool to initialize the database. 
It is placed in a folder 'src/main/resources/db/migration'.
Flyway automatically executes these scripts when the application is launched.

## Launch the application
1) Save the application to your computer.
2) Install Java 21 and Maven on your computer.
3) Configure the connection to your PostgreSQL database in a file 'application.yml'
in a folder 'src/main/resources'
4) Execute the command 'mvn spring-boot:run' in the terminal to start the application.

## Endpoints
1) '/ping' - GET request returns "pong". This way we can check if the application is up.
2) 'api/auth/signup' - POST request to register a new user.
3) '/api/auth/login' - POST request to log in.
4) '/api/auth/resend/email-confirmation/{email}' - GET request makes it possible to send a letter 
to the user about email verification again.
5) '/api/auth/email-confirm/{token}' - GET request confirmation of the user's email using an access token.
6) '/api/auth/send/reset-password-email/{email}' - GET request sending an email to change the user's password via email.
7) '/api/auth/change-password' - POST request to change the user's password.
8) '/api/user/current-user' - GET request returns information about the current logged-in user according to the JWT token.
9) '/api/user/all' GET request returns information about all users if the current user is an admin.
10) '/api/auth/logout/{email}' - DELETE request to log out the application.

