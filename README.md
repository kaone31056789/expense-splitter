# Expense Splitter (Splitwise-style) — Spring Boot

**Project Based Learning in Java, Experiment 3.3** · B.Tech IT, Semester 5

A REST backend for sharing group expenses, like Splitwise. You create users, put them in groups,
record who paid for what and how it should be split, and the app works out who owes whom and the
**fewest payments** needed to settle up. A small web interface (served by the same app) and a
Postman collection are included for demos.

## Features

| Feature | How it is built |
|---|---|
| Users and groups (many-to-many) | JPA `@ManyToMany` with a `group_members` join table |
| Expenses with 3 split types | **Strategy pattern**: `EQUAL`, `PERCENTAGE`, `EXACT` beans injected as `Map<String, SplitStrategy>` and chosen at runtime from the request body |
| Net balance per member | Java Streams: `groupingBy` + `summingDouble` |
| Settlement | Greedy algorithm with two `PriorityQueue` max-heaps (creditors and debtors): at most n − 1 payments, usually the minimum |
| Settle up | "Settle" button records a payment (full or partial); balances and suggestions update; payments can be undone |
| Activity log and notifications | **Observer pattern**: `AuditLogger` saves to the DB, `NotificationService` prints to the console |
| App settings (currency, rounding) | **Singleton pattern**: `ConfigService.getInstance()` |
| All-or-nothing save | `@Transactional` on `addExpense`: an invalid split rolls back the expense too |
| Clean errors | `@ControllerAdvice` + `@ExceptionHandler` → `{timestamp, status, error, message}` |
| Money rounding | `BigDecimal` with `HALF_UP` to 2 decimals; paisa left over from rounding goes to one participant so shares always add up exactly |

## Tech stack

Java 17 · Spring Boot 3.3.5 · Spring Web · Spring Data JPA / Hibernate · H2 (in-memory; MySQL config included in comments) · Maven · Postman. No Lombok.

## How to run

You only need **Java 17**. Maven is not required because the project includes the Maven Wrapper.

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

Then open:

| URL | What |
|---|---|
| http://localhost:8080/ | Demo web interface (click **Load demo data**) |
| http://localhost:8080/h2-console | H2 database console. JDBC URL `jdbc:h2:mem:expensedb`, user `sa`, no password |

Run the automated test: `mvnw.cmd test`

You can also open the folder in IntelliJ IDEA or Eclipse as a Maven project and run `ExpenseSplitterApplication`.

### Using Postman

1. In Postman click **Import** and choose `postman/Expense-Splitter.postman_collection.json`.
2. Start the app.
3. Run the folders in order (1 → 8), or click the collection → **Run** to run everything (41 requests, 59 checks).
   Folder 8 deletes everything the run created, so you can run it again and again.
   IDs from each response are saved automatically, and every request has tests that check the status code and values.

### Switching to MySQL

1. Uncomment `mysql-connector-j` in `pom.xml`.
2. Run `CREATE DATABASE expensedb;` in MySQL.
3. In `application.properties`, comment out the H2 block and uncomment the MySQL block (set your password).

## API

| Method | Endpoint | Purpose | Success | Errors |
|---|---|---|---|---|
| POST | `/api/users` | Create a user `{name, email}` | 201 | 400 |
| GET | `/api/users` | List users | 200 | |
| DELETE | `/api/users/{userId}` | Delete a user (refused while they are part of expenses) | 204 | 404, 409 |
| DELETE | `/api/groups/{groupId}` | Delete a group with its expenses and activity log | 204 | 404 |
| POST | `/api/groups` | Create a group `{name}` | 201 | 400 |
| GET | `/api/groups`, `/api/groups/{id}` | List groups / get one | 200 | 404 |
| POST | `/api/groups/{groupId}/members/{userId}` | Add a member | 200 | 400, 404 |
| POST | `/api/groups/{groupId}/expenses` | Add an expense | 201 | 400, 404 |
| GET | `/api/groups/{groupId}/expenses?userId=` | List expenses (optional filter by user) | 200 | 404 |
| DELETE | `/api/expenses/{expenseId}` | Delete an expense | 204 | 404 |
| GET | `/api/groups/{groupId}/balances` | Net balance of each member | 200 | 404 |
| GET | `/api/groups/{groupId}/settlements` | Minimum payments to settle | 200 | 404 |
| POST | `/api/groups/{groupId}/payments` | Record a settle-up payment `{fromUserId, toUserId, amount}` (can be partial) | 201 | 400, 404 |
| GET | `/api/groups/{groupId}/payments` | List recorded payments | 200 | 404 |
| DELETE | `/api/payments/{paymentId}` | Undo a payment | 204 | 404 |
| GET | `/api/groups/{groupId}/activity` | Activity log (from the observer) | 200 | 404 |

Example expense body:

```json
{
  "description": "Cab to airport",
  "amount": 1000,
  "paidBy": 2,
  "splitType": "PERCENTAGE",
  "splits": [
    { "userId": 1, "value": 25 },
    { "userId": 2, "value": 25 },
    { "userId": 3, "value": 30 },
    { "userId": 4, "value": 20 }
  ]
}
```

For `EQUAL`, leave out `value`. For `EXACT`, `value` is the amount in rupees.

## Project structure

```
expense-splitter/
├── pom.xml, mvnw, mvnw.cmd, .mvn/
├── postman/Expense-Splitter.postman_collection.json
├── docs/  (Postman test guide, viva cheat-sheet, lab record)
└── src/
    ├── main/java/com/expensesplitter/
    │   ├── ExpenseSplitterApplication.java
    │   ├── config/      ConfigService (Singleton)
    │   ├── controller/  UserController, GroupController, ExpenseController
    │   ├── dto/         ExpenseRequest, SplitRequest, BalanceResponse, SettlementResponse, ErrorResponse
    │   ├── entity/      User, Group, Expense, ExpenseShare, ActivityLog
    │   ├── exception/   custom exceptions + GlobalExceptionHandler
    │   ├── observer/    ExpenseListener, ExpenseEventPublisher, AuditLogger, NotificationService
    │   ├── repository/  JpaRepository interfaces
    │   ├── service/     UserService, GroupService, ExpenseService, BalanceService
    │   └── strategy/    SplitStrategy + Equal / Percentage / Exact
    ├── main/resources/  application.properties, static/index.html
    └── test/java/...    ExpenseSplitterApplicationTests
```
