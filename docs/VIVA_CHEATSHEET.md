# Viva Cheat-Sheet (plain English)

Read this top to bottom once. Each answer has three parts:
- **In simple words**: what it means.
- **Say this**: the short answer to give the examiner.
- **Show**: the file to open if they ask to see it.

---

## 0. Your project in one breath

> "Our project is an **Expense Splitter**, like the Splitwise app. Friends add the bills they paid, choose how to split each bill (equally, by percentage, or exact amounts), and the app tells everyone **who owes whom** and the **fewest payments** needed to settle. People can then mark payments as done. It is built with **Spring Boot** in Java, stores data with **JPA/Hibernate** in an **H2 database**, and we tested it with **Postman**."

### 2-minute demo script
1. Run `mvnw.cmd spring-boot:run`, then open **http://localhost:8080**.
2. Click **Load demo data**: 4 friends, 1 group (Goa Trip), 3 bills.
3. Point at **Balances**: "Green means they get money back, red means they owe."
4. Point at **Settle up**: "Only 3 payments are needed. Diya pays Aman 400, Chirag 200, Bhavya 150."
5. Click **Settle** on one line and type a smaller amount: "The remaining amount updates."
6. Add a bad expense (percentages adding up to 90) and press Save: "The server rejects it with error **400**, and nothing is saved."
7. Point at the black **API console** at the bottom: "Every click is a real REST call. Here are the method, the URL and the status code."
8. Optionally, open Postman and run the collection: **41 requests, 59 checks, all pass**.

---

## 1. Basic words (learn these first)

**What is Spring Boot?**
- In simple words: a ready-made kit for building Java web servers. It sets up the server (Tomcat), the database connection and JSON conversion automatically, so we only write our own logic.
- Say this: "Spring Boot is a framework built on Spring that auto-configures everything and has an embedded Tomcat server, so we just run the app."

**What is a REST API?**
- In simple words: a set of web addresses (URLs) the frontend calls to get or change data, like a waiter taking orders. `POST` means create, `GET` means read, `DELETE` means remove.
- Say this: "Our backend exposes URLs like `POST /api/users` and `GET /api/groups/1/balances`. They send and receive JSON."

**What is JSON?**
- In simple words: plain text for sending data, for example `{"name": "Aman", "email": "aman@demo.com"}`.

**What do the HTTP status codes mean?**
| Code | Meaning | When we use it |
|---|---|---|
| 200 | OK | Reading data, adding a member |
| 201 | Created | A new user, group, expense or payment was made |
| 204 | No Content | Deleted successfully (nothing to send back) |
| 400 | Bad Request | Your input is wrong (for example, percentages not adding up to 100) |
| 404 | Not Found | That id does not exist |
| 405 | Method Not Allowed | Right URL, wrong method |
| 409 | Conflict | You can't delete a user who still has expenses |

---

## 2. Database questions

**What is JPA? What is Hibernate?**
- In simple words: JPA is a set of rules for saving Java objects into database tables without writing SQL. Hibernate is the tool that follows those rules and writes the SQL for us.
- Say this: "JPA is the specification and Hibernate is the implementation. We mark classes with `@Entity` and Hibernate creates the tables and SQL."
- Show: `entity/Expense.java`

**What is an Entity?**
- In simple words: a Java class that becomes a database table. Each object becomes a row.
- Our entities are User, Group, Expense, ExpenseShare, ActivityLog and Payment.

**What is a Repository / JpaRepository?**
- In simple words: a ready-made helper for saving and finding data. We write an interface, and Spring writes the code behind it.
- Say this: "By extending `JpaRepository` we get `save`, `findById`, `findAll` and `delete` for free. Spring even builds queries from method names, like `existsByEmail`."
- Show: `repository/UserRepository.java`

**What are the relationships?**
- **User ↔ Group is many-to-many:** one person can be in many groups, and one group has many people. This uses `@ManyToMany` with a join table called `group_members`.
- **Expense → User is many-to-one:** many bills can be paid by the same person (`@ManyToOne`).
- **Expense → ExpenseShare is one-to-many:** one bill has many shares, one per person (`@OneToMany`). `cascade = ALL` means deleting the bill deletes its shares too.

