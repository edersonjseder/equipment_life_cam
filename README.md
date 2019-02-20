# Equipment Life CAM Android App
Here you will find a project that aims to control photograph equipments and accessories for professional and amateur photographers.
The goal is to avoid the photographers to buy or sell stolen equipments when they negotiete them among themselves and keep other registered photographers
from buying stolen equipments.

- Java 8
- Android Platform
- Data manipulation layer
- Architecture components (LiveData, LiveCycle, Room)
- Gradle

# Database
The actual implementation uses SQLite as the database. The use of room makes easy to perform SQL queries and data manipulation
in the Android app

# Proposed Screens
The screens descriptions are:
- Create an access credentials containing email as username and password
- Register the owner data so his profile can be properly stored in database
- After his registration, he will be automatically be logged and ready to use the app and then can register new equipments
- The equipment registration contains also the serial number and through it it's possible to search for equipments which have
its status and the status can be owned, sold or stolen
- The user can search for equipments on the list through its serial number and its status
- The user can login by Facebook and by Google account as well

# Flavors
This application has a free flavor and paid flavor 

- The difference is that in free version the limit of equipments registered are 5
and the paid is unlimited.
- The user can logout from his Facebook userr and login with his Google account and in the
free version this is not possible
- The free version has Google addons and the paid has no adds on
