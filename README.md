 
# 🏪 ShopEase – Multi-Shop Management System

<p align="center">
  <strong>A Java-based multi-shop management solution for managing products, inventory, sales, customers, credit payments, and business analytics.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-Enterprise%20Development-orange?style=for-the-badge&logo=openjdk" alt="Java"/>
  <img src="https://img.shields.io/badge/JSP-Servlets-blue?style=for-the-badge" alt="JSP and Servlets"/>
  <img src="https://img.shields.io/badge/Database-Oracle-red?style=for-the-badge&logo=oracle" alt="Oracle"/>
  <img src="https://img.shields.io/badge/Server-Apache%20Tomcat-yellow?style=for-the-badge&logo=apachetomcat" alt="Tomcat"/>
  <img src="https://img.shields.io/badge/Build-Maven-purple?style=for-the-badge&logo=apachemaven" alt="Maven"/>
</p>

---

## 📌 Overview

**ShopEase** is a web-based Multi-Shop Management System developed using Java, JSP, Servlets, JDBC, and Oracle Database.

The application allows multiple shop owners to register and independently manage their shop operations through a centralized platform.

Each registered shop has access to its own products, inventory, customers, sales records, credit transactions, and reports.

The main objective is to simplify daily shop operations, improve inventory tracking, manage customer credit, and provide useful business insights through a single application.

## ✨ Key Features

### 🔐 1. Shop Registration & Authentication
- Shop owner registration.
- User login and logout.
- Secure password hashing using PBKDF2.
- Session-based authentication.
- Protected application routes.
- Shop-specific data access.
- Session timeout management.

### 📊 2. Dashboard
- Shop overview and business statistics.
- Today's sales overview.
- Total sales information.
- Total registered products.
- Low-stock product count.
- Total customer count.
- Pending Udhar balance.
- Quick navigation to application modules.

### 📦 3. Product & Inventory Management
- Add, update, and manage products.
- Organize products using categories.
- Maintain SKU and barcode information.
- Manage purchase and selling prices.
- Track available stock quantities.
- Configure minimum stock levels.
- Identify low-stock products.
- Maintain product status.

### 📈 4. Stock Management
- Stock-in operations.
- Stock-out operations.
- Manual stock adjustments.
- Previous and updated stock tracking.
- Stock movement history.
- Track stock changes with reference information.

### 🧾 5. Sales & Billing
- Create customer-based sales bills.
- Add multiple products to a single bill.
- Automatic subtotal calculation.
- Discount calculation.
- Automatic grand total calculation.
- Automatic stock deduction after successful sales.
- Unique bill number generation.
- Payment method selection.
- Invoice details and printing.
- Transaction-based sale processing.

### 👥 6. Customer Management
- Register new customers.
- Update customer information.
- Maintain customer contact details.
- View customer records.
- Manage shop-specific customer information.

### 💰 7. Udhar / Credit Management
- Create credit-based sales.
- Track unpaid and partially paid bills.
- Collect customer payments.
- Support partial payment collection.
- Update outstanding balances automatically.
- Track payment collection history.
- Support Cash, UPI, and Card collections.
- Automatically update payment status.

### 📉 8. Reports & Analytics
- Total bills and sales summary.
- Total discount information.
- Total collected payment.
- Outstanding credit summary.
- Product and low-stock statistics.
- Daily sales trends.
- Payment method analysis.
- Best-selling product analysis.
- Date-based report filters.

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Core application development |
| JSP | Dynamic web pages |
| Java Servlets | Request and response handling |
| JDBC | Database connectivity |
| Oracle Database | Relational data storage |
| HTML5 | Page structure |
| CSS3 | Styling and responsive layouts |
| JavaScript | Client-side interactions and calculations |
| JSTL | JSP page logic and formatting |
| Apache Tomcat 9 | Web application server |
| Apache Maven | Dependency and build management |
| IntelliJ IDEA | Development environment |
| Lucide Icons | Interface icons |

## 🏗️ Application Architecture

ShopEase follows an MVC-based architecture with DAO and Service layers.

```text
                 USER / BROWSER
                        |
                        v
                 JSP PRESENTATION
                        |
                        v
                 SERVLET CONTROLLER
                        |
                        v
                  SERVICE LAYER
                 (Business Logic)
                        |
                        v
                    DAO LAYER
                (Database Operations)
                        |
                        v
                 JDBC CONNECTION
                        |
                        v
                  ORACLE DATABASE
```

### Architecture Components

| Layer | Responsibility |
|---|---|
| Model | Represents application data |
| View | JSP pages and user interface |
| Controller | Handles HTTP requests using Servlets |
| Service | Implements business logic and validations |
| DAO | Executes database operations |
| Utility | Database connection and password utilities |
 
## 🗄️ Database Design

ShopEase uses Oracle Database to store shop information, user accounts, inventory, sales, and payment records.

### Main Database Tables

