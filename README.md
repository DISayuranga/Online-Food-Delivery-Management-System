# CraveBite - Premium Food Delivery Platform

CraveBite is a full-featured, multi-role web application designed for a modern food delivery ecosystem. Built with **Java (Spring Boot 3)**, **HTML5 / Tailwind CSS / Modern JavaScript (Fetch API)**, and **MySQL**, this project demonstrates a robust implementation of Object-Oriented Programming (OOP) principles, relational database management, and native Java File I/O.

---

## 👥 Multi-Role Architecture

The system supports four distinct user roles with dedicated interfaces and permissions:

1. **Customer**:
   - Browse menus with polymorphic price calculations (taxes and dish fees calculated dynamically).
   - Manage shopping cart (add items, adjust quantities, calculate item and total prices).
   - **Profile & Settings Central Hub (`/profile.html`)**:
     - **Profile & Settings**: View/update name, email, phone number, and default delivery address.
     - **Delivery Locations (CRUD)**: Add, edit, view, and delete drop-off addresses with auto-filled recipient details and an interactive Leaflet.js Map coordinate picker with real-time GPS coordinates and direct **"🗺️ Open in Google Maps"** navigation links.
     - **Online Payment Cards (CRUD)**: Add, edit, view, and delete Credit and Debit cards with Expiry Month/Year dropdowns and stunning **realistic SVG physical card previews** (gold EMV smart chip, contactless RFID waves, Visa/Mastercard logos, embossed masked card numbers).
   - Live order tracking and order history viewing.
   - Leave and manage star reviews for dishes and delivery riders.
   - Profile management with cascading account deletion.

2. **Restaurant Owner**:
   - Dedicated dashboard to manage menus (`/product-add.html` labeled cleanly as **"Food Items"**).
   - Create, read, update, and delete polymorphic food items (Main Course, Dessert, Beverage) with **direct image uploads from local device** (JPG, PNG, WEBP) completely replacing manual text URLs.
   - View incoming customer orders in real-time with customer contact info, delivery address, and direct Google Maps destination viewing.
   - Accept orders and advance status from `PLACED` to `PREPARING` and `OUT_FOR_DELIVERY` (handover to rider).

3. **Delivery Rider**:
   - Dedicated delivery dashboard (`/rider-orders.html`).
   - View orders ready for pickup and claim available deliveries or skip orders.
   - **Direct Google Maps Navigation**: 1-click **"🗺️ Open in Google Maps Navigation"** button which automatically reads customer coordinates (`lat, lng`) and launches Google Maps routing directly to the customer's doorstep.
   - Mark orders as `DELIVERED` upon reaching the customer's doorstep.
   - Native Java File I/O: Generate, physically write to disk, and download comprehensive `.txt` delivery audit reports.

4. **Administrator**:
   - Comprehensive Admin Control Panel (`/admin-dashboard.html`).
   - Live search and filter users by username, role, or name.
   - Create new users under any role with role-specific fields.
   - Edit user profile details and reset passwords.
   - Safe cascading deletion: cleans up dependent carts, orders, reviews, addresses, and payment methods to preserve database integrity.

---

## 💻 Tech Stack & Architecture

- **Backend Framework**: Java 21 / Spring Boot 3.3.4
- **Persistence & ORM**: Spring Data JPA / Hibernate (Single-Table Inheritance with Discriminator Columns)
- **Database**: MySQL 8.x
- **Frontend**: Clean HTML5, JavaScript (Native ES6 Fetch API), Tailwind CSS (via CDN)
- **File I/O**: Native `java.io` (`FileWriter`, `BufferedWriter`, `File`, `FileSystemResource`)
- **Unit Testing**: JUnit Jupiter 5 / Spring Boot Test (`OopModelTests.java`)

```
   ┌───────────────────────────────────────────────┐
   │            FRONTEND (Client-Side)             │
   │  HTML5 + Tailwind CSS CDN + ES6 Fetch API     │
   │  Runs on: http://localhost:8080 (or 5500/3000)│
   └───────────────────────┬───────────────────────┘
                           │
             REST API Calls│ (JSON over HTTP)
             Full CORS OK  │
                           ▼
   ┌───────────────────────────────────────────────┐
   │             BACKEND (Server-Side)             │
   │  Spring Boot 3 + Spring Data JPA (Java 21)    │
   │  Runs on: http://localhost:8080               │
   └───────────────┬───────────────────────┬───────┘
                   │                       │
      JPA/Hibernate│                       │Native java.io
                   ▼                       ▼
   ┌───────────────────────┐   ┌───────────────────────────┐
   │   MySQL Database      │   │ Disk File Storage (.txt)  │
   │   (food_delivery_db)  │   │ Rider Delivery Reports    │
   └───────────────────────┘   └───────────────────────────┘
```