**Why H2 and not MySQL?**
- In simple words: H2 runs inside our Java program in memory, so there is nothing to install. It is perfect for a lab, but the data disappears when the app stops. MySQL is a real database server where data stays.
- Say this: "H2 is an in-memory database, good for testing. To switch to MySQL we only change `application.properties` and add the MySQL driver. The code stays the same because JPA hides the database."

**Why are the tables named `users` and `expense_groups`?**
- Because `user` and `group` are reserved words in SQL.

---

## 3. The money logic (most likely questions)

**How do you split a bill?**
- **EQUAL:** total ÷ number of people. Rs 1200 among 4 is Rs 300 each.
- **PERCENTAGE:** each person pays their percent. The percents must add up to 100.
- **EXACT:** each person's amount is typed in. The amounts must add up to the total.

**What happens with Rs 1000 split among 3?**
- Each share is 333.33, which totals 999.99. The leftover 1 paisa goes to the first person (333.34), so the shares always add up exactly to 1000.

**Why do you use BigDecimal for rounding?**
- In simple words: computers store decimals like 0.1 slightly wrong (in Java, 0.1 + 0.2 = 0.30000000000000004). BigDecimal rounds exactly to 2 decimal places.
- Show: `config/ConfigService.java`, method `round()`

**How is the balance calculated?**
- In simple words: **balance = what you paid − your share of the bills (+ settle-up payments you made − payments you received)**.
- Positive means you get money back. Negative means you owe. Everyone's balances always add up to 0.
- Example: Aman paid 1200 and his shares total 800, so his balance is **+400**.
- Say this: "We use Java Streams: `groupingBy` groups the amounts per person and `summingDouble` adds them up."
- Show: `service/BalanceService.java`, method `getBalances()`

**How does the settlement (Settle up) work?**
- In simple words: make two lines, one of people who are owed money and one of people who owe money, each sorted from biggest to smallest. The biggest debtor pays the biggest creditor as much as possible. Whoever still has an amount left goes back in line. Repeat until nobody owes anything.
- Say this: "It is a **greedy algorithm** with two **PriorityQueues** used as max-heaps. Each step clears at least one person, so we need at most **n − 1 payments**."
- Show: `service/BalanceService.java`, method `getSettlements()`

**Is it always the minimum number of payments?**
- Honest answer: **almost always, but not always**. Finding the true minimum is a very hard problem (NP-hard). We tested random groups: greedy was optimal in 100% of 4-person groups, about 91% of 6-person groups and 76% of 8-person groups, and was never more than 2 payments over. It never needs more than n − 1.

**What does the Settle button do?**
- It records a payment, for example "Diya paid Aman Rs 250" (`POST /api/groups/{id}/payments`). The payment counts in the balances, so the remaining amount drops. You can't pay more than you owe (that gives a 400 error), and you can **undo** a payment (`DELETE /api/payments/{id}`).

---

## 4. Design patterns (very likely questions)

**What is a design pattern?**
- A well-known, reusable solution to a common coding problem, like a recipe.

**Strategy pattern: where and why?**
- In simple words: there are many ways to do one job, and you pick one while the program runs. Like Google Maps, where you choose car, bike or walking for the same trip.
- In our project there are three ways to split a bill: `EqualSplitStrategy`, `PercentageSplitStrategy` and `ExactSplitStrategy`. Spring puts all three into a `Map<String, SplitStrategy>`, and we pick one using the `splitType` sent in the request.
- Why: to add a new split type, we add one new class and change nothing else. There is no long if-else chain.
- Show: `strategy/` folder and `service/ExpenseService.java`

**Observer pattern: where and why?**
- In simple words: when something happens, everyone who subscribed gets told automatically, like YouTube subscribers getting a notification when a new video is uploaded.
- In our project, when an expense is added, `ExpenseEventPublisher` tells two listeners: `AuditLogger`, which saves a line in the activity log, and `NotificationService`, which prints a message in the console.
- Why: the expense code doesn't need to know who is listening. We can add an email notifier later without touching it.
- Show: `observer/` folder

**Singleton pattern: where and why?**
- In simple words: only one object of a class can ever exist, like a school having one principal.
- In our project, `ConfigService` holds the currency ("Rs") and rounding rules. It has a **private constructor**, so nobody can call `new ConfigService()`, and everyone calls `ConfigService.getInstance()`.
- Show: `config/ConfigService.java`