| Table | Description |
|---|---|
| SHOPS | Registered shop information |
| USERS | Shop user accounts |
| CATEGORIES | Product categories |
| PRODUCTS | Product and inventory details |
| STOCK_HISTORY | Stock movement records |
| CUSTOMERS | Customer information |
| SALES | Sales and billing information |
| SALE_ITEMS | Individual products in each bill |
| PAYMENTS | Sales payment records |
| UDHAR_COLLECTIONS | Customer credit collection history |

### Database Features
- Primary key-based record identification.
- Shop-wise data separation.
- Unique bill numbers within each shop.
- Database constraints for important business rules.
- Transaction management using JDBC.
- Prepared statements for database operations.

**Multi-Shop Data Isolation:** Each shop's records are associated with its `SHOP_ID`. The authenticated session determines the shop context for database operations.

---

## 📁 Project Structure

```text
ShopEasy/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── shopeasy/
│       │           ├── controller/
│       │           ├── dao/
│       │           ├── filter/
│       │           ├── model/
│       │           ├── service/
│       │           └── util/
│       │
│       └── webapp/
│           ├── css/
│           ├── js/
│           ├── images/
│           ├── WEB-INF/
│           ├── dashboard.jsp
│           ├── login.jsp
│           ├── register.jsp
│           ├── products.jsp
│           ├── categories.jsp
│           ├── stock.jsp
│           ├── customers.jsp
│           ├── sales.jsp
│           ├── sale-details.jsp
│           ├── sales-history.jsp
│           ├── udhar.jsp
│           ├── reports.jsp
│           └── profile.jsp
│
├── pom.xml
├── .gitignore
└── README.md
```

*The structure above represents the main application organization.*

---

## ⚙️ Installation & Setup

Follow these steps to run ShopEase locally.

### 1. Prerequisites

Install the following:

- JDK 25 (or a compatible JDK).
- IntelliJ IDEA.
- Apache Tomcat 9.
- Oracle Database.
- Maven support (included through the Maven wrapper in the project).

### 2. Clone the Repository

```bash
git clone https://github.com/MareoCraft/ShopEase.git
```

Navigate into the project:

```bash
cd ShopEase
```

### 3. Configure Oracle Database

1. Start your Oracle Database.
2. Create or select a database user.
3. Create the required ShopEase tables and sequences.
4. Configure the database connection in `DBConnection.java`.

Update the database URL, username, and password according to your Oracle configuration.

**Security:** Never commit actual database credentials to GitHub. Use environment variables or a local configuration file excluded from Git.

### 4. Configure Apache Tomcat

1. Open the project in IntelliJ IDEA.
2. Configure Apache Tomcat 9.
3. Set the application deployment artifact to the ShopEase WAR.
4. Configure the application server.
5. Run the application.

### 5. Build the Project

Using the IntelliJ Terminal on Windows:

```powershell
.\mvnw.cmd clean package
```

The generated WAR file will be available inside:

```text
target/ShopEasy-1.0-SNAPSHOT.war
```

### 6. Open the Application

After starting Tomcat, open:

```text
http://localhost:8080/ShopEasy/
```

The exact context path may vary according to your Tomcat deployment configuration.

---

## 🔒 Security & Data Management

- Password hashing using PBKDF2WithHmacSHA256.
- Random salt generation for password hashing.
- Session-based authentication.
- Authentication filter for protected routes.
- Shop-wise data access restrictions.
- PreparedStatement-based database operations.
- Transaction handling for critical sales and payment operations.
- Validation of product stock and payment amounts.

---

## 📸 Screenshots

Add your application screenshots here to demonstrate the user interface.

Suggested screenshots:

| Page | Screenshot |
|---|---|
| Login | `screenshots/login.png` |
| Registration | `screenshots/register.png` |
| Dashboard | `screenshots/dashboard.png` |
| Products | `screenshots/products.png` |
| Stock Management | `screenshots/stock.png` |
| Sales & Billing | `screenshots/sales.png` |
| Customer Management | `screenshots/customers.png` |
| Udhar Management | `screenshots/udhar.png` |
| Reports | `screenshots/reports.png` |

---

## 🚀 Future Enhancements

- Barcode scanner integration.
- PDF invoice download.
- Advanced sales analytics.
- Automated low-stock notifications.
- Product import and export.
- Role-based permissions for additional shop staff.
- Cloud deployment.
- Automated database backup.

---

## 🎯 Project Objectives

- Simplify daily shop management.
- Maintain accurate inventory records.
- Reduce manual billing operations.
- Improve customer credit tracking.
- Provide shop owners with useful business insights.
- Maintain independent data access for multiple shops.

---

## 👨‍💻 Developer

**MareoCraft**

GitHub: [@MareoCraft](https://github.com/MareoCraft)

Project: [ShopEase – Multi-Shop Management System](https://github.com/MareoCraft/ShopEase)

---

## 📄 License

This project is currently available for educational and portfolio purposes. Add a formal open-source license if you intend to permit reuse and redistribution.

---

<p align="center">
  <strong>ShopEase – Simplifying Shop Management, One Transaction at a Time.</strong>
</p>

<p align="center">
  ⭐ If you find this project interesting, consider giving the repository a star!
</p>
