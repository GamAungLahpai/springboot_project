
# REST API Documentation

## 1. Overview

This document describes the REST API endpoints implemented in our Spring Boot Database Management System for the Database Solutions course.

The application uses Spring Boot, Spring Data JPA, Hibernate, and MariaDB to manage customers, company customers, customer profiles, orders, order items, products, product categories, suppliers, and supplier addresses.

**Base URL:**

```text
http://localhost:8080
```

The API uses JSON for request and response bodies where applicable. Some endpoints accept values through URL query parameters instead of a JSON request body.

All endpoints described below are implemented in the supplied controller classes.

---

## 2. API Endpoint Summary

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/customers` | Retrieve all customers |
| POST | `/api/customers` | Create a new customer |
| GET | `/api/customers/{id}/orders` | Retrieve orders belonging to a customer |
| DELETE | `/api/customers/{id}` | Delete a customer |
| POST | `/api/company-customers` | Create a company customer |
| GET | `/api/company-customers/{id}` | Retrieve a company customer by ID |
| POST | `/api/customer-profiles` | Create a customer profile |
| GET | `/api/customer-profiles/{id}` | Retrieve a customer profile by ID |
| POST | `/api/order-items` | Create an order item |
| GET | `/api/order-items/order/{orderId}` | Retrieve all items belonging to an order |
| GET | `/api/product-categories` | Retrieve all product categories |
| POST | `/api/product-categories` | Create a product category |
| GET | `/api/products` | Retrieve all products |
| POST | `/api/products` | Create a product |
| PATCH | `/api/products/{productId}/price` | Update a product's price |
| GET | `/api/products/{productId}/price-history` | Retrieve product price-change history |
| GET | `/api/suppliers` | Retrieve all suppliers |
| POST | `/api/suppliers` | Create a supplier |
| POST | `/api/suppliers/{supplierId}/addresses` | Add an address to a supplier |

**Total: 19 endpoints across 7 controllers.**

---

## 3. Customer API

Base path: `/api/customers`

### 3.1 Get All Customers

Retrieves all customers stored in the database.

**Request**

```http
GET /api/customers
```

**Example response (200 OK)**

```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Smith",
    "email": "john@example.com",
    "phone": "+358401234567",
    "orders": [],
    "customerProfile": null
  }
]
```

The `orders` collection and `customerProfile` association are defined in the `Customer` entity.

### 3.2 Create Customer

Creates a new customer. The controller also supports saving associated orders when they are supplied in the request and correctly linked through the entity relationship.

**Request**

```http
POST /api/customers
Content-Type: application/json
```

**Example JSON body**

```json
{
  "firstName": "Emma",
  "lastName": "Wilson",
  "email": "emma.wilson@example.com",
  "phone": "+358409876543"
}
```

**Example response**

```json
{
  "id": 2,
  "firstName": "Emma",
  "lastName": "Wilson",
  "email": "emma.wilson@example.com",
  "phone": "+358409876543",
  "orders": [],
  "customerProfile": null
}
```

The customer ID is generated automatically by the database.

### 3.3 Get Customer Orders

Retrieves all orders associated with a specific customer.

**Request**

```http
GET /api/customers/1/orders
```

**Path parameter**

| Parameter | Type | Description |
|---|---|---|
| `id` | Integer | Customer ID |

**Example response**

```json
[
  {
    "id": 10,
    "orderDate": "2026-10-09T10:30:00",
    "deliveryDate": "2026-10-12T14:00:00",
    "status": "NEW",
    "shippingAddress": null,
    "orderItems": []
  }
]
```

The customer relationship is excluded from order serialization using `@JsonBackReference`, helping prevent circular JSON references.

### 3.4 Delete Customer

Deletes a customer using their ID.

**Request**

```http
DELETE /api/customers/2
```

**Path parameter**

| Parameter | Type | Description |
|---|---|---|
| `id` | Integer | Customer ID |

The controller returns no response body.

**Note:** The `Customer` entity uses cascading persistence operations for its orders and customer profile. Deletion can affect related records, while other database foreign-key constraints may prevent deletion when dependent data exists.

---

## 4. Company Customer API

Base path: `/api/company-customers`

Company customers extend the `Customer` entity using JPA's joined inheritance strategy. Their data is stored across the `customers` and `companycustomers` tables.

### 4.1 Create Company Customer

**Request**

```http
POST /api/company-customers
Content-Type: application/json
```

**Example JSON body**

```json
{
  "firstName": "Alice",
  "lastName": "Manager",
  "email": "alice@techcompany.fi",
  "phone": "+358401112233",
  "companyName": "Tech Solutions Oy",
  "businessId": "1234567-8"
}
```

**Example response**

```json
{
  "id": 3,
  "firstName": "Alice",
  "lastName": "Manager",
  "email": "alice@techcompany.fi",
  "phone": "+358401112233",
  "orders": [],
  "customerProfile": null,
  "companyName": "Tech Solutions Oy",
  "businessId": "1234567-8"
}
```

The joined inheritance strategy links the company-specific row to its parent customer using the same primary key.

### 4.2 Get Company Customer by ID

**Request**

```http
GET /api/company-customers/3
```

**Path parameter**

| Parameter | Type | Description |
|---|---|---|
| `id` | Integer | Company customer ID |

Returns the company customer, including inherited customer details and company-specific fields.

---

## 5. Customer Profile API

Base path: `/api/customer-profiles`

A customer profile stores optional additional information about a customer.

Each customer can have at most one profile because `customer_id` is unique in the `customerprofiles` table.

### 5.1 Create Customer Profile

This endpoint accepts request parameters, not a JSON request body.

**Request**

```http
POST /api/customer-profiles?customerId=1&dateOfBirth=2000-05-15&preferredLanguage=English
```

**Query parameters**

| Parameter | Type | Description |
|---|---|---|
| `customerId` | Integer | Existing customer ID |
| `dateOfBirth` | Date | Date in YYYY-MM-DD format |
| `preferredLanguage` | String | Preferred language |

**Example response**

```json
{
  "id": 1,
  "dateOfBirth": "2000-05-15",
  "preferredLanguage": "English"
}
```

The controller retrieves the existing customer and associates it with the newly created profile before saving.

The `customer` field is excluded from JSON serialization using `@JsonBackReference`.

### 5.2 Get Customer Profile by ID

**Request**

```http
GET /api/customer-profiles/1
```

**Example response**

```json
{
  "id": 1,
  "dateOfBirth": "2000-05-15",
  "preferredLanguage": "English"
}
```

---

## 6. Order Item API

Base path: `/api/order-items`

The `orderitems` table represents the many-to-many relationship between orders and products.

Its composite primary key consists of:

- `order_id`
- `product_id`

### 6.1 Create Order Item

Creates an order item associated with an existing order and product.

This endpoint accepts query parameters.

**Request**

```http
POST /api/order-items?orderId=10&productId=5&quantity=2&unitPrice=49.99
```

**Query parameters**

| Parameter | Type | Description |
|---|---|---|
| `orderId` | Integer | Existing order ID |
| `productId` | Integer | Existing product ID |
| `quantity` | Integer | Product quantity |
| `unitPrice` | Decimal | Price per unit |

**Example response**

```json
{
  "id": {
    "orderId": 10,
    "productId": 5
  },
  "quantity": 2,
  "unitPrice": 49.99
}
```

The controller creates an `OrderItemId` composite key, associates the existing order and product, and saves the order item.

The `order` and `product` relationships are excluded from this JSON response by the entity's Jackson annotations.

### 6.2 Get Items by Order ID

Retrieves all order items belonging to a particular order.

**Request**

```http
GET /api/order-items/order/10
```

**Path parameter**

| Parameter | Type | Description |
|---|---|---|
| `orderId` | Integer | Order ID |

**Example response**

```json
[
  {
    "id": {
      "orderId": 10,
      "productId": 5
    },
    "quantity": 2,
    "unitPrice": 49.99
  },
  {
    "id": {
      "orderId": 10,
      "productId": 7
    },
    "quantity": 1,
    "unitPrice": 25.00
  }
]
```

**Implementation note:** The controller currently retrieves all order items through `findAll()` and filters them by order ID using a Java Stream.

---

## 7. Product Category API

Base path: `/api/product-categories`

### 7.1 Get All Product Categories

**Request**

```http
GET /api/product-categories
```

**Example response**

```json
[
  {
    "id": 1,
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  },
  {
    "id": 2,
    "name": "Furniture",
    "description": "Home and office furniture"
  }
]
```

### 7.2 Create Product Category

**Request**

```http
POST /api/product-categories
Content-Type: application/json
```

**Example JSON body**

```json
{
  "name": "Computers",
  "description": "Laptops, desktops and computer accessories"
}
```

**Example response**

```json
{
  "id": 3,
  "name": "Computers",
  "description": "Laptops, desktops and computer accessories"
}
```

---

## 8. Product API

Base path: `/api/products`

The Product API supports product creation, retrieval, price updates, and price-history retrieval.

### 8.1 Get All Products

**Request**

```http
GET /api/products
```

**Example response (simplified)**

```json
[
  {
    "id": 5,
    "name": "Wireless Mouse",
    "description": "Ergonomic wireless mouse",
    "price": 29.99,
    "stockQuantity": 100,
    "category": {
      "id": 1,
      "name": "Electronics",
      "description": "Electronic devices and accessories"
    },
    "supplier": {
      "id": 1,
      "name": "Tech Supplier Oy",
      "contactName": "John Smith",
      "phone": "+358401234567",
      "email": "supplier@example.com"
    },
    "orderItems": []
  }
]
```

The complete response can include associated category, supplier, and order-item information depending on stored data.

### 8.2 Create Product

Creates a product associated with an existing category and supplier.

The category and supplier IDs must be provided as query parameters, while the product information is provided as a JSON request body.

**Request**

```http
POST /api/products?categoryId=1&supplierId=1
Content-Type: application/json
```

**Example JSON body**

```json
{
  "name": "Mechanical Keyboard",
  "description": "RGB mechanical keyboard",
  "price": 89.99,
  "stockQuantity": 50
}
```

**Example response (simplified)**

```json
{
  "id": 6,
  "name": "Mechanical Keyboard",
  "description": "RGB mechanical keyboard",
  "price": 89.99,
  "stockQuantity": 50,
  "category": {
    "id": 1,
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  },
  "supplier": {
    "id": 1,
    "name": "Tech Supplier Oy",
    "contactName": "John Smith",
    "phone": "+358401234567",
    "email": "supplier@example.com"
  },
  "orderItems": []
}
```

The controller retrieves the selected category and supplier from their repositories before saving the product.

### 8.3 Update Product Price

Updates the price of an existing product.

**Request**

```http
PATCH /api/products/6/price?newPrice=79.99
```

**Parameters**

| Parameter | Location | Type | Description |
|---|---|---|---|
| `productId` | Path | Integer | Existing product ID |
| `newPrice` | Query | Decimal | New product price |

**Example response (simplified)**

```json
{
  "id": 6,
  "name": "Mechanical Keyboard",
  "description": "RGB mechanical keyboard",
  "price": 79.99,
  "stockQuantity": 50,
  "orderItems": []
}
```

The endpoint retrieves the product, updates its price, and saves it through `ProductRepository`.

### 8.4 Get Product Price History

Retrieves historical product price changes.

**Request**

```http
GET /api/products/6/price-history
```

**Path parameter**

| Parameter | Type | Description |
|---|---|---|
| `productId` | Integer | Product ID |

**Example response**

```json
[
  {
    "id": 2,
    "changeTime": "2026-10-09T14:15:00",
    "oldPrice": 89.99,
    "newPrice": 79.99
  },
  {
    "id": 1,
    "changeTime": "2026-10-08T09:30:00",
    "oldPrice": 99.99,
    "newPrice": 89.99
  }
]
```

The repository method:

```java
findByProduct_IdOrderByChangeTimeDesc(productId)
```

retrieves the records for the selected product, ordered by change time in descending order.

The `product` association is omitted from the serialized history entries because it uses `@JsonIgnore`.

### 8.5 Database Trigger and Price History

Product price history is maintained by a MariaDB trigger:

```sql
products_after_update
```

This trigger executes after an update on the `products` table.

When the product price changes, it inserts a new record into `pricechanges` containing:

- Product ID
- Timestamp
- Previous price
- New price

**Example workflow:**

1. A product initially costs 89.99.
2. A PATCH request updates its price to 79.99.
3. The database trigger detects the change.
4. The trigger inserts a new price-history record.
5. The GET price-history endpoint retrieves the updated history.

The database also defines a scheduled event:

```sql
cleanup_old_pricechanges
```

When the MariaDB event scheduler is enabled, this event runs every minute and deletes price-change records older than 30 days.

These features demonstrate database-side automation alongside the REST API.

---

## 9. Supplier API

Base path: `/api/suppliers`

### 9.1 Get All Suppliers

**Request**

```http
GET /api/suppliers
```

**Example response**

```json
[
  {
    "id": 1,
    "name": "Tech Supplier Oy",
    "contactName": "John Smith",
    "phone": "+358401234567",
    "email": "supplier@example.com"
  }
]
```

### 9.2 Create Supplier

**Request**

```http
POST /api/suppliers
Content-Type: application/json
```

**Example JSON body**

```json
{
  "name": "Nordic Electronics Oy",
  "contactName": "Anna Virtanen",
  "phone": "+358409998877",
  "email": "contact@nordicelectronics.fi"
}
```

**Example response**

```json
{
  "id": 2,
  "name": "Nordic Electronics Oy",
  "contactName": "Anna Virtanen",
  "phone": "+358409998877",
  "email": "contact@nordicelectronics.fi"
}
```

### 9.3 Add Supplier Address

Creates a new address associated with an existing supplier.

**Request**

```http
POST /api/suppliers/2/addresses
Content-Type: application/json
```

**Path parameter**

| Parameter | Type | Description |
|---|---|---|
| `supplierId` | Integer | Existing supplier ID |

**Example JSON body**

```json
{
  "streetAddress": "Mannerheimintie 10",
  "postalCode": "00100",
  "city": "Helsinki",
  "country": "Finland"
}
```

**Example response (simplified)**

```json
{
  "id": 1,
  "streetAddress": "Mannerheimintie 10",
  "postalCode": "00100",
  "city": "Helsinki",
  "country": "Finland"
}
```

The controller finds the supplier using the supplied ID and associates it with the address before saving.

The complete serialized response may additionally contain the associated supplier object.

---

## 10. HTTP Methods and Response Handling

The application uses the following HTTP methods:

| Method | Purpose |
|---|---|
| GET | Retrieve records |
| POST | Create new records |
| PATCH | Update a specific field |
| DELETE | Delete a record |

The controllers primarily return JPA entity objects directly instead of using dedicated response DTOs.

Typical successful requests return HTTP 200 OK unless another status is configured.

Missing records are generally handled using `Optional.orElseThrow()` in the controllers. The implementation does not define a dedicated API error-response format in the supplied controller files.

Consequently, unsuccessful requests may produce framework-generated errors, and the exact HTTP status depends on the exception and Spring Boot configuration.

Database constraints may also reject invalid or conflicting data, including duplicate composite keys, duplicate customer profiles, and invalid foreign-key references.

---

## 11. API Testing

We used Postman to send HTTP requests and DataGrip to inspect stored database records.

The following sequence can be used to demonstrate the application:

1. Create a product category using `POST /api/product-categories`.
2. Create a supplier using `POST /api/suppliers`.
3. Create a product using `POST /api/products`.
4. Retrieve products using `GET /api/products`.
5. Update the product price using `PATCH /api/products/{productId}/price`.
6. Retrieve price history using `GET /api/products/{productId}/price-history`.
7. Create and retrieve customers using the Customer API.
8. Create customer profiles and supplier addresses.
9. Create order items for existing orders and products.

The IDs in the examples are illustrative and must be replaced with existing database record IDs when testing.

**Important:** The supplied controller set does not contain a general Order creation endpoint. To test order items, a valid order must already exist in the database or be created through the customer creation flow with appropriately structured nested order data.

---

## 12. Implementation Notes

- **Persistence:** Spring Data JPA repositories provide standard database operations such as `save()`, `findAll()`, `findById()`, and `deleteById()`.
- **Relationships:** JPA mappings include one-to-one, one-to-many, many-to-one, and joined inheritance.
- **Composite key:** `OrderItem` uses `@EmbeddedId` and `@MapsId` to associate order items with their orders and products.
- **JSON serialization:** Jackson annotations such as `@JsonManagedReference`, `@JsonBackReference`, and `@JsonIgnore` are used to control JSON output and prevent circular references.
- **Lazy loading:** Many entity relationships use `FetchType.LAZY`.
- **Price history:** A database trigger records price changes, while a scheduled database event removes older history records.
- **API design:** Some POST and PATCH endpoints use query parameters, while other POST endpoints use JSON request bodies.

---

## 13. Scope and Limitations

This document describes the REST endpoints explicitly implemented in the seven supplied controllers.

Although the project contains repositories and entities for additional database tables, repository methods alone do not expose public HTTP endpoints.

For example, the supplied controllers do not expose general REST endpoints for:

- Creating orders independently through `/api/orders`
- Retrieving a single customer using `/api/customers/{id}`
- Managing customer addresses through a dedicated address controller
- Deleting or updating all entity types

These operations should not be considered implemented API endpoints unless corresponding controllers are added.

The JSON examples in this document illustrate expected structures based on the supplied entity classes. They are not independently verified HTTP test results.

---

## 14. Conclusion

The REST API provides backend operations for managing customers, company customers, customer profiles, order items, product categories, products, suppliers, and supplier addresses.

It integrates Spring Boot with MariaDB through JPA/Hibernate and demonstrates relational database concepts such as foreign keys, entity relationships, composite keys, and inheritance.

The product price-history feature additionally demonstrates how REST operations can work together with database triggers and scheduled events.
