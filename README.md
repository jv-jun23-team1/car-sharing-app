# CarSharingApp

[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](http://forthebadge.com)
[![forthebadge](https://forthebadge.com/images/badges/built-with-grammas-recipe.svg)](http://forthebadge.com)
[![forthebadge](https://forthebadge.com/images/badges/powered-by-coffee.svg)](http://forthebadge.com)

<h3>Welcome to the CarSharingApp API project, where we bring you an exceptional online car rental experience!</h3>
Our robust and secure application, built on the **Java Spring Boot framework**, is designed to cater to all car enthusiasts. We've harnessed cutting-edge technologies and integrated a range of features to offer users a safe, all-encompassing, and user-friendly e-commerce platform. In the following sections, we will walk you through the essential aspects and impressive functionalities of our application.

## Table of Contents

- [👨‍💻Tech stack](#tech-stack)
- [⚡Quick start](#-quick-start)
- [✈️Features](#features)
- [Roles](#roles)
- [‍🚀Swagger](#swagger)
- [🧑Contribution](#contribution)
- [🏁Conclusion](#conclusion)
- [📝License](#license)

## 👨‍💻Tech stack

Here's a brief high-level overview of the tech stack the **BookStore API** uses:

- [Spring Boot](https://spring.io/projects/spring-boot): provides a set of pre-built templates and conventions for creating stand-alone, production-grade Spring-based applications.
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html): provides features like authentication, authorization, and protection against common security threats.
- [Spring Web](https://spring.io/projects/spring-ws#overview): includes tools for handling HTTP requests, managing sessions, and processing web-related tasks.
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/): provides a higher-level abstraction for working with databases and includes support for JPA (Java Persistence API).
- [Hibernate](https://hibernate.org/): simplifies the interaction between Java applications and databases by mapping Java objects to database tables and vice versa.
- [Lombok](https://projectlombok.org/): helps reduce boilerplate code by automatically generating common code constructs (like getters, setters, constructors, etc.) during compile time.
- [Mapstruct](https://mapstruct.org/): generates mapping code based on annotations, reducing the need for manual, error-prone mapping code.
- [Liquibase](https://www.liquibase.org/): helps manage database schema changes over time, making it easier to track and deploy database updates.
- [Swagger](https://swagger.io/): provides a framework for generating interactive API documentation, allowing developers to understand, test, and use APIs more easily.
- [Docker](https://www.docker.com/): provides a consistent and reproducible way to deploy applications across different environments.

## ⚡️ Quick start

First, let's download a [repository](https://github.com/jv-jun23-team1/car-sharing-app).

Via IDE:
- Open IntelliJ IDEA.
- Select "File" -> "New Project from Version Control."
- Paste the link: https://github.com/jv-jun23-team1/car-sharing-app.git

Via git console command:

```bash
git clone https://github.com/jv-jun23-team1/car-sharing-app.git
```

Then we should build a project using **Maven**:
```bash
mvn clean install
```
Then, rise a **Docker** container of your app:
```bash
docker build -t {your-image-name}
docker-compose build
docker-compose up
```
That's all you need to know to start! 🎉

## ✈️Features

### Security:

In our application, we use JWT tokens to ensure secure authentication and authorization. This guarantees that only verified users can access sensitive features.

### Cars:

Users can get list of all cars or details of specific car by its id. Managers can add, update (change model, brand, type or amount available) and delete cars. 

### Payment:

Our platform supports payment function provided through Stripe platform. You can make your payment or get list of payments by user id.

### Notifications Service (Telegram)

Handles notifications about new rentals, overdue rentals, and successful payments. Other services interact with it to send notifications to car sharing service administrators. Utilizes the Telegram API, Telegram Chats, and Bots for communication.:

## Roles:

In our API, functionality is divided for Customers and Managers:

| Controller               | Customer                                                                                            | Manager                              |
|--------------------------|-----------------------------------------------------------------------------------------------------|--------------------------------------|
| AuthenticationController | Registration, authentication                                                                        | -                                    |
| CarController            | Get list of cars, get car by id                                                                     | Create, update, delete functionality |
| PaymentController        | Create a new payment, get all payments by user ID                                                   | -                                    |
| RentalController         | Create a new rental, get list of own rentals by status, get details of own rental by id, return car | Get list or details of any rentals   |
| UserController           | Get public information about account, update data                                                   | Change user roles                    |

## 🚀Swagger

We have integrated Swagger for easy API documentation. To access the API documentation after running the application, visit the [Swagger API documentation](http://localhost:8080/swagger-ui.html).

## 🧑Contribution

We welcome contributions from the community to enhance the Bookstore project. Whether you want to fix a bug, improve an existing feature, or propose a new one, your contributions are valuable to us.

## 🏁Conclusion:

This project is a RESTful API for a car sharing service, designed to manage user authentication, user profiles, car inventory, car rentals, payments via Stripe, and notifications via Telegram. It provides various endpoints under different controllers to handle these functionalities.

## 📝License

This project is licensed under the MIT license. Feel free to edit and distribute this template as you like.
