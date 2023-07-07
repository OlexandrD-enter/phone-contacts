“Phone contacts” application. Phone contacts application allows adding/editing and deleting contacts data. Single contact is represented by the following data:

1.     Contact name

2.     Contact emails. One contact may have multiple emails

3.     Contact phone number. One contact may have multiple phone numbers

User should have a possibility to:

1.     Register in the app, login and password should be provided during registration

2.     Login to the app

3.     Add new contact

4.     Edit existing contact

5.     Delete existing contact

6.     Get list of existing contacts

The app to be implemented should:

-       Use any relational database to store contacts data. Take into account that schema should be designed in accordance with 1st and 2nd normal forms

-       Use Spring Boot, Spring Web, Spring Security and Spring Data + Hibernate

-       Should give access only to authorized users, so each user should have his own list of phone contacts (Use Spring Security)

-       Be a RESTful webservice from a client perspective

-       When contact is added or edited, emails and phone numbers should be validated so thatit is not possible to add phone number like “+38-asdas” or email like “aa@”. Also every phone number and email should be unique per contact. So it should not be possible to add already existing email, the same for phone numbers

Contact names should be unique

-       Have unit and integration tests


Docker image for springboot app: https://hub.docker.com/r/olexandrd13/springboot-app-contacts
Docker image of mysql db: https://hub.docker.com/r/olexandrd13/mysql-for-springboot-contacts-app


Run app on docker containers and try login:
![login + docker](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/aff3dd9c-aa2b-4e9a-b5fe-2ad66e0d7bd7)

Unauthorized request
![unauthorized](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/76157076-a4f4-4768-9066-37c8b044a839)

Registration bad data
![bad reg data](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/2f1a0144-c5d4-40b4-9f14-701d4c3f48eb)

Login bad data
![login bad data](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/7599a8ee-c9bf-424d-9a61-449ab246b927)

Post new contact
![add new](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/bd740156-6428-4d6b-a7ad-2915ffaf587d)

Post with the same data
![post with same data](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/c1d2a7a3-eb19-415b-86d1-dad7c19a231b)

Edit exists
![edit exists](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/9c5fdb00-f9ac-4894-9f39-5ab5be91600f)

Delete
![delete](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/c6e5530b-7b83-4b31-a188-3e14decaa50c)

View contact image
![contact image2](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/689ab623-0482-4d37-8c15-f14a3b11f711)

Export
![export](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/a9389c63-8001-4b08-8119-a223451f173b)

Import
![import](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/05ca2535-f980-4ba3-8856-deee262f5314)







