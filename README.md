# Leave Management System

A full-stack role-based web application for managing student leave requests with admin approval.

---

## Features
- **Role-based Authentication:** Secure login for Admin and Students.
- **Leave Requests:** Students can submit leave requests, track status, and receive notifications.
- **Admin Dashboard:** Admin can approve, reject, or delete leave requests.
- **Technology Stack:** React (Frontend), Spring Boot (Backend), MySQL (Database).

---

## Technologies Used
- **Frontend:** React, Axios, React Router
- **Backend:** Spring Boot, Spring Security, JPA
- **Database:** MySQL
- **Email:** SMTP Gmail integration
- **Tools:** IntelliJ IDEA, VS Code

---

## Getting Started

### Backend
1. Clone the repo:
```bash
git clone https://github.com/your-username/leavemanagement.git
```
2. Navigate to backend folder:
```bash
cd leavemanagement
```
3. Configure `application.properties` (replace passwords with placeholders):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/leave_management
spring.datasource.username=root
spring.datasource.password={your_password}
spring.mail.username={your_email}
spring.mail.password={your_email_password}
```
4. Run Spring Boot application.

### Frontend
1. Navigate to frontend folder:
```bash
cd leave-frontend
```
2. Install dependencies:
```bash
npm install
```
3. Configure `.env` file:
```env
REACT_APP_API_URL=http://localhost:8080/api
```
4. Run React app:
```bash
npm start
```

---

## Usage
- **Admin:** View all leave requests, approve, reject, delete requests.
- **Student:** Submit leave request, view status, receive notifications.

---

## Notes
- Email notifications are sent for approval/rejection.
- Role-based access is enforced via Spring Security.

---

## Author
**Subash Rajaram**

---

## License
This project is licensed under the MIT License.
