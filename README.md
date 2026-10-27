# 🍔 CraveBite — Food Delivery Platform

A full-stack food delivery web application with Customer, Restaurant Owner, Rider, and Admin portals.

---

## 🚀 Quick Start

> **Prerequisites**: Java 17+, Maven, Node.js 18+

### 1. Start the Backend

```bash
cd "d:\sliit\Y1\oop\project 2\project"
cd backend
mvn spring-boot:run
```

Backend runs at → **http://localhost:8080**

### 2. Start the Frontend

```bash
cd "d:\sliit\Y1\oop\project 2\project"
cd frontend
npm run dev
```

Frontend runs at → **http://localhost:3000**

---

## 📁 Project Structure

```
project/
├── backend/          # Spring Boot REST API (Java)
│   ├── src/
│   └── pom.xml
├── frontend/         # Static HTML/CSS/JS frontend
│   ├── index.html
│   ├── package.json
│   └── js/
└── README.md
```

---

## 💻 Run Commands Summary

### 1️⃣ Project Root Commands (`cd "d:\sliit\Y1\oop\project 2\project"`)
```bash
# Navigate to project root
cd "d:\sliit\Y1\oop\project 2\project"

# To run both or manage subfolders
cd backend   # Go to backend
cd frontend  # Go to frontend
```

### 2️⃣ Frontend Commands (`cd frontend`)
```bash
cd "d:\sliit\Y1\oop\project 2\project\frontend"

# Start the frontend dev server on http://localhost:3000
npm run dev

# Alternative start command
npm start
```
> The frontend dev server runs directly via Node.js (`node server.js`). No external packages or installation needed!

### 3️⃣ Backend Commands (`cd backend`)
```bash
cd "d:\sliit\Y1\oop\project 2\project\backend"

# Run Spring Boot backend server on http://localhost:8080
mvn spring-boot:run

# Run all test cases
mvn test

# Clean and compile
mvn clean compile

# Build JAR package (skipping tests)
mvn clean package -DskipTests
```

---

## 🔑 Test Accounts

| Role | Username | Password |
|------|----------|----------|
| **Admin** | `admin` | `password` |
| **Customer** | `customer` | `password` |
| **Restaurant Owner** | `owner` | `password` |
| **Rider** | `rider` | `password` |

---

## 🌐 Key Pages

| Page | URL |
|------|-----|
| Home | http://localhost:3000/index.html |
| Login | http://localhost:3000/login.html |
| Register | http://localhost:3000/register.html |
| Browse Food | http://localhost:3000/products-list.html |
| My Cart | http://localhost:3000/cart-view.html |
| My Orders | http://localhost:3000/order-history.html |
| Profile & Settings | http://localhost:3000/profile.html |
| Admin Dashboard | http://localhost:3000/admin-dashboard.html |
| Restaurant Dashboard | http://localhost:3000/restaurant-orders.html |
| Rider Dashboard | http://localhost:3000/rider-orders.html |
| Contact Us & Support | http://localhost:3000/contact.html |

---

## 🔌 API Endpoints (Backend — port 8080)

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register new user (customer only from UI) |
| `POST` | `/api/auth/login` | Login |

### Products / Food Items
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/products/list` | List all food items (supports `?category=` and `?restaurantId=` filters) |
| `POST` | `/api/products/add` | Add food item (restaurant owner) |
| `PUT` | `/api/products/edit/{id}` | Edit food item |
| `DELETE` | `/api/products/delete/{id}` | Delete food item |

### Home
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/home` | Featured restaurants and categories |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/orders` | Get orders (role-based) |
| `POST` | `/api/orders/place` | Place order |
| `PUT` | `/api/orders/{id}/status` | Update order status |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/admin/users` | List all users |
| `POST` | `/api/admin/users` | Create any user (admin only) |
| `DELETE` | `/api/admin/users/{id}` | Delete user |

---

## 👥 User Roles & Features

### 🧑 Customer
- Browse food by category or restaurant
- Add items to cart, place instant or **scheduled orders** (for tomorrow or custom time)
- Manage delivery locations (interactive Google Maps picker)
- Manage payment card details (realistic SVG card display)
- Track orders in real time with dish photos and kitchen status
- View order history with dish photos and totals
- Send direct messages to Admin, Restaurants, or Riders & receive direct replies
- Interactive **CraveBot AI assistant** on all pages
- Profile & Settings management

### 🏪 Restaurant Owner
- Dashboard with sales overview & kitchen display
- Food Items management (with device image upload)
- Manage incoming orders (view dishes with images, quantities, customer maps location)
- Support for **scheduled orders** (view custom scheduled delivery times)
- Transfer orders to rider for delivery
- View & directly reply to customer inquiries
- Customer reviews & downloadable sales reports

### 🛵 Rider
- View assigned and available delivery orders with item pictures
- Accept or skip orders
- Navigate to customer location via Google Maps
- View customer inquiries & send direct replies
- Vehicle profile management (with admin approval validation)

### 🔐 Admin
- Full dashboard & analytics
- Manage all users (customers, restaurant owners, riders)
- Manage restaurants and menus
- Manage orders and deliveries
- View all customer inquiries & post official replies
- Approve rider vehicle registrations
- Create any user type from admin panel

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | HTML5, Tailwind CSS, Vanilla JS |
| Backend | Java 17, Spring Boot 3 |
| Database | H2 (in-memory, auto-seeded) |
| Images | Unsplash CDN + local upload support |
| Maps | Google Maps Embed API |
| Payments | Custom card UI (SVG card display) |

---

## 📦 Database

The backend uses an **H2 in-memory database** that is auto-seeded on startup with:
- Default user accounts (admin, customer, owner, rider)
- Sample food categories: Burgers, Pizza, Lunch, Dinner, Juice, Sushi, Healthy, Desserts, Breakfast, Beverages, Main Course
- Sample restaurant data

> ⚠️ Data resets on every backend restart. This is intentional for development/demo purposes.

---

## 🧪 Running Tests

```bash
cd backend
mvn test
```

All 6 JUnit tests should pass (`BUILD SUCCESS`).
