# 🏫 Smart Campus Operations Hub

> IT3030 – Platform-based Application Framework  
> Y3S1 | WD-10 | SLIIT

A simple full-stack web application for managing campus resources, bookings, and maintenance tickets.

---

## 📌 Project Overview

This system helps manage basic campus operations:

- Book lecture halls and rooms
- Report maintenance issues
- Manage campus resources
- Send notifications for updates

---

## ✨ Features

### 📍 Resource Management
- Add and view campus resources
- Search by type and availability

### 📅 Booking System
- Users can book resources
- Admin approves or rejects bookings
- Prevents double booking

### 🛠 Ticket System
- Report maintenance issues
- Track ticket status (Open / In Progress / Resolved)
- Add comments for updates

### 🔔 Notifications
- Booking updates
- Ticket status updates
- Mark as read option

### 🔐 Authentication
- Login system with roles:
  - Admin
  - User
  - Technician
- JWT-based security

---

## 🏗 System Architecture

- **Frontend:** React + Material UI  
- **Backend:** Spring Boot  
- **Database:** MongoDB Atlas  
- **API:** REST APIs  

---

## ⚙️ Setup Instructions

### Backend
```bash
cd backend
mvn spring-boot:run