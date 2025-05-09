# 💼 Salaries Files Generator – Backend

This is the backend of a web-based payroll processing system developed for small businesses. It allows users to securely input employee salary data and automatically generate bank-compliant salary files in both Excel and PDF formats with PDF417 barcodes.

## 🛠️ Tech Stack

- **Java**
- **Spring Boot**
- **H2 Database** (embedded)
- **JWT (JSON Web Tokens)** for authentication
- **Maven**, **Spring Security**
- **Apache POI** for Excel generation
- **iText** or similar library for PDF generation and barcode embedding

## 🎯 Project Purpose

The backend powers a platform designed to:
- Streamline the salary file submission process for businesses
- Automate generation of Excel and PDF files from employee input
- Ensure security and compliance with bank requirements (including PDF417 barcode inclusion)
- Handle user authentication

## 🔐 Authentication & Security

- JWT-based login system
- Password encryption using Spring Security


## ⚙️ Setup Instructions

1. **Clone the Repository**
```bash
git clone https://github.com/yourusername/salary-files-generator-backend.git
cd salary-files-generator-backend
```

2. **Build and Run**

Use your preferred IDE (e.g., IntelliJ, Eclipse) or terminal:
```bash
mvn clean install
mvn spring-boot:run
```

Backend will run on: http://localhost:8080/

H2 Database UI: http://localhost:8080/h2-console

## 🧪 Key Endpoints
| Method | Endpoint                 | Description                       |
| ------ | ------------------------ | --------------------------------- |
| POST   | `/api/auth/login`        | Authenticate and receive JWT      |
| GET    | `/api/employees`         | Retrieve employee list            |
| POST   | `/api/salaries/generate` | Generate Excel & PDF with barcode |


## 📦 Features
- Salary record input and validation
- Secure document generation with PDF417 barcode
- RESTful API architecture
- Lightweight embedded database (H2)
- Modular and extensible architecture


## 🌐 Live Demo
[Link to Live Demo](https://willtechbooth.dev/salaries/)
