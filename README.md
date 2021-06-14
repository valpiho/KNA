# Demo app for Kuehne Nagel Internship
RESTful Back-end Spring Boot application for mobile app https://github.com/CrazyGaffer/SQUFF3.0

**AWS link** <br>
http://kna-env.eba-venipq2h.eu-central-1.elasticbeanstalk.com/

In this application Users can register as Client or Driver <br>
Clients can add or remove other Clients or Drivers to their contacts list <br>
Clients can create inbound or outbound orders <br>
Clients can send or recieve orders <br>
Drivers can take orders to fullfil the shipment from one Client to another <br>

Java 11  <br>
Spring Boot 2.5.0  <br>
Maven
PosgreSQL for production <br>
H2 for dev and tests <br>
AWS (Elastic Beanstalk + RDS)<br>

**Angular v12 (Soon)**

Docker image is available here **piboxtln/spring-kna:v1**

### List of API end points:

- **POST**     /api/v1/login <br>

- **POST**     /api/v1/register <br>
Email with the new password will be sent

- **GET**      /api/v1/reset-password/{email} <br>
Email with the new password will be sent

==========================================================================

- **GET**      /api/v1/account <br>
Get User account details - for authenticated user

- **PATCH**    /api/v1/account <br>
Update User account details - for authenticated user

- **GET**      /api/v1/account/contacts <br>
Get users contacts list - for authenticated user

- **PATCH**    /api/v1/account/contacts/add?username={username} <br>
Add user with username to authenticated user contacts list

- **PATCH**    /api/v1/account/contacts/remove?username={username} <br>
Remove user with username from authenticated user contacts list

==========================================================================

- **GET**      /api/v1/users <br>
Get all users except authenticated user

- **GET**      /api/v1/users/{username} <br>
Get user with username

- **POST**     /api/v1/users <br>
For Admin only - Create new user

- **PATCH**    /api/v1/users/{username} <br>
For Admin only - Update user with username

- **DELETE**   /api/v1/users/{username} <br>
For Admin only - Delete user with username

==========================================================================

- **GET**      /api/v1/orders <br>
Get Orders by authenticated User

- **GET**      /api/v1/orders/all <br>
For Admin only - Get all Orders

- **GET**      /api/v1/orders/open <br>
For Drivers only - Get all Orders with Open Status

- **GET**      /api/v1/orders/{username} <br>
For Admin only - Get all Orders by User username

- **PATCH**    /api/v1/orders/{qrCode} <br>
Update Order Status

- **POST**     /api/v1/orders <br>
For Clients only - Create new Order

- **GET**      /api/v1/orders/{qrCode} <br>
Get Order by qrCode

- **DELETE**   /api/v1/orders/{qrCode} <br>
For Admin or User created by - Delete Order by qrCode
