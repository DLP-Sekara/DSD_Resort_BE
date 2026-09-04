DROP DATABASE IF EXISTS dsd_resort_db;
CREATE DATABASE dsd_resort_db;
USE dsd_resort_db;

-- =========================================================
-- LEVEL 1: Independent Tables (No Foreign Keys)
-- =========================================================

CREATE TABLE SystemUser (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'RECEPTIONIST', 'HEAD_CHEF') NOT NULL
);

CREATE TABLE RoomType (
    type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    max_occupancy INT NOT NULL
);

CREATE TABLE Guest (
    guest_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    nic VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE MealPlan (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    planCode VARCHAR(10) UNIQUE NOT NULL
);

CREATE TABLE FoodItem (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity_on_hand INT DEFAULT 0,
    is_kitchen_prepared BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE RawMaterial (
    material_id INT AUTO_INCREMENT PRIMARY KEY,
    material_name VARCHAR(100) NOT NULL,
    unit_of_measure VARCHAR(20) NOT NULL,
    quantity_on_hand DECIMAL(10, 2) DEFAULT 0.00,
    category VARCHAR(50) NULL
);

-- =========================================================
-- LEVEL 2: Tables with Single Dependencies
-- =========================================================

CREATE TABLE Room (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    type_id INT NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    FOREIGN KEY (type_id) REFERENCES RoomType(type_id) ON DELETE RESTRICT
);

CREATE TABLE BOMTemplate (
    template_id INT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    created_by INT NOT NULL,
    item_id INT NOT NULL UNIQUE,
    FOREIGN KEY (created_by) REFERENCES SystemUser(user_id),
    FOREIGN KEY (item_id) REFERENCES FoodItem(item_id)
);

-- =========================================================
-- LEVEL 3: Core Transactional & AI Tables
-- =========================================================

CREATE TABLE Reservation (
    res_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    plan_id INT NOT NULL,
    handled_by INT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    total_bill DECIMAL(10, 2) DEFAULT 0.00,
    guestCount INT NOT NULL,
    status VARCHAR(20) DEFAULT 'CONFIRMED',
    FOREIGN KEY (guest_id) REFERENCES Guest(guest_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id),
    FOREIGN KEY (plan_id) REFERENCES MealPlan(plan_id),
    FOREIGN KEY (handled_by) REFERENCES SystemUser(user_id)
);

CREATE TABLE RestaurantOrder (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NULL,
    handled_by INT NOT NULL,
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) DEFAULT 0.00,
    status ENUM('PENDING', 'PREPARING', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (guest_id) REFERENCES Guest(guest_id),
    FOREIGN KEY (handled_by) REFERENCES SystemUser(user_id)
);

CREATE TABLE BOMTemplateItem (
    template_item_id INT AUTO_INCREMENT PRIMARY KEY,
    template_id INT NOT NULL,
    material_id INT NOT NULL,
    qty_per_person DECIMAL(10, 4) NOT NULL,
    FOREIGN KEY (template_id) REFERENCES BOMTemplate(template_id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES RawMaterial(material_id)
);

CREATE TABLE DemandForecast (
    forecast_id VARCHAR(36) PRIMARY KEY,
    template_id TEXT NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    target_date DATE NOT NULL,
    date_details VARCHAR(255),
    temperature DECIMAL(5,2),
    predicted_guests INT NOT NULL,
    weather_feature VARCHAR(50),
    is_holiday BOOLEAN DEFAULT FALSE
);

CREATE TABLE BOMUsageLog (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    template_id INT NOT NULL,
    cooked_by INT NOT NULL,
    usage_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    portions_cooked INT NOT NULL,
    FOREIGN KEY (template_id) REFERENCES BOMTemplate(template_id),
    FOREIGN KEY (cooked_by) REFERENCES SystemUser(user_id)
);

-- =========================================================
-- LEVEL 4: Mapping Tables & Reviews (Multiple Dependencies)
-- =========================================================

CREATE TABLE ReservationDetail (
    res_id INT NOT NULL,
    item_id INT NOT NULL,
    ordered_qty INT NOT NULL,
    PRIMARY KEY (res_id, item_id),
    FOREIGN KEY (res_id) REFERENCES Reservation(res_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES FoodItem(item_id)
);

CREATE TABLE RestaurantOrderDetail (
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    ordered_qty INT NOT NULL,
    PRIMARY KEY (order_id, item_id),
    FOREIGN KEY (order_id) REFERENCES RestaurantOrder(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES FoodItem(item_id)
);

CREATE TABLE GuestReview (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NULL,
    reviewer_name VARCHAR(100) NULL,
    res_id INT NULL,      -- Can be NULL if review is only for a restaurant order
    order_id INT NULL,    -- Can be NULL if review is only for a room reservation
    review_text TEXT NOT NULL,
    nlp_score DECIMAL(5, 2),
    sentiment_label VARCHAR(20),
    star_rating INT,
    food_items JSON,
    staff_members JSON,
    date_of_visit DATE,
    FOREIGN KEY (guest_id) REFERENCES Guest(guest_id),
    FOREIGN KEY (res_id) REFERENCES Reservation(res_id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES RestaurantOrder(order_id) ON DELETE CASCADE
);