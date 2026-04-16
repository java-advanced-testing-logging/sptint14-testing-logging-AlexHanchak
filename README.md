# Advanced Java Course
## Web Application 'ToDo List'

## 📋 Project Overview
This is a Spring MVC web application for managing ToDo lists with tasks. The project implements a complete CRUD functionality for Users, ToDo lists, Tasks, and States.

## 🎯 Sprint 14: Testing and Logging
This development stage focuses on ensuring the stability and transparency of the application by implementing unit and integration tests, as well as a logging system.

### 🏗️ Current Project Status
Most of the functionality for controllers, services, and templates is already implemented. Your task is to cover the remaining key components with tests and logging.

---

## 🏗️ Project Structure

### ✅ Already Implemented
- **Models and DTOs**: A complete set of entities (`User`, `ToDo`, `Task`, `State`) and Data Transfer Objects with validation.
- **Services and Repositories**: All business logic and database access (Spring Data JPA).
- **Controllers and Templates**: A full user interface for managing users, ToDo lists, tasks, and states.
- **Tests and Logging for Task and State**: The `Task` and `State` components already have full test coverage and implemented logging (use them as an example).

### ✅ Task for Students (Implemented)
Testing and logging for the **User** and **ToDo** components are implemented.

#### 1. Unit Tests (Service Layer - Mockito)
Implement tests to verify the business logic in:
- **`UserService`**:
    - `register(CreateUserDto)`: verify `USER` role assignment, adding the `{noop}` prefix to the password, and handling duplicate emails (should throw `IllegalArgumentException`).
    - `update(UpdateUserDto)`: verify that only administrators can change roles.
    - `readById(long)`: successful retrieval and handling of `EntityNotFoundException`.
- **`ToDoService`**:
    - `create(ToDo)`: verify uniqueness of the list title.
    - `addCollaborator` / `removeCollaborator`: verify correct addition/removal of users from the `Set` of collaborators.

#### 2. Integration Tests (Web Layer - MockMvc)
Implement tests to verify mappings and model handling in:
- **`UserController`**:
    - Displaying creation and editing forms.
    - `POST /users/create`: verify handling of validation errors and redirect upon success.
    - Displaying the list of all users.
- **`ToDoController`**:
    - Displaying ToDo lists for a specific user.
    - `GET /todos/{id}/tasks`: verify that the correct tasks and the list of potential collaborators are passed to the model.

#### 3. Logging (SLF4J)
Add logging for core operations and errors in:
- `UserService` and `ToDoService` (creation, update, deletion logic).
- `UserController` and `ToDoController` (incoming requests, validation results).

*Note: Use the `@Slf4j` annotation from Lombok.*

---

## 🚀 How to Run
1. Ensure PostgreSQL is running with database `todolist` (or use H2 configuration for quick start).
2. Run the Spring Boot application.
3. Navigate to `http://localhost:8083`.
4. Login with credentials provided in the database (e.g., from `data.sql`).

## 📚 Technologies Used
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- PostgreSQL / H2
- Lombok
- Bootstrap 5
- Jakarta Validation
- Java 21

## ✅ What Can Be Tested Now
- ✅ Login/Logout functionality
- ✅ Home page
- ✅ State management (full CRUD) at `/states`
- ✅ Registration form display at `/users/create`
- ✅ Task management (already covered by tests)
- ✅ User management (tests and logging implemented)
- ✅ ToDo management (tests and logging implemented)
