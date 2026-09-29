# Personal Finance Dashboard

A full-stack personal finance management application for tracking income, expenses, budgets, and accounts. Built with Java Spring Boot on the backend, Angular on the frontend, and PostgreSQL for data persistence, all containerized with Docker for easy deployment.

---

## Tech Stack

| Layer          | Technology                                            |
|----------------|-------------------------------------------------------|
| **Backend**    | Java 21, Spring Boot 3, Spring Security + JWT, Spring Data JPA, PostgreSQL |
| **Frontend**   | Angular 17, RxJS, ngx-charts, SCSS                   |
| **Infrastructure** | Docker, Docker Compose, Nginx                     |

---

## Dashboard Wireframe

```
+------------------------------------------------------------------+
|  SIDEBAR  |  [Month/Year Selector]                               |
|           |  +------------+ +------------+ +----------+ +-------+ |
| Dashboard |  | Total      | | Monthly    | | Monthly  | |Savings| |
| Accounts  |  | Balance    | | Income     | | Expenses | |Rate   | |
| Transact. |  | $24,500    | | $5,200     | | $3,100   | | 40.4% | |
| Budgets   |  +------------+ +------------+ +----------+ +-------+ |
|           |  +-------------------------+ +------------------------+|
|  [Logout] |  | Category Breakdown      | | Monthly Trend          ||
|           |  | (Pie Chart)             | | (Bar Chart)            ||
|           |  |    Food 35%             | |  |||  |||  |||         ||
|           |  |    Transport 20%        | |  |||  |||  |||         ||
|           |  |    Bills 25%            | |  Jun  Jul  Aug         ||
|           |  +-------------------------+ +------------------------+|
|           |  +-------------------------+ +------------------------+|
|           |  | Budget Progress         | | Recent Transactions    ||
|           |  | Food [=========>] 85%   | | Sep 15  Grocery  -$85  ||
|           |  | Transport [=====>] 60%  | | Sep 14  Salary +$5200  ||
|           |  | Bills [============] 95%| | Sep 13  Netflix  -$15  ||
|           |  +-------------------------+ +------------------------+|
+------------------------------------------------------------------+
```

---

## Prerequisites

- **Docker** & **Docker Compose** (for containerized deployment)
- For local development:
  - Java 21 (JDK)
  - Maven 3.9+
  - Node.js 20+
  - Angular CLI 17

---

## Quick Start

```bash
# Clone the repository
git clone <repository-url>
cd finance-dashboard

# Start all services
docker-compose up --build

# Access the application
# Frontend:  http://localhost
# Backend:   http://localhost:8080/api
# Database:  localhost:5432
```

To stop all services:

```bash
docker-compose down
```

To stop and remove all data (including the database volume):

```bash
docker-compose down -v
```

---

## Local Development

### Backend

```bash
cd backend

# Run with Maven
mvn spring-boot:run

# Run tests
mvn test

# Build JAR
mvn clean package -DskipTests
```

The backend API will be available at `http://localhost:8080`.

### Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server
ng serve

# Run tests
ng test

# Build for production
ng build --configuration production
```

The frontend dev server will be available at `http://localhost:4200` with hot reload enabled.

---

## API Endpoints

### Authentication

| Method | Endpoint              | Description        | Auth Required |
|--------|-----------------------|--------------------|---------------|
| POST   | `/api/auth/register`  | Register new user  | No            |
| POST   | `/api/auth/login`     | Login              | No            |

### Dashboard

| Method | Endpoint         | Description        | Auth Required |
|--------|------------------|--------------------|---------------|
| GET    | `/api/dashboard` | Dashboard summary  | Yes           |

### Accounts

| Method | Endpoint            | Description        | Auth Required |
|--------|---------------------|--------------------|---------------|
| GET    | `/api/accounts`     | List all accounts  | Yes           |
| POST   | `/api/accounts`     | Create account     | Yes           |
| GET    | `/api/accounts/:id` | Get account by ID  | Yes           |
| PUT    | `/api/accounts/:id` | Update account     | Yes           |
| DELETE | `/api/accounts/:id` | Delete account     | Yes           |

### Transactions

| Method | Endpoint                | Description           | Auth Required |
|--------|-------------------------|-----------------------|---------------|
| GET    | `/api/transactions`     | List all transactions | Yes           |
| POST   | `/api/transactions`     | Create transaction    | Yes           |
| GET    | `/api/transactions/:id` | Get transaction by ID | Yes           |
| PUT    | `/api/transactions/:id` | Update transaction    | Yes           |
| DELETE | `/api/transactions/:id` | Delete transaction    | Yes           |

### Categories

| Method | Endpoint           | Description          | Auth Required |
|--------|--------------------|----------------------|---------------|
| GET    | `/api/categories`  | List all categories  | Yes           |
| POST   | `/api/categories`  | Create category      | Yes           |

### Budgets

| Method | Endpoint                     | Description        | Auth Required |
|--------|------------------------------|--------------------|---------------|
| GET    | `/api/budgets`               | List all budgets   | Yes           |
| POST   | `/api/budgets`               | Create budget      | Yes           |
| GET    | `/api/budgets/:id/progress`  | Budget progress    | Yes           |

---

## Project Structure

```
finance-dashboard/
├── docker-compose.yml          # Docker Compose orchestration
├── .env                        # Environment variables
├── init.sql                    # Database seed script
├── .gitignore                  # Git ignore rules
├── README.md                   # Project documentation
│
├── backend/                    # Spring Boot backend
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/portfolio/finance/
│       │   │   ├── config/          # Global exception handler
│       │   │   ├── controller/      # REST controllers
│       │   │   ├── dto/             # Data transfer objects
│       │   │   ├── entity/          # JPA entities
│       │   │   ├── repository/      # Spring Data repositories
│       │   │   ├── scheduler/       # Scheduled jobs
│       │   │   ├── security/        # Security, JWT, CORS config
│       │   │   └── service/         # Business logic
│       │   └── resources/
│       │       └── application.yml  # App configuration
│       └── test/                    # Unit & integration tests
│
└── frontend/                   # Angular frontend
    ├── Dockerfile
    ├── nginx.conf
    ├── package.json
    └── src/
        ├── app/
        │   ├── core/               # Guards, interceptors, services
        │   ├── features/           # Feature modules (dashboard, accounts, etc.)
        │   └── shared/             # Shared components, pipes, directives
        ├── assets/                 # Static assets
        ├── environments/           # Environment configs
        └── styles/                 # Global SCSS styles
```

---

## Environment Variables

| Variable        | Description                    | Default Value                      |
|-----------------|--------------------------------|------------------------------------|
| `DB_USER`       | PostgreSQL username            | `finance_user`                     |
| `DB_PASSWORD`   | PostgreSQL password            | *(set in .env)*                    |
| `JWT_SECRET`    | Secret key for JWT signing (Base64-encoded, 256+ bits) | *(set in .env)* |

Copy `.env.example` to `.env` and fill in your values:

```bash
cp .env.example .env
# Edit .env with your secrets
```

The `.env` file is excluded from git to prevent secrets from being committed.

---

## License

This project is for personal use and educational purposes.
