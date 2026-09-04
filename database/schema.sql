CREATE DATABASE IF NOT EXISTS food_ordering DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE food_ordering;

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(30) NOT NULL,
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1正常 0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    receiver VARCHAR(30) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    detail VARCHAR(255) NOT NULL,
    is_default TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_address_user (user_id),
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `admin` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(30),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    sort_order INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS food (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    image_url VARCHAR(500),
    stock INT NOT NULL DEFAULT 0,
    sales INT NOT NULL DEFAULT 0,
    recommended TINYINT NOT NULL DEFAULT 0,
    special_offer TINYINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_food_category FOREIGN KEY (category_id) REFERENCES category(id),
    INDEX idx_food_category_status (category_id, status),
    INDEX idx_food_name (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_cart_user_food (user_id, food_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT fk_cart_food FOREIGN KEY (food_id) REFERENCES food(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(40) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT',
    address_snapshot VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at DATETIME,
    completed_at DATETIME,
    INDEX idx_orders_user_created (user_id, created_at),
    INDEX idx_orders_status_created (status, created_at),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    food_name_snapshot VARCHAR(100) NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_item_food FOREIGN KEY (food_id) REFERENCES food(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    rating TINYINT NOT NULL,
    content VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_comment_user_order_food (user_id, order_id, food_id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT fk_comment_food FOREIGN KEY (food_id) REFERENCES food(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_favorite_user_food (user_id, food_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT fk_favorite_food FOREIGN KEY (food_id) REFERENCES food(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    reply VARCHAR(1000),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    replied_at DATETIME,
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT,
    action VARCHAR(100) NOT NULL,
    method VARCHAR(10),
    request_path VARCHAR(255),
    detail TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_operation_log_created (created_at),
    CONSTRAINT fk_operation_log_admin FOREIGN KEY (admin_id) REFERENCES `admin`(id)
) ENGINE=InnoDB;

INSERT INTO `admin` (username, password, real_name, status)
VALUES ('admin', '$2a$10$rGIzYE9CdahzrJjfEDl9R.KJUv7hyLdggyweexj9A9qrA.j3.3sie', '系统管理员', 1)
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO category (name, sort_order) VALUES ('主食', 1), ('小吃', 2), ('饮料', 3)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO food (category_id, name, description, price, original_price, stock, recommended, special_offer)
SELECT c.id, '番茄鸡蛋面', '酸甜开胃，现煮面条', 18.00, 22.00, 100, 1, 1 FROM category c WHERE c.name = '主食'
  AND NOT EXISTS (SELECT 1 FROM food WHERE name = '番茄鸡蛋面');
INSERT INTO food (category_id, name, description, price, stock, recommended)
SELECT c.id, '香辣鸡翅', '外酥里嫩，香辣入味', 26.00, 80, 1 FROM category c WHERE c.name = '小吃'
  AND NOT EXISTS (SELECT 1 FROM food WHERE name = '香辣鸡翅');
INSERT INTO food (category_id, name, description, price, stock)
SELECT c.id, '冰柠檬茶', '清爽解腻', 8.00, 200 FROM category c WHERE c.name = '饮料'
  AND NOT EXISTS (SELECT 1 FROM food WHERE name = '冰柠檬茶');
