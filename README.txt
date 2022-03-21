Appointment Scheduler - a program to create and maintain appointments between a contact and a customer.
Users can create, update, and delete Customers and Appointments.

Author:			Jonathan Hawranko
Student ID:		#xxxxxxxxx
Phone (EST):	xxx-xxx-xxxx
Email:			xxxxxxxxxxxx
App. Version:	1.0
Date:			March 15, 2022

IDE info:		IntelliJ IDEA 2021.2.2 (Ultimate Edition)
JDK version:	Java 11.0.14 2022-01-18 LTS
JavaFX version:	javafx-sdk-17.0.2
MySQL conn.:	mysql-connector-java-8.0.28

Directions for running the program:
Load project into IDE and compile the code.  Ensure MySQL is running on port 3306, and run the application.
The user is presented with a login screen, presented in languages English and French, depending on the user's locale.  All login attempts are logged to file login_activity.txt, located in the root directory of the project.  Once logged in, the user is shown a screen displaying all Customer information, and all Appointment information.  The user is able to create, update, and delete all customers and appointments.  Input is validated for appointment times and to ensure proper database format.  All client-side updates are processed in the database immediately.

For my 3rd report, I chose to list all Users in the system, and list out all appointment start/end times that were either updated or created by the user.
