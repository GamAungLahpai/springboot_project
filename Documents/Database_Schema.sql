-- Database schema for Spring Boot Database Management System
-- Based on the submitted SQL script.

create table contacts
(
    id        int auto_increment
        primary key,
    email     varchar(255) not null,
    reference char(32)     not null
)
    collate = utf8mb4_unicode_ci;

create table customers
(
    id         int auto_increment
        primary key,
    first_name varchar(100) not null,
    last_name  varchar(100) not null,
    email      varchar(255) not null,
    phone      varchar(30)  null
)
    collate = utf8mb4_unicode_ci;

create table companycustomers
(
    id           int          not null
        primary key,
    company_name varchar(255) not null,
    business_id  varchar(100) not null,
    constraint fk_companycustomer_customer
        foreign key (id) references customers (id)
);

create table customeraddresses
(
    id             int auto_increment
        primary key,
    customer_id    int          not null,
    street_address varchar(255) not null,
    postal_code    varchar(20)  null,
    city           varchar(100) not null,
    country        varchar(100) null,
    constraint fk_customeraddress_customer
        foreign key (customer_id) references customers (id)
)
    collate = utf8mb4_unicode_ci;

create index idx_country
    on customeraddresses (country);

create table customerprofiles
(
    id                 int auto_increment
        primary key,
    customer_id        int         not null,
    date_of_birth      date        null,
    preferred_language varchar(50) null,
    constraint customer_id
        unique (customer_id),
    constraint fk_customerprofile_customer
        foreign key (customer_id) references customers (id)
);

create table orders
(
    id                  int auto_increment
        primary key,
    customer_id         int                                     not null,
    order_date          datetime    default current_timestamp() not null,
    delivery_date       datetime                                null,
    shipping_address_id int                                     null,
    status              varchar(50) default 'NEW'               null,
    constraint fk_order_customer
        foreign key (customer_id) references customers (id),
    constraint fk_order_shipping_address
        foreign key (shipping_address_id) references customeraddresses (id)
)
    collate = utf8mb4_unicode_ci;

create table productcategories
(
    id          int auto_increment
        primary key,
    name        varchar(100) not null,
    description text         null
)
    collate = utf8mb4_unicode_ci;

create table suppliers
(
    id           int auto_increment
        primary key,
    name         varchar(255) not null,
    contact_name varchar(100) null,
    phone        varchar(30)  null,
    email        varchar(255) null
)
    collate = utf8mb4_unicode_ci;

create table products
(
    id             int auto_increment
        primary key,
    name           varchar(255)   not null,
    description    text           null,
    price          decimal(10, 2) not null,
    stock_quantity int default 0  not null,
    category_id    int            null,
    supplier_id    int            null,
    constraint fk_product_category
        foreign key (category_id) references productcategories (id),
    constraint fk_product_supplier
        foreign key (supplier_id) references suppliers (id)
)
    collate = utf8mb4_unicode_ci;

create table orderitems
(
    order_id   int            not null,
    product_id int            not null,
    quantity   int            not null,
    unit_price decimal(10, 2) not null,
    primary key (order_id, product_id),
    constraint fk_orderitem_order
        foreign key (order_id) references orders (id),
    constraint fk_orderitem_product
        foreign key (product_id) references products (id)
)
    collate = utf8mb4_unicode_ci;

create table pricechanges
(
    id          int auto_increment
        primary key,
    product_id  int            not null,
    change_time timestamp      not null,
    old_price   decimal(10, 2) not null,
    new_price   decimal(10, 2) not null,
    constraint `1`
        foreign key (product_id) references products (id)
);

create index product_id
    on pricechanges (product_id);

-- The trigger uses an INSERT ... SELECT statement (one SQL statement),
-- so a custom DELIMITER is not required.
create definer = root@localhost trigger products_after_update
    after update
    on products
    for each row
    insert into pricechanges(
                             product_id,
                             change_time,
                             old_price,
                             new_price
    )
    select
    old.id,
    now(),
    old.price,
    new.price
    where old.price <> new.price;

create table supplieraddresses
(
    id             int auto_increment
        primary key,
    supplier_id    int          not null,
    street_address varchar(255) not null,
    postal_code    varchar(20)  null,
    city           varchar(100) not null,
    country        varchar(100) null,
    constraint fk_supplieraddress_supplier
        foreign key (supplier_id) references suppliers (id)
)
    collate = utf8mb4_unicode_ci;

create definer = root@localhost event cleanup_old_pricechanges on schedule
    every '1' MINUTE
        starts '2026-09-17 21:20:46'
    enable
    do
    delete from pricechanges
where change_time < now() - interval 30 day;
