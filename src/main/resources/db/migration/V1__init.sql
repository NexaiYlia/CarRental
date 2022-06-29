CREATE TABLE IF NOT EXISTS carrental.users_roles
(
    user_id    INT         NOT NULL PRIMARY KEY ,
    roles  VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER'
);

CREATE TABLE IF NOT EXISTS carrental.users
(
    id              INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR(45)  NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    second_name     VARCHAR(100) NOT NULL,
    phone_number    VARCHAR(13)  NOT NULL,
    email           VARCHAR(100) NOT NULL,
    verification_status TINYINT,
    verification_code  VARCHAR(255)

);

CREATE TABLE IF NOT EXISTS carrental.cars
(
    id                INT                              NOT NULL PRIMARY KEY AUTO_INCREMENT,
    brand             VARCHAR(100)                     NOT NULL,
    model             VARCHAR(100)                     NOT NULL,
    gearbox           VARCHAR(30)                      NOT NULL,
    manufactured_year YEAR                             NOT NULL,
    engine_type       VARCHAR(30)                      NOT NULL,
    price_per_day     DECIMAL(10, 2)                   NOT NULL,
    car_type          VARCHAR(30)                      NOT NULL,
    image_path        VARCHAR(255)                     NULL
);

CREATE TABLE IF NOT EXISTS carrental.orders
(
    id                INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id           INT          NOT NULL,
    car_id            INT          NOT NULL,
    status            VARCHAR(50) NOT NULL,
    start_date        DATE,
    end_date          DATE,
    cost              DECIMAL(5, 2)  NOT NULL,


    FOREIGN KEY (user_id) REFERENCES carrental.users (id),
    FOREIGN KEY (car_id) REFERENCES carrental.cars (id)

);

CREATE TABLE IF NOT EXISTS carrental.payments
(
    id           INT            NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_id     INT            NOT NULL,
    status       VARCHAR(50)    NOT NULL,
    total_price  DECIMAL(10, 2) NOT NULL,
    payment_date DATE           NOT NULL,

    FOREIGN KEY (order_id) REFERENCES carrental.orders (id)

);

CREATE TABLE IF NOT EXISTS carrental.comments
(
    id       INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id  INT           NOT NULL,
    content  VARCHAR(3000) NOT NULL,
    date     DATE          NOT NULL

);

CREATE TABLE carrental.accidents
(
  id           INT  NOT NULL PRIMARY KEY AUTO_INCREMENT,
  date         DATE NOT NULL,
  description  VARCHAR(255) NOT NULL,
  order_id     INT NOT NULL,
  cost         DECIMAL(10, 2) NOT NULL,
  is_paid      TINYINT NOT NULL DEFAULT 0,

  FOREIGN KEY (order_id) REFERENCES carrental.orders (id)
);
