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
![login + docker](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/4a42d37a-5a64-4bf0-8607-aee06f2c5d62)

Unauthorized request
![unauthorized](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/3a66a150-a0a6-45a4-b340-3a6b12d90c97)

Registration bad data
![bad reg data](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/65255be4-621a-4d59-aef8-470408ab3d8c)

Login bad data
![login bad data](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/74a1f344-b40f-4428-9cf2-d2762dfb6a3d)

Post new contact
![add new](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/929cf580-b2f8-47f2-8106-df567f3b1dfa)

Post with the same data
![post with same data](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/dafb20e3-33fd-4146-870a-9bd6e09eb050)

Edit exists
![edit exists](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/692efc57-8ce7-4bda-9b7c-4a9f2b98bbeb)

Delete
![delete](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/d37c6f7c-4443-4ee9-b9d1-2254adece8e5)

View contact image
![contact image2](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/aafeafba-2317-43e9-ac5a-c09d7564f582)

Export
![export](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/7a8f42f4-83cb-4c6b-b9da-7c68cb53b6ba)

Import
![import](https://github.com/OlexandrD-enter/phone-contacts/assets/77500422/0db863bd-8da8-426c-9afa-13c4e159b63b)







