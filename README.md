# Demo app for Kuehne Nagel Internship
RESTful Back-end Spring Boot application for mobile app https://github.com/CrazyGaffer/SQUFF3.0

Java 11 
Spring Boot 2.5.0 
PosgreSQL 
H2 for dev and tests
AWS



### List of API end points:

- **POST**     http://localhost:8080/api/v1/login <br>

- **POST**     http://localhost:8080/api/v1/register <br>
Email with the new password will be sent

- **GET**      http://localhost:8080/api/v1/reset-password/{email} <br>
Email with the new password will be sent

=======================================================================================

- **GET**      http://localhost:8080/api/v1/account <br>
Get User account details - for authenticated user

- **PATCH**    http://localhost:8080/api/v1/account <br>
Update User account details - for authenticated user

- **GET**      http://localhost:8080/api/v1/account/contacts <br>
Get users contacts list - for authenticated user

- **PATCH**    http://localhost:8080/api/v1/account/contacts/add?username={username} <br>
Add user with username to authenticated user contacts list

- **PATCH**    http://localhost:8080/api/v1/account/contacts/remove?username={username} <br>
Remove user with username from authenticated user contacts list

=======================================================================================

- **GET**      http://localhost:8080/api/v1/users <br>
Get all users except authenticated user

- **GET**      http://localhost:8080/api/v1/users/{username} <br>
Get user with username

- **POST**     http://localhost:8080/api/v1/users <br>
For Admin only - Create new user

- **PATCH**    http://localhost:8080/api/v1/users/{username} <br>
For Admin only - Update user with username

- **DELETE**   http://localhost:8080/api/v1/users/{username} <br>
For Admin only - Delete user with username

=======================================================================================

- **GET**      http://localhost:8080/api/v1/orders <br>
Get Orders by authenticated User

- **GET**      http://localhost:8080/api/v1/orders/all <br>
For Admin only - Get all Orders

- **GET**      http://localhost:8080/api/v1/orders/open <br>
For Drivers only - Get all Orders with Open Status

- **GET**      http://localhost:8080/api/v1/orders/{username} <br>
For Admin only - Get all Orders by User username

- **PATCH**    http://localhost:8080/api/v1/orders/{qrCode} <br>
Update Order Status

- **POST**     http://localhost:8080/api/v1/orders <br>
For Clients only - Create new Order

- **GET**      http://localhost:8080/api/v1/orders/{qrCode} <br>
Get Order by qrCode

- **DELETE**   http://localhost:8080/api/v1/orders/{qrCode} <br>
For Admin or User created by - Delete Order by qrCode