---

## ⚡ DAILY QUICK START 🟢 [RUN EVERY TIME YOU START THE APP]

> [!NOTE]
> **The database `food_delivery_db` and all tables (`users`, `food_items`, `addresses`, `payment_methods`, `orders`, etc.) are ALREADY created and populated on your machine!**
> You do **NOT** need to create the database again, and you do **NOT** need to run unit tests (`mvn test`) every time.

To start and use the project right now, open your terminal and run **one single command**:

* **📁 Location / Directory**: `project/` (The Project Root folder `.../project`)
* **Frequency**: 🟢 `[RUN EVERY TIME YOU START THE APP]`
* **Command**:
  ```bash
  mvn -f backend/pom.xml spring-boot:run
  ```
  *(OR if your terminal is already inside `project/backend/`, run: `mvn spring-boot:run`)*

Once you see:
```text
Started FoodDeliveryApplication in ... seconds
```

Open your web browser (Chrome, Edge, Firefox) and navigate to:
👉 **[http://localhost:8080/](http://localhost:8080/)**

Everything (Frontend, Backend, Database, Test Accounts) is fully connected and live!

---

## 🔐 ADMIN LOGIN CREDENTIALS & ACCESS

| Parameter | Admin Login Details |
| :--- | :--- |
| **Login Page URL** | 👉 **[http://localhost:8080/login.html](http://localhost:8080/login.html)** |
| **Username** | `admin` |
| **Password** | `password` |
| **Direct Dashboard URL** | 👉 **[http://localhost:8080/admin-dashboard.html](http://localhost:8080/admin-dashboard.html)** |
| **Role Type** | `ADMIN` (Super Administrator) |

### 🛠️ Admin Capabilities & Features:
* **User Management**: View, add, update, and delete users across all roles (`CUSTOMER`, `RESTAURANT_OWNER`, `RIDER`, `ADMIN`).
* **Live Search**: Instant real-time filtering by username, full name, or user role.
* **Password Reset**: Update any user's password directly from the Admin Edit modal.
* **Cascading Safe Deletion**: Deletes associated cart items, reviews, addresses, and payment methods safely without foreign key errors.

---

## 👥 ALL TEST LOGIN ACCOUNTS (By Role)

| Role | Username | Password | Access / Dashboard URL | Capabilities |
| :--- | :--- | :--- | :--- | :--- |
| **👑 Admin** | `admin` | `password` | [`/admin-dashboard.html`](http://localhost:8080/admin-dashboard.html) | Full user management across all 4 roles |
| **🛒 Customer** | `customer` | `password` | [`/profile.html`](http://localhost:8080/profile.html) | Browse menu, cart, checkout, tracking, reviews |
| **👨‍🍳 Restaurant Owner** | `owner` | `password` | [`/restaurant-orders.html`](http://localhost:8080/restaurant-orders.html) | Manage menu dishes, accept & prepare incoming orders |
| **🛵 Delivery Rider** | `rider` | `password` | [`/rider-orders.html`](http://localhost:8080/rider-orders.html) | Claim orders, mark delivered, download .txt report |

---

## 🛠️ COMPLETE COMMANDS GUIDE (With Execution Locations & Frequency)

Below is the complete list of all commands, their exact execution folder, and whether they need to be run only the 1st time or every time:

---

### 1. Database Setup 🔴 [ONLY RUN 1ST TIME ON A BRAND-NEW PC]
* **📁 Location / Tool**: MySQL Terminal, MySQL Workbench, or phpMyAdmin
* **Frequency**: 🔴 `[ONLY RUN 1ST TIME]` *(ALREADY DONE on this machine)*
* **Command**:
  ```sql
  CREATE DATABASE IF NOT EXISTS food_delivery_db;
  ```
* *Alternative*: If you ever move this project to another computer without internet, you can import the pre-saved database backup:
  * **📁 Location / Directory**: `project/` (Project Root)
  * **Command**:
    ```bash
    mysql -u root -proot food_delivery_db < database_backup.sql
    ```

---

### 2. Verify Database Credentials 🔴 [ONLY CHECK 1ST TIME ON A BRAND-NEW PC]
* **📁 Location / File**: `project/backend/src/main/resources/application.properties`
* **Frequency**: 🔴 `[ONLY RUN 1ST TIME]` *(ALREADY DONE on this machine)*
* **Content**:
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/food_delivery_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  spring.datasource.username=root
  spring.datasource.password=root
  ```
  *(Only change `root` if your local MySQL has a different username or password).*

---

### 3. Run Automated Unit Tests 🟡 [OPTIONAL - FOR EVALUATION & GRADING ONLY]
Run this whenever your teacher, examiner, or evaluator asks to verify that OOP Polymorphism, Encapsulation, and Model calculations pass unit tests:
* **📁 Location / Directory**: `project/` (Project Root)
* **Frequency**: 🟡 `[OPTIONAL - EVALUATION ONLY]` *(Does NOT need to be run daily)*
* **Command (from Project Root)**:
  ```bash
  mvn -f backend/pom.xml test
  ```
  *OR (if inside `project/backend/` folder)*:
  ```bash
  cd backend
  mvn test
  ```
* **Expected Result**:
  ```text
  [INFO] Running com.fooddelivery.OopModelTests
  [INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
  [INFO] BUILD SUCCESS
  ```

---

### 4. Run the Backend Server 🟢 [RUN EVERY TIME YOU START THE APP]
Start the Spring Boot REST API server:
* **📁 Location / Directory**: `project/` (Project Root)
* **Frequency**: 🟢 `[RUN EVERY TIME YOU START THE APP]`
* **Command**:
  ```bash
  mvn -f backend/pom.xml spring-boot:run
  ```
  *OR (if inside `project/backend/` folder)*:
  ```bash
  cd backend
  mvn spring-boot:run
  ```
* **Backend API URL**: `http://localhost:8080`

---

## 🌐 FRONTEND EXECUTION OPTIONS (With Exact Directories)

The frontend connects dynamically to the backend via REST API (`http://localhost:8080/api/...`). You can run it using any of the following methods:

---

### Method 1: Unified Spring Boot 🟢 [DEFAULT - NO EXTRA TERMINAL NEEDED]
* **📁 Location**: Handled automatically by the Backend server!
* **Frequency**: 🟢 `[RUN EVERY TIME]`
* **How to run**:
  1. Make sure Backend is running (`mvn -f backend/pom.xml spring-boot:run`).
  2. Open your browser and go directly to:
     ```text
     http://localhost:8080/index.html
     # or
     http://localhost:8080/
     ```

---

### Method 2: VS Code Live Server 🟢 [RUN SEPARATELY IN VS CODE]
* **📁 Location / File**: `project/frontend/index.html`
* **Frequency**: 🟢 `[WHEN YOU WANT TO RUN FRONTEND SEPARATELY]`
* **How to run**:
  1. Open the project folder in **Visual Studio Code**.
  2. In the left file explorer, right-click `frontend/index.html`.
  3. Click **"Open with Live Server"**.
  4. Browser opens automatically at:
     ```text
     http://127.0.0.1:5500/frontend/index.html
     ```
  *(Full CORS is pre-configured on the backend, so all actions work seamlessly).*

---

### Method 3: Python Built-In HTTP Server 🟢 [RUN IN SEPARATE TERMINAL]
* **📁 Location / Directory**: `project/frontend/` (Navigate into `frontend` folder first)
* **Frequency**: 🟢 `[WHEN YOU WANT TO RUN FRONTEND SEPARATELY]`
* **Commands (in Terminal 2)**:
  ```bash
  cd frontend
  python -m http.server 3000
  ```
* **Browser URL**:
  ```text
  http://localhost:3000/index.html
  ```

---

### Method 4: Node.js Serve / HTTP-Server 🟢 [RUN IN SEPARATE TERMINAL]
* **📁 Location / Directory**: `project/frontend/` (Navigate into `frontend` folder first)
* **Frequency**: 🟢 `[WHEN YOU WANT TO RUN FRONTEND SEPARATELY]`
* **Commands (in Terminal 2)**:
  ```bash
  cd frontend
  npx serve -l 3000
  ```
* **Browser URL**:
  ```text
  http://localhost:3000/index.html
  ```

---

## 🔗 Frontend Pages & Navigation URLs

Whether you access the frontend via port 8080 (Spring Boot), port 5500 (VS Code Live Server), or port 3000 (Python/Node), all pages are accessible:

| Page | URL Path (Port 8080) | Description |
| :--- | :--- | :--- |
| **Home** | `/index.html` or `/` | Landing page with category carousel and quick ordering |
| **Menu** | `/products-list.html` | Food dishes showing base price, polymorphic tax/dish price, and cart button |
| **Login** | `/login.html` | User authentication for all roles |
| **Register** | `/register.html` | Account registration with role selector and role-specific field |
| **Profile & Settings** | `/profile.html` | Profile Hub: Account details, Delivery Locations CRUD (Map Pin), Payment Cards CRUD (SVG Display) |
| **Cart** | `/cart-view.html` | Manage items, update quantities, view order subtotal and total |
| **Checkout** | `/checkout.html` | Select delivery address, choose masked card, and process payment |
| **Order Tracking** | `/order-tracking.html` | Live tracking of customer active orders with 1-click Order Cancellation |
| **Order History** | `/order-history.html` | Delivered orders receipt log with review shortcut |
| **Contact Us** | `/contact.html` | Customer support, inquiry form, and FAQ |
| **Restaurant Orders** | `/restaurant-orders.html` | Live orders console (Accept & Prepare, Ready for Rider, Cancel Order) |
| **Food Items** | `/product-add.html` | Food Items menu management with device image upload and category pricing |
| **Restaurant Reviews** | `/restaurant-reviews.html` | Real-time customer ratings and dish feedback for restaurant owner |
| **Restaurant Sales** | `/restaurant-sales.html` | Financial analytics and physical `.txt` sales report download |
| **Rider Dashboard** | `/rider-orders.html` | Accept/Skip orders, direct Google Maps routing, mark delivered, report download |
| **Admin Panel** | `/admin-dashboard.html` | 5 tabs: Analytics, Customers, Restaurants, Riders, Orders & Deliveries |
| **Addresses** | `/address-list.html` | Saved delivery addresses with interactive Leaflet.js Map coordinate picker |
| **Payments** | `/payment-methods.html` | Saved payment methods (Credit/Debit) |
| **Reviews** | `/my-reviews.html` | Reviews left by the user |

---

## 🔑 Default Login Credentials

On first startup, the database is automatically seeded with test accounts for each role:

| Role | Username | Password | Role Details |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `password` | Super Admin with access to `/admin-dashboard.html` |
| **Customer** | `customer` | `password` | Alice Customer with pre-seeded addresses and cards |
| **Restaurant Owner** | `owner` | `password` | Bob Owner of "Bob's Burgers & Bistro" |
| **Rider** | `rider` | `password` | Charlie Rider (WP-CA-9876) with delivery claiming & report downloading |

---

## 🧬 Object-Oriented Programming (OOP) Concepts Applied

This project strictly adheres to core OOP principles:

### 1. Abstraction
Abstract classes define essential properties and enforce mandatory polymorphic behaviors across all subclasses without exposing unnecessary implementation details:
- [`FoodItem`](file:///backend/src/main/java/com/fooddelivery/model/FoodItem.java): Defines abstract `calculatePrice()` and abstract `getItemType()`.
- [`User`](file:///backend/src/main/java/com/fooddelivery/model/User.java): Base entity defining abstract `getRoleType()`.
- [`Address`](file:///backend/src/main/java/com/fooddelivery/model/Address.java): Base entity defining abstract `getDeliveryInstructions()`, `getAddressType()`, and `getSpecificDetail()`.
- [`PaymentMethod`](file:///backend/src/main/java/com/fooddelivery/model/PaymentMethod.java): Base entity defining abstract `processPayment(double amount)`, `getCardType()`, and `getCardSpecificDetail()`.
- [`Review`](file:///backend/src/main/java/com/fooddelivery/model/Review.java): Base entity defining abstract `getReviewType()`.

### 2. Inheritance
Hierarchies are structured to eliminate code redundancy and facilitate single-table database mapping:
- `User` $\rightarrow$ [`Customer`](file:///backend/src/main/java/com/fooddelivery/model/Customer.java), [`RestaurantOwner`](file:///backend/src/main/java/com/fooddelivery/model/RestaurantOwner.java), [`Rider`](file:///backend/src/main/java/com/fooddelivery/model/Rider.java), [`Admin`](file:///backend/src/main/java/com/fooddelivery/model/Admin.java).
- `FoodItem` $\rightarrow$ [`MainCourse`](file:///backend/src/main/java/com/fooddelivery/model/MainCourse.java), [`Dessert`](file:///backend/src/main/java/com/fooddelivery/model/Dessert.java), [`Beverage`](file:///backend/src/main/java/com/fooddelivery/model/Beverage.java).
- `Address` $\rightarrow$ [`HomeAddress`](file:///backend/src/main/java/com/fooddelivery/model/HomeAddress.java), [`OfficeAddress`](file:///backend/src/main/java/com/fooddelivery/model/OfficeAddress.java).
- `PaymentMethod` $\rightarrow$ [`CreditCard`](file:///backend/src/main/java/com/fooddelivery/model/CreditCard.java), [`DebitCard`](file:///backend/src/main/java/com/fooddelivery/model/DebitCard.java).
- `Review` $\rightarrow$ [`FoodReview`](file:///backend/src/main/java/com/fooddelivery/model/FoodReview.java), [`DeliveryReview`](file:///backend/src/main/java/com/fooddelivery/model/DeliveryReview.java).

### 3. Polymorphism
Method overriding allows runtime dispatch where different objects handle common requests in specialized ways:
- **Dynamic Pricing**:
  - `MainCourse`: adds a standard 10% preparation tax: `basePrice + (basePrice * 0.10)`.
  - `Dessert`: adds a flat Rs. 2.00 sweet tax: `basePrice + 2.00`.
  - `Beverage`: adds a 5% tax and Rs. 1.00 cup fee: `basePrice + (basePrice * 0.05) + 1.00`.
  - In `CartItem.getTotalPrice()`, it polymorphically computes `foodItem.calculatePrice() * quantity`.
- **Dynamic Delivery Instructions**:
  - `HomeAddress`: formats residential front door instructions with gate access codes.
  - `OfficeAddress`: formats commercial reception instructions with floor and company details.
- **Dynamic Payment Processing**:
  - `CreditCard`: routes payment authorization through the Visa/Mastercard network against the user's credit limit.
  - `DebitCard`: routes payment directly to bank balance checks using the specified financial institution.

### 4. Encapsulation & Information Hiding
- All class fields are declared `private` with explicit getters and setters.
- **Card Security (PCI-DSS Best Practice)**: In [`PaymentMethod.java`](file:///backend/src/main/java/com/fooddelivery/model/PaymentMethod.java), `cardNumber` and `cvv` are annotated with `@JsonProperty(access = WRITE_ONLY)`. Raw card numbers and security codes are never leaked in REST API JSON responses.
- The `getMaskedDetails()` method securely returns masked card strings (e.g. `**** **** **** 4444`) to the frontend for display and receipt generation.

### 5. Native Java File I/O
- Located in [`OrderController.java`](file:///backend/src/main/java/com/fooddelivery/controller/OrderController.java) (`downloadRiderReport`).
- The backend writes physical delivery audit logs to server disk using `java.io.File`, `java.io.FileWriter`, and `java.io.BufferedWriter`.
- The file is then streamed safely to the client browser using Spring's `FileSystemResource` and HTTP `Content-Disposition: attachment`.

---

## 📋 Comprehensive CRUD Operations Matrix

| Entity | Create | Read | Update | Delete |
| :--- | :--- | :--- | :--- | :--- |
| **Users** | Registration (`/register.html`) or Admin Add Modal | Profile (`/profile.html`) & Admin Table (`/admin-dashboard.html`) | Edit Profile or Admin Edit Modal | Cascading account delete in Profile & Admin Dashboard |
| **Menu Items** | Add Dish (`/product-add.html`) | Public Menu (`/products-list.html`) & Restaurant Menu | Edit Dish (`/product-edit.html`) | Delete Dish (cleans up associated cart items and reviews) |
| **Addresses** | Add Address (`/address-form.html`) | Address List (`/address-list.html`) & Checkout | Edit Address (`/address-edit.html`) | Delete Address in Address List |
| **Payment Cards**| Add Card (`/payment-add.html`) | Cards List (`/payment-methods.html`) & Checkout | Edit Card (`/payment-edit.html`) | Delete Card in Cards List |
| **Reviews** | Submit Review (`/submit-review.html`) | My Reviews (`/my-reviews.html`) | Edit Review (`/review-edit.html`) | Delete Review in My Reviews |
| **Shopping Cart**| Add to Cart button on Menu | Cart View (`/cart-view.html`) | Update item quantity in Cart | Remove item or Clear Cart |
| **Orders** | Checkout (`/checkout.html`) | Order History & Order Tracking | Status transitions: `PLACED` $\rightarrow$ `PREPARING` $\rightarrow$ `OUT_FOR_DELIVERY` $\rightarrow$ `DELIVERED` | Order lifecycle managed |

---

## 🧪 Testing Summary

- **Unit Testing**: Run `mvn -f backend/pom.xml test` to verify all 6 unit tests in `OopModelTests.java`.
- **API Health**: Run `mvn -f backend/pom.xml compile` to verify zero compile warnings or errors.
- **Frontend Verification**: All pages use standard HTML5, Tailwind CSS, and JavaScript fetch calls with zero broken links or JSP scriptlets.
