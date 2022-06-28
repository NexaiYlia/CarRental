CREATE TABLE IF NOT EXISTS carrental.roles
(
    id    INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name  VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS carrental.users
(
    id              INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    login           VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    second_name     VARCHAR(100) NOT NULL,
    phone_number    VARCHAR(13)  NOT NULL,
    email           VARCHAR(100) NULL,
    role_id         INT          NOT NULL,

    FOREIGN KEY (role_id) REFERENCES carrental.roles (id)
);

CREATE TABLE IF NOT EXISTS carrental.cars
(
    id                INT                              NOT NULL PRIMARY KEY AUTO_INCREMENT,
    brand             VARCHAR(100)                     NOT NULL,
    model             VARCHAR(100)                     NOT NULL,
    color             VARCHAR(50)                      NOT NULL,
    mileage           INT                              NOT NULL,
    gearbox           ENUM ('automatic', 'manual')     NOT NULL,
    manufactured_year YEAR                          NOT NULL,
    engine_type       ENUM ('petrol', 'diesel', 'gas') NOT NULL,
    price_per_day     DECIMAL(10, 2)                   NOT NULL,
    car_type          VARCHAR(30)                      NULL,
    image_path        VARCHAR(255)                     NULL
);

CREATE TABLE IF NOT EXISTS carrental.status
(
    id           INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    status_name  VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS carrental.orders
(
    id                INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id           INT          NOT NULL,
    car_id            INT          NOT NULL,
    status_id         INT          NOT NULL,
    start_date        TIMESTAMP(6) NOT NULL,
    end_date          TIMESTAMP(6) NOT NULL,
    rejection_comment VARCHAR(500) NULL,
    return_comment    VARCHAR(500) NULL,

    FOREIGN KEY (user_id) REFERENCES carrental.users (id),
    FOREIGN KEY (car_id) REFERENCES carrental.cars (id),
    FOREIGN KEY (status_id) REFERENCES carrental.status (id)
);

CREATE TABLE IF NOT EXISTS carrental.news
(
    id               INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id          INT          NOT NULL,
    header           VARCHAR(100) NOT NULL,
    content          VARCHAR(500) NOT NULL,
    publication_date TIMESTAMP(6) NOT NULL,
    image_path       VARCHAR(255) NULL,

    FOREIGN KEY (user_id) REFERENCES carrental.users (id)
);

CREATE TABLE IF NOT EXISTS carrental.payment
(
    id           INT            NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_id     INT            NOT NULL,
    status_id    INT            NOT NULL,
    total_price  DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP(6)   NOT NULL,

    FOREIGN KEY (order_id) REFERENCES carrental.orders (id),
    FOREIGN KEY (status_id) REFERENCES carrental.status (id)
);

CREATE TABLE IF NOT EXISTS carrental.comments
(
    id       INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id  INT          NOT NULL,
    car_id   INT          NOT NULL,
    order_id INT          NOT NULL,
    content  VARCHAR(200) NOT NULL,

    FOREIGN KEY (user_id) REFERENCES carrental.users (id),
    FOREIGN KEY (car_id) REFERENCES carrental.cars (id),
    FOREIGN KEY (order_id) REFERENCES carrental.orders (id)
);

CREATE TABLE carrental.accidents
(
  id           INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  date         TIMESTAMP(6) NOT NULL,
  description  VARCHAR(255) NOT NULL,
  photo        BLOB         NULL,
  order_id     INT NOT NULL,

  FOREIGN KEY (order_id) REFERENCES carrental.orders (id)
);
