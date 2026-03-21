# 🏢 Breakout Room Booking API
The Breakout Room Booking System is a web-based app designed to allows students to easily check room availability, make bookings and manage schedules in real time. 

---

## 🚀 Features

- 🔐 User authentication (JWT)
  * JWT-based authentication
  * Secure login & registration
  * Role-based access control (Admin/User)
  * Password encryption using BCrypt
- 🏠 Room booking management
- 📅 Booking history tracking
  * Book rooms with time slots
  * Prevent double booking (conflict validation)
  * Booking history tracking
  * Cancel or update bookings
- 👤 User Management
- 🌐 RESTful API design

---

## 🛠️ Tech Stack

- Java 21+
- Spring Boot
- Spring Security (JWT)
- PostgreSQL
- Maven
- Swagger
---

# 🚀 Backend Deployment Guide  
## Breakout Room Booking API (Docker + Nginx + DigitalOcean)

This guide explains how to deploy the **Breakout Room Booking API** on a **DigitalOcean server** using **Docker** and **Nginx as a reverse proxy**, with optional SSL via Cloudflare.

---

## 📌 Overview

Deployment architecture:

```text
Client (Browser / Mobile)
        ↓
Cloudflare (DNS + SSL)
        ↓
DigitalOcean Droplet
        ↓
Nginx (Reverse Proxy)
        ↓
Docker Container (Spring Boot API)
        ↓
PostgreSQL (Docker / External DB)
