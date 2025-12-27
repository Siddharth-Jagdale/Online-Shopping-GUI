🛒 Online Shopping Portal (DBMS Mini Project)
📌 Project Overview

The Online Shopping Portal is a Java-based desktop application developed as a DBMS mini project.
It demonstrates the integration of a Java Swing GUI with a MySQL database using JDBC, covering all essential database operations such as Create, Read, Update, Delete (CRUD) along with a basic Cart feature.

This project simulates a simple online shopping system where products are stored in a database and managed through a user-friendly graphical interface.

🎯 Objectives

To understand database connectivity using JDBC

To perform CRUD operations through a GUI

To reflect real-time database updates in the GUI

To implement a basic shopping cart feature

To apply DBMS concepts in a practical application

🛠️ Technologies Used

Java (JDK 17)

Java Swing (GUI)

MySQL Database

JDBC

Maven

MySQL Workbench

🗂️ Project Structure
online-shopping-gui/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/mycompany/onlineshop/
│       │       ├── MainWindow.java
│       │       ├── DBHelper.java
│       │       ├── Product.java
│       │       └── Cart.java
│       │
│       └── resources/
│           └── db.properties
│
├── pom.xml
└── README.md

🗄️ Database Schema
Database:

CREATE DATABASE online_shopping;
USE online_shopping;

Table:

CREATE TABLE products (
  product_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150),
  price DECIMAL(10,2),
  stock INT
);

Sample Data:

INSERT INTO products (name, price, stock) VALUES
('Wireless Mouse', 599.00, 50),
('USB-C Charger', 899.00, 30),
('Noise Cancelling Headphones', 2999.00, 12);

⚙️ Configuration (db.properties)

Located at: src/main/resources/db.properties

db.url=jdbc:mysql://localhost:3306/online_shopping
db.user=root
db.password=your_mysql_password

🚀 How to Run the Project
1️⃣ Prerequisites

Java JDK 17 installed

MySQL Server running

Maven installed

MySQL database & table created

2️⃣ Clone or Open Project

cd online-shopping-gui

3️⃣ Build & Run

mvn clean compile exec:java


🧩 Features Implemented

✅ Display products fetched from MySQL database

✅ Add new products

✅ Update existing products

✅ Delete products

✅ Search products by name

✅ Refresh product list

✅ Add products to Cart

✅ View Cart contents

✅ Real-time GUI update after database operations


🖥️ GUI Highlights

Clean and user-friendly Swing interface

Styled buttons and table

Cart confirmation popup messages

Live synchronization with database


📘 Learning Outcomes

Hands-on experience with JDBC

Understanding of DBMS concepts

Practical implementation of CRUD operations

GUI-based database interaction

Basic software design and modular coding


👨‍💻 Author

Siddharth Jagdale
DBMS Mini Project – Java GUI + MySQL

📄 License

This project is developed for academic purposes only.