**Strategy vs Observer: what's the difference?**
- **Strategy** means choosing one method out of many (how to do something).
- **Observer** means telling many listeners about one event (who should be told).

---

## 5. Spring questions

**What are IoC and DI?**
- In simple words: we don't create objects ourselves with `new`. Spring creates them and hands them to the classes that need them. Like a restaurant: you don't cook, the kitchen brings the food to you.
- IoC (Inversion of Control) means Spring controls object creation. DI (Dependency Injection) means Spring passes ("injects") the objects in.

**`@Autowired` vs constructor injection?**
- `@Autowired` on a field: Spring fills that field automatically. We use this in most classes because it is simple.
- **Constructor injection:** dependencies are passed through the constructor and stored as `final` fields. This is the recommended way because it is safer and easier to test. We show it in `ExpenseService`.

**What do `@RestController`, `@Service` and `@Repository` mean?**
- `@RestController` handles web requests and returns JSON.
- `@Service` holds the business logic (the rules).
- `@Repository` talks to the database.
- They give three layers: Controller → Service → Repository.

**What are `@RequestMapping`, `@PathVariable` and `@RequestBody`?**
- `@RequestMapping("/api/users")` is the base URL for a controller.
- `@PathVariable` reads a value from the URL, like the `5` in `/api/expenses/5`.
- `@RequestBody` turns the JSON body into a Java object.

**Why `@Transactional` on addExpense?**
- In simple words: **all or nothing**. Saving an expense means saving the expense, its shares and a log line. If any part fails, everything is cancelled (rolled back), so there is never a half-saved bill.
- Proof: we save the expense first, then check the split. If the split is wrong, an exception is thrown and Spring removes the saved expense. In the database you can even see that id 4 is missing after a failed try.
- Show: `service/ExpenseService.java`, method `addExpense()`

**What do `@ControllerAdvice` and `@ExceptionHandler` do?**
- In simple words: one central place that catches all errors and sends back a neat JSON message instead of a scary error page.
- Example: `{"timestamp": "...", "status": 404, "error": "Not Found", "message": "User not found with id 9"}`
- Our custom exceptions are UserNotFoundException, GroupNotFoundException, ExpenseNotFoundException, PaymentNotFoundException, InvalidSplitException and UserInUseException.
- Show: `exception/GlobalExceptionHandler.java`

---

## 6. Java questions

**What is a lambda?**
- A short, nameless function, for example `u -> u.getId()` or `(a, b) -> Double.compare(b.amount, a.amount)`.

**What are Streams?**
- A way to process a list step by step (filter, map, group, sum) without writing loops. We use them for balances and for filtering expenses by user.

**Which collections do you use?**
- `HashMap` stores totals per person. `ArrayList` holds lists of results. `PriorityQueue` holds the heaps used in settlement. `HashSet` holds group members and catches duplicate participants.

---

## 7. Testing questions

**How did you test the project?**
- **Postman / Newman:** 41 requests with 59 automatic checks covering success cases, error cases (400, 404, 409), payments and deletes. All passed.
- **JUnit:** one test runs the full scenario in code. It checks the balances, the settlements, the rollback and the payments.
- **Browser:** we used the web interface by hand.

**Why does Postman need the app running?**
- Postman is only a client. It sends requests to `localhost:8080`, so the Spring Boot server must be running.

---

## 8. "Show me where..." quick table

| They ask about | Open this file |
|---|---|
| Strategy pattern | `strategy/SplitStrategy.java`, `ExpenseService.java` (constructor) |
| Observer pattern | `observer/ExpenseEventPublisher.java`, `AuditLogger.java` |
| Singleton | `config/ConfigService.java` |
| `@Transactional` and rollback | `service/ExpenseService.java`, `addExpense()` |
| Streams (groupingBy) | `service/BalanceService.java`, `getBalances()` |
| PriorityQueue / settlement | `service/BalanceService.java`, `getSettlements()` |
| Error handling | `exception/GlobalExceptionHandler.java` |
| Relationships | `entity/Group.java` (`@ManyToMany`), `entity/Expense.java` |
| REST endpoints | `controller/` folder |
| Database settings | `src/main/resources/application.properties` |
