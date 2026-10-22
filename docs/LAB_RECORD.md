# Lab Record - Experiment 3.3: Expense Splitter (Spring Boot)

## 1. Project folder structure

```
expense-splitter/.gitignore
expense-splitter/mvnw
expense-splitter/mvnw.cmd
expense-splitter/pom.xml
expense-splitter/README.md
expense-splitter/.mvn/wrapper/maven-wrapper.properties
expense-splitter/docs/POSTMAN_TESTS.md
expense-splitter/docs/VIVA_CHEATSHEET.md
expense-splitter/postman/Expense-Splitter.postman_collection.json
expense-splitter/src/main/java/com/expensesplitter/ExpenseSplitterApplication.java
expense-splitter/src/main/java/com/expensesplitter/config/ConfigService.java
expense-splitter/src/main/java/com/expensesplitter/controller/ExpenseController.java
expense-splitter/src/main/java/com/expensesplitter/controller/GroupController.java
expense-splitter/src/main/java/com/expensesplitter/controller/UserController.java
expense-splitter/src/main/java/com/expensesplitter/dto/BalanceResponse.java
expense-splitter/src/main/java/com/expensesplitter/dto/ErrorResponse.java
expense-splitter/src/main/java/com/expensesplitter/dto/ExpenseRequest.java
expense-splitter/src/main/java/com/expensesplitter/dto/SettlementResponse.java
expense-splitter/src/main/java/com/expensesplitter/dto/SplitRequest.java
expense-splitter/src/main/java/com/expensesplitter/entity/ActivityLog.java
expense-splitter/src/main/java/com/expensesplitter/entity/Expense.java
expense-splitter/src/main/java/com/expensesplitter/entity/ExpenseShare.java
expense-splitter/src/main/java/com/expensesplitter/entity/Group.java
expense-splitter/src/main/java/com/expensesplitter/entity/User.java
expense-splitter/src/main/java/com/expensesplitter/exception/ExpenseNotFoundException.java
expense-splitter/src/main/java/com/expensesplitter/exception/GlobalExceptionHandler.java
expense-splitter/src/main/java/com/expensesplitter/exception/GroupNotFoundException.java
expense-splitter/src/main/java/com/expensesplitter/exception/InvalidSplitException.java
expense-splitter/src/main/java/com/expensesplitter/exception/UserInUseException.java
expense-splitter/src/main/java/com/expensesplitter/exception/UserNotFoundException.java
expense-splitter/src/main/java/com/expensesplitter/observer/AuditLogger.java
expense-splitter/src/main/java/com/expensesplitter/observer/ExpenseEventPublisher.java
expense-splitter/src/main/java/com/expensesplitter/observer/ExpenseListener.java
expense-splitter/src/main/java/com/expensesplitter/observer/NotificationService.java
expense-splitter/src/main/java/com/expensesplitter/repository/ActivityLogRepository.java
expense-splitter/src/main/java/com/expensesplitter/repository/ExpenseRepository.java
expense-splitter/src/main/java/com/expensesplitter/repository/ExpenseShareRepository.java
expense-splitter/src/main/java/com/expensesplitter/repository/GroupRepository.java
expense-splitter/src/main/java/com/expensesplitter/repository/UserRepository.java
expense-splitter/src/main/java/com/expensesplitter/service/BalanceService.java
expense-splitter/src/main/java/com/expensesplitter/service/ExpenseService.java
expense-splitter/src/main/java/com/expensesplitter/service/GroupService.java
expense-splitter/src/main/java/com/expensesplitter/service/UserService.java
expense-splitter/src/main/java/com/expensesplitter/strategy/EqualSplitStrategy.java
expense-splitter/src/main/java/com/expensesplitter/strategy/ExactSplitStrategy.java
expense-splitter/src/main/java/com/expensesplitter/strategy/PercentageSplitStrategy.java
expense-splitter/src/main/java/com/expensesplitter/strategy/SplitStrategy.java
expense-splitter/src/main/resources/application.properties
expense-splitter/src/main/resources/static/index.html
expense-splitter/src/test/java/com/expensesplitter/ExpenseSplitterApplicationTests.java
```

## 2. pom.xml

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Spring Boot parent gives us sensible defaults and dependency versions -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <groupId>com.expensesplitter</groupId>
    <artifactId>expense-splitter</artifactId>
    <version>1.0.0</version>
    <name>expense-splitter</name>
    <description>Splitwise-style Expense Splitter - PBL in Java, Experiment 3.3</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- REST APIs (@RestController, embedded Tomcat, Jackson JSON) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Data JPA + Hibernate -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- H2 in-memory database (default) -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- MySQL driver: uncomment this if you switch to MySQL in application.properties
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        -->

        <!-- JUnit 5 + Spring test support -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Lets us run "mvn spring-boot:run" and build an executable jar -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## 3. application.properties

### src/main/resources/application.properties

```properties
# ===================== SERVER =====================
spring.application.name=expense-splitter
server.port=8080

# ===================== H2 DATABASE (default) =====================
# In-memory DB: data is lost when the app stops (perfect for lab demos)
spring.datasource.url=jdbc:h2:mem:expensedb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 web console -> http://localhost:8080/h2-console  (JDBC URL: jdbc:h2:mem:expensedb)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# ===================== JPA / HIBERNATE =====================
# create-drop: tables are created on startup and dropped on shutdown
spring.jpa.hibernate.ddl-auto=create-drop
# Print the SQL Hibernate generates (useful to explain in viva)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
# Keep the DB session open while JSON is written, so lazy lists (members, shares) load
spring.jpa.open-in-view=true

# ===================== OPTIONAL: MYSQL =====================
# 1) Uncomment the mysql-connector-j dependency in pom.xml
# 2) Create the database:  CREATE DATABASE expensedb;
# 3) Comment out the H2 lines above and uncomment these:
# spring.datasource.url=jdbc:mysql://localhost:3306/expensedb
# spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# spring.datasource.username=root
# spring.datasource.password=your_password
# spring.jpa.hibernate.ddl-auto=update
```

## 4. Source code

### src/main/java/com/expensesplitter/config/ConfigService.java

```java
package com.expensesplitter.config;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * SINGLETON PATTERN (classic GoF version, not a Spring bean).
 * Only one ConfigService object exists in the whole JVM.
 *  - private constructor  -> nobody can call "new ConfigService()"
 *  - private static field -> holds the one instance
 *  - getInstance()        -> global access point (lazy + thread-safe)
 */
public class ConfigService {

    // volatile makes the instance visible correctly across threads
    private static volatile ConfigService instance;

    private final String currencySymbol;
    private final int decimalPlaces;

    // Smallest difference we treat as "zero" when comparing money (half a paisa)
    private final double tolerance;

    private ConfigService() {
        this.currencySymbol = "Rs";
        this.decimalPlaces = 2;
        this.tolerance = 0.01;
    }

    // Double-checked locking: synchronize only the first time
    public static ConfigService getInstance() {
        if (instance == null) {
            synchronized (ConfigService.class) {
                if (instance == null) {
                    instance = new ConfigService();
                }
            }
        }
        return instance;
    }

    /**
     * Rounds money to 2 decimals using BigDecimal (HALF_UP).
     * We use BigDecimal here because plain double math can give 0.1 + 0.2 = 0.30000000000000004.
     */
    public double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // 400 -> "Rs 400.00"
    public String format(double value) {
        return currencySymbol + " " + String.format("%." + decimalPlaces + "f", round(value));
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public double getTolerance() {
        return tolerance;
    }
}
```

### src/main/java/com/expensesplitter/controller/ExpenseController.java

```java
package com.expensesplitter.controller;

import com.expensesplitter.dto.ExpenseRequest;
import com.expensesplitter.entity.Expense;
import com.expensesplitter.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // POST /api/groups/1/expenses -> 201 Created, or 400 if the split is invalid
    @PostMapping("/groups/{groupId}/expenses")
    public ResponseEntity<Expense> addExpense(@PathVariable Long groupId, @RequestBody ExpenseRequest request) {
        return new ResponseEntity<>(expenseService.addExpense(groupId, request), HttpStatus.CREATED);
    }

    // GET /api/groups/1/expenses            -> all expenses
    // GET /api/groups/1/expenses?userId=2   -> only expenses involving user 2
    @GetMapping("/groups/{groupId}/expenses")
    public List<Expense> getExpenses(@PathVariable Long groupId,
                                     @RequestParam(required = false) Long userId) {
        return expenseService.getExpenses(groupId, userId);
    }

    // DELETE /api/expenses/5 -> 204 No Content
    @DeleteMapping("/expenses/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }
}
```

### src/main/java/com/expensesplitter/controller/GroupController.java

```java
package com.expensesplitter.controller;

import com.expensesplitter.dto.BalanceResponse;
import com.expensesplitter.dto.SettlementResponse;
import com.expensesplitter.entity.ActivityLog;
import com.expensesplitter.entity.Group;
import com.expensesplitter.service.BalanceService;
import com.expensesplitter.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private BalanceService balanceService;

    // POST /api/groups   body: {"name": "Goa Trip"} -> 201 Created
    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        return new ResponseEntity<>(groupService.createGroup(group), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{groupId}")
    public Group getGroup(@PathVariable Long groupId) {
        return groupService.getGroup(groupId);
    }

    // DELETE /api/groups/1 -> 204 No Content (also deletes its expenses and activity log)
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.noContent().build();
    }

    // POST /api/groups/1/members/2 -> 200 OK (returns the updated group)
    @PostMapping("/{groupId}/members/{userId}")
    public Group addMember(@PathVariable Long groupId, @PathVariable Long userId) {
        return groupService.addMember(groupId, userId);
    }

    // GET /api/groups/1/balances -> net balance of every member
    @GetMapping("/{groupId}/balances")
    public List<BalanceResponse> getBalances(@PathVariable Long groupId) {
        return balanceService.getBalances(groupId);
    }

    // GET /api/groups/1/settlements -> minimum list of payments
    @GetMapping("/{groupId}/settlements")
    public List<SettlementResponse> getSettlements(@PathVariable Long groupId) {
        return balanceService.getSettlements(groupId);
    }

    // GET /api/groups/1/activity -> audit log written by the AuditLogger observer
    @GetMapping("/{groupId}/activity")
    public List<ActivityLog> getActivity(@PathVariable Long groupId) {
        return groupService.getActivity(groupId);
    }
}
```

### src/main/java/com/expensesplitter/controller/UserController.java

```java
package com.expensesplitter.controller;

import com.expensesplitter.entity.User;
import com.expensesplitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @RestController = @Controller + @ResponseBody (return values become JSON)
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // POST /api/users   body: {"name": "Aman", "email": "aman@mail.com"}  -> 201 Created
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    // GET /api/users -> 200 OK
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET /api/users/1 -> 200 OK or 404
    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    // DELETE /api/users/1 -> 204 No Content, 404 if missing, 409 if still in expenses
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
```

### src/main/java/com/expensesplitter/dto/BalanceResponse.java

```java
package com.expensesplitter.dto;

/**
 * Net balance of one member in a group.
 * balance > 0 -> gets money back, balance < 0 -> owes money, 0 -> settled
 */
public class BalanceResponse {

    private Long userId;
    private String name;
    private double balance;
    private String status;

    public BalanceResponse() {
    }

    public BalanceResponse(Long userId, String name, double balance, String status) {
        this.userId = userId;
        this.name = name;
        this.balance = balance;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
```

### src/main/java/com/expensesplitter/dto/ErrorResponse.java

```java
package com.expensesplitter.dto;

import java.time.LocalDateTime;

/**
 * Clean JSON error body returned by GlobalExceptionHandler.
 * { "timestamp": "...", "status": 404, "error": "Not Found", "message": "User not found with id 9" }
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    public ErrorResponse(int status, String error, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
```

### src/main/java/com/expensesplitter/dto/ExpenseRequest.java

```java
package com.expensesplitter.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON body for POST /api/groups/{groupId}/expenses
 * {
 *   "description": "Dinner",
 *   "amount": 1200,
 *   "paidBy": 1,
 *   "splitType": "EQUAL",
 *   "splits": [ {"userId": 1}, {"userId": 2} ]
 * }
 */
public class ExpenseRequest {

    private String description;
    private Double amount;
    private Long paidBy;
    private String splitType;
    private List<SplitRequest> splits = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(Long paidBy) {
        this.paidBy = paidBy;
    }

    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    public List<SplitRequest> getSplits() {
        return splits;
    }

    public void setSplits(List<SplitRequest> splits) {
        this.splits = splits;
    }
}
```

### src/main/java/com/expensesplitter/dto/SettlementResponse.java

```java
package com.expensesplitter.dto;

/**
 * One payment needed to settle the group, e.g. "Diya pays Aman Rs 400.00"
 */
public class SettlementResponse {

    private Long fromUserId;
    private String fromUser;
    private Long toUserId;
    private String toUser;
    private double amount;
    private String message;

    public SettlementResponse() {
    }

    public SettlementResponse(Long fromUserId, String fromUser, Long toUserId, String toUser,
                              double amount, String message) {
        this.fromUserId = fromUserId;
        this.fromUser = fromUser;
        this.toUserId = toUserId;
        this.toUser = toUser;
        this.amount = amount;
        this.message = message;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

### src/main/java/com/expensesplitter/dto/SplitRequest.java

```java
package com.expensesplitter.dto;

/**
 * One participant of an expense.
 * value meaning depends on split type:
 *   EQUAL      -> ignored (can be left out)
 *   PERCENTAGE -> percent, e.g. 25
 *   EXACT      -> amount,  e.g. 250
 */
public class SplitRequest {

    private Long userId;
    private Double value;

    public SplitRequest() {
    }

    public SplitRequest(Long userId, Double value) {
        this.userId = userId;
        this.value = value;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
```

### src/main/java/com/expensesplitter/entity/ActivityLog.java

```java
package com.expensesplitter.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Audit trail entry written by the AuditLogger observer.
 * Example message: "Aman added expense 'Dinner' of Rs 1200.00"
 */
@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Plain id (not a relation) so logs survive even if the expense is deleted
    private Long groupId;

    @Column(nullable = false, length = 500)
    private String message;

    private LocalDateTime createdAt;

    public ActivityLog() {
    }

    public ActivityLog(Long groupId, String message) {
        this.groupId = groupId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
```

### src/main/java/com/expensesplitter/entity/Expense.java

```java
package com.expensesplitter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * One bill paid by one user, split among several users.
 */
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double amount;

    // EQUAL, PERCENTAGE or EXACT
    @Column(nullable = false)
    private String splitType;

    private LocalDateTime createdAt;

    // Many expenses can be paid by one user
    @ManyToOne
    @JoinColumn(name = "paid_by_id", nullable = false)
    private User paidBy;

    // Many expenses belong to one group (hidden in JSON, we expose groupId instead)
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonIgnore
    private Group group;

    // One expense has many shares. cascade = ALL: saving/deleting the expense
    // also saves/deletes its shares. orphanRemoval removes shares taken out of the list.
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShare> shares = new ArrayList<>();

    public Expense() {
    }

    public Expense(String description, double amount, String splitType, User paidBy, Group group) {
        this.description = description;
        this.amount = amount;
        this.splitType = splitType;
        this.paidBy = paidBy;
        this.group = group;
        this.createdAt = LocalDateTime.now();
    }

    // Helper that keeps both sides of the relationship in sync
    public void addShare(ExpenseShare share) {
        shares.add(share);
        share.setExpense(this);
    }

    // Shown in JSON as "groupId"
    public Long getGroupId() {
        return group == null ? null : group.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(User paidBy) {
        this.paidBy = paidBy;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<ExpenseShare> getShares() {
        return shares;
    }

    public void setShares(List<ExpenseShare> shares) {
        this.shares = shares;
    }
}
```

### src/main/java/com/expensesplitter/entity/ExpenseShare.java

```java
package com.expensesplitter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * How much ONE user owes for ONE expense.
 * Example: Dinner Rs 1200 split equally among 4 -> four rows of Rs 300.
 */
@Entity
@Table(name = "expense_shares")
public class ExpenseShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Back-reference to the parent expense (ignored in JSON to avoid loops)
    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    @JsonIgnore
    private Expense expense;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double shareAmount;

    public ExpenseShare() {
    }

    public ExpenseShare(User user, double shareAmount) {
        this.user = user;
        this.shareAmount = shareAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(double shareAmount) {
        this.shareAmount = shareAmount;
    }
}
```

### src/main/java/com/expensesplitter/entity/Group.java

```java
package com.expensesplitter.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * A group of users who share expenses (e.g. "Goa Trip").
 * Table is named "expense_groups" because "group" is a reserved SQL word.
 */
@Entity
@Table(name = "expense_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Owning side of User <-> Group many-to-many.
    // Creates join table group_members(group_id, user_id).
    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members = new HashSet<>();

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    // Helper that keeps both sides of the relationship in sync
    public void addMember(User user) {
        members.add(user);
        user.getGroups().add(this);
    }

    // Used to check membership by id (works even if objects are different instances)
    public boolean hasMember(Long userId) {
        return members.stream().anyMatch(u -> u.getId().equals(userId));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }
}
```

### src/main/java/com/expensesplitter/entity/User.java

```java
package com.expensesplitter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * A person who can join groups and share expenses.
 * Table is named "users" because "user" is a reserved word in H2/MySQL.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Inverse side of the many-to-many. Group owns the join table.
    // @JsonIgnore stops infinite JSON loops (user -> groups -> members -> user ...)
    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private Set<Group> groups = new HashSet<>();

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
}
```

### src/main/java/com/expensesplitter/exception/ExpenseNotFoundException.java

```java
package com.expensesplitter.exception;

public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(Long id) {
        super("Expense not found with id " + id);
    }
}
```

### src/main/java/com/expensesplitter/exception/GlobalExceptionHandler.java

```java
package com.expensesplitter.exception;

import com.expensesplitter.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * @ControllerAdvice = one place that catches exceptions thrown by ANY controller
 * and turns them into a clean JSON error with the right HTTP status.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // 404 - something with the given id does not exist
    @ExceptionHandler({UserNotFoundException.class, GroupNotFoundException.class, ExpenseNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 400 - split rules broken
    @ExceptionHandler(InvalidSplitException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSplit(InvalidSplitException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 409 - user cannot be deleted while expenses still refer to them
    @ExceptionHandler(UserInUseException.class)
    public ResponseEntity<ErrorResponse> handleUserInUse(UserInUseException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 400 - bad input such as empty name or duplicate email
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadInput(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 400 - broken JSON body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadJson(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Request body is missing or is not valid JSON");
    }

    // 400 - e.g. /api/groups/abc/balances (abc is not a number)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid value '" + ex.getValue() + "' for " + ex.getName());
    }

    // 404 - URL does not match any endpoint, e.g. DELETE /api/expenses/ (id missing)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoEndpoint(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "No endpoint at /" + ex.getResourcePath() + " - check the URL and the id");
    }

    // 405 - right URL, wrong HTTP method (e.g. GET /api/expenses/1)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleWrongMethod(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMethod() + " is not supported here. Allowed: "
                + ex.getSupportedHttpMethods());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), message);
        return new ResponseEntity<>(body, status);
    }
}
```

### src/main/java/com/expensesplitter/exception/GroupNotFoundException.java

```java
package com.expensesplitter.exception;

public class GroupNotFoundException extends RuntimeException {

    public GroupNotFoundException(Long id) {
        super("Group not found with id " + id);
    }
}
```

### src/main/java/com/expensesplitter/exception/InvalidSplitException.java

```java
package com.expensesplitter.exception;

// Thrown when a split is wrong (percent != 100, exact total != amount, non-member, ...)
public class InvalidSplitException extends RuntimeException {

    public InvalidSplitException(String message) {
        super(message);
    }
}
```

### src/main/java/com/expensesplitter/exception/UserInUseException.java

```java
package com.expensesplitter.exception;

// Thrown when deleting a user who still appears in expenses (mapped to 409 Conflict)
public class UserInUseException extends RuntimeException {

    public UserInUseException(String message) {
        super(message);
    }
}
```

### src/main/java/com/expensesplitter/exception/UserNotFoundException.java

```java
package com.expensesplitter.exception;

// Unchecked (RuntimeException) so it also triggers @Transactional rollback automatically
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User not found with id " + id);
    }
}
```

### src/main/java/com/expensesplitter/ExpenseSplitterApplication.java

```java
package com.expensesplitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the application.
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 */
@SpringBootApplication
public class ExpenseSplitterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseSplitterApplication.class, args);
    }
}
```

### src/main/java/com/expensesplitter/observer/AuditLogger.java

```java
package com.expensesplitter.observer;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.entity.ActivityLog;
import com.expensesplitter.entity.Expense;
import com.expensesplitter.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Observer #1: stores an activity log row in the database.
 * It runs inside the same @Transactional addExpense call, so if anything fails
 * the log row is rolled back together with the expense.
 */
@Component
public class AuditLogger implements ExpenseListener {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private final ConfigService config = ConfigService.getInstance();

    @Override
    public void onExpenseAdded(Expense expense) {
        String message = expense.getPaidBy().getName() + " added expense '" + expense.getDescription()
                + "' of " + config.format(expense.getAmount()) + " (" + expense.getSplitType() + " split)";
        activityLogRepository.save(new ActivityLog(expense.getGroupId(), message));
    }

    @Override
    public void onExpenseDeleted(Expense expense) {
        String message = "Expense '" + expense.getDescription() + "' of "
                + config.format(expense.getAmount()) + " was deleted";
        activityLogRepository.save(new ActivityLog(expense.getGroupId(), message));
    }
}
```

### src/main/java/com/expensesplitter/observer/ExpenseEventPublisher.java

```java
package com.expensesplitter.observer;

import com.expensesplitter.entity.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER PATTERN: the "subject".
 * Spring finds every bean that implements ExpenseListener and injects them as a list.
 * Adding a new listener later = just create a new @Component class, no change here.
 */
@Component
public class ExpenseEventPublisher {

    private final List<ExpenseListener> listeners = new ArrayList<>();

    @Autowired
    public void setListeners(List<ExpenseListener> listeners) {
        this.listeners.addAll(listeners);
    }

    public void notifyExpenseAdded(Expense expense) {
        listeners.forEach(listener -> listener.onExpenseAdded(expense));
    }

    public void notifyExpenseDeleted(Expense expense) {
        listeners.forEach(listener -> listener.onExpenseDeleted(expense));
    }
}
```

### src/main/java/com/expensesplitter/observer/ExpenseListener.java

```java
package com.expensesplitter.observer;

import com.expensesplitter.entity.Expense;

/**
 * OBSERVER PATTERN: the "observer" interface.
 * Any class that wants to react to expense events implements this.
 */
public interface ExpenseListener {

    void onExpenseAdded(Expense expense);

    // Java 8 default method: listeners may ignore deletes if they want
    default void onExpenseDeleted(Expense expense) {
    }
}
```

### src/main/java/com/expensesplitter/observer/NotificationService.java

```java
package com.expensesplitter.observer;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.entity.Expense;
import org.springframework.stereotype.Service;

/**
 * Observer #2: "notifies" the group. In a real app this could send an email or push
 * notification; here we just print to the console.
 */
@Service
public class NotificationService implements ExpenseListener {

    private final ConfigService config = ConfigService.getInstance();

    @Override
    public void onExpenseAdded(Expense expense) {
        System.out.println("[NOTIFICATION] " + expense.getPaidBy().getName()
                + " added expense " + expense.getDescription()
                + " (" + config.format(expense.getAmount()) + ")");
    }
}
```

### src/main/java/com/expensesplitter/repository/ActivityLogRepository.java

```java
package com.expensesplitter.repository;

import com.expensesplitter.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    // Derived delete query: DELETE FROM activity_logs WHERE group_id = ?
    void deleteByGroupId(Long groupId);
}
```

### src/main/java/com/expensesplitter/repository/ExpenseRepository.java

```java
package com.expensesplitter.repository;

import com.expensesplitter.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Derived query: SELECT * FROM expenses WHERE group_id = ? ORDER BY created_at
    // "Group_Id" means: follow the 'group' relation, then use its 'id'
    List<Expense> findByGroup_IdOrderByCreatedAtAsc(Long groupId);

    // How many expenses this user paid for (used before deleting a user)
    long countByPaidBy_Id(Long userId);
}
```

### src/main/java/com/expensesplitter/repository/ExpenseShareRepository.java

```java
package com.expensesplitter.repository;

import com.expensesplitter.entity.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {

    // How many expense shares belong to this user (used before deleting a user)
    long countByUser_Id(Long userId);
}
```

### src/main/java/com/expensesplitter/repository/GroupRepository.java

```java
package com.expensesplitter.repository;

import com.expensesplitter.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
```

### src/main/java/com/expensesplitter/repository/UserRepository.java

```java
package com.expensesplitter.repository;

import com.expensesplitter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository gives save(), findById(), findAll(), deleteById() ... for free.
 * Spring Data generates the SQL for existsByEmail from the method name.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
```

### src/main/java/com/expensesplitter/service/BalanceService.java

```java
package com.expensesplitter.service;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.BalanceResponse;
import com.expensesplitter.dto.SettlementResponse;
import com.expensesplitter.entity.Expense;
import com.expensesplitter.entity.ExpenseShare;
import com.expensesplitter.entity.Group;
import com.expensesplitter.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@Service
public class BalanceService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private GroupService groupService;

    private final ConfigService config = ConfigService.getInstance();

    /**
     * Net balance = (total this member PAID) - (total this member OWES).
     * Positive -> others owe them. Negative -> they owe others.
     */
    @Transactional(readOnly = true)
    public List<BalanceResponse> getBalances(Long groupId) {
        Group group = groupService.getGroup(groupId);
        List<Expense> expenses = expenseRepository.findByGroup_IdOrderByCreatedAtAsc(groupId);

        // userId -> total paid   (Streams: groupingBy into a HashMap + summingDouble)
        Map<Long, Double> paid = expenses.stream()
                .collect(Collectors.groupingBy(e -> e.getPaidBy().getId(), HashMap::new,
                        Collectors.summingDouble(Expense::getAmount)));

        // userId -> total owed   (flatMap turns each expense into its list of shares)
        Map<Long, Double> owed = expenses.stream()
                .flatMap(e -> e.getShares().stream())
                .collect(Collectors.groupingBy(s -> s.getUser().getId(), HashMap::new,
                        Collectors.summingDouble(ExpenseShare::getShareAmount)));

        // Build one row per member, sorted from highest balance to lowest
        return group.getMembers().stream()
                .map(member -> {
                    double net = config.round(paid.getOrDefault(member.getId(), 0.0)
                            - owed.getOrDefault(member.getId(), 0.0));
                    return new BalanceResponse(member.getId(), member.getName(), net, statusOf(net));
                })
                .sorted(Comparator.comparingDouble(BalanceResponse::getBalance).reversed()
                        .thenComparing(BalanceResponse::getName))
                .collect(Collectors.toList());
    }

    private String statusOf(double net) {
        if (net > config.getTolerance()) {
            return "gets back " + config.format(net);
        } else if (net < -config.getTolerance()) {
            return "owes " + config.format(-net);
        }
        return "settled up";
    }

    /**
     * Minimum cash-flow settlement (greedy).
     * 1. Put creditors (balance > 0) in a max-heap and debtors (balance < 0) in a max-heap
     *    of how much they owe.
     * 2. Repeatedly match the BIGGEST creditor with the BIGGEST debtor.
     *    The debtor pays min(credit, debt). Whoever still has money left goes back in the heap.
     * 3. Every step settles at least one person, so we need at most (n - 1) payments.
     */
    @Transactional(readOnly = true)
    public List<SettlementResponse> getSettlements(Long groupId) {
        List<BalanceResponse> balances = getBalances(groupId);

        // Max-heaps: the lambda comparator puts the LARGEST amount at the top
        PriorityQueue<Party> creditors = new PriorityQueue<>((a, b) -> Double.compare(b.amount, a.amount));
        PriorityQueue<Party> debtors = new PriorityQueue<>((a, b) -> Double.compare(b.amount, a.amount));

        for (BalanceResponse b : balances) {
            if (b.getBalance() > config.getTolerance()) {
                creditors.add(new Party(b.getUserId(), b.getName(), b.getBalance()));
            } else if (b.getBalance() < -config.getTolerance()) {
                debtors.add(new Party(b.getUserId(), b.getName(), -b.getBalance()));
            }
        }

        List<SettlementResponse> result = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Party creditor = creditors.poll();
            Party debtor = debtors.poll();

            double pay = config.round(Math.min(creditor.amount, debtor.amount));
            String message = debtor.name + " pays " + creditor.name + " " + config.format(pay);
            result.add(new SettlementResponse(debtor.userId, debtor.name,
                    creditor.userId, creditor.name, pay, message));

            // Whoever still has money left goes back into their heap
            creditor.amount = config.round(creditor.amount - pay);
            debtor.amount = config.round(debtor.amount - pay);
            if (creditor.amount > config.getTolerance()) {
                creditors.add(creditor);
            }
            if (debtor.amount > config.getTolerance()) {
                debtors.add(debtor);
            }
        }
        return result;
    }

    // Small helper used only inside the settlement algorithm
    private static class Party {
        Long userId;
        String name;
        double amount; // how much this person should receive (creditor) or pay (debtor)

        Party(Long userId, String name, double amount) {
            this.userId = userId;
            this.name = name;
            this.amount = amount;
        }
    }
}
```

### src/main/java/com/expensesplitter/service/ExpenseService.java

```java
package com.expensesplitter.service;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.ExpenseRequest;
import com.expensesplitter.dto.SplitRequest;
import com.expensesplitter.entity.Expense;
import com.expensesplitter.entity.ExpenseShare;
import com.expensesplitter.entity.Group;
import com.expensesplitter.entity.User;
import com.expensesplitter.exception.ExpenseNotFoundException;
import com.expensesplitter.exception.InvalidSplitException;
import com.expensesplitter.observer.ExpenseEventPublisher;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.strategy.SplitStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final ExpenseEventPublisher eventPublisher;

    // STRATEGY PATTERN: Spring injects every SplitStrategy bean into this map,
    // keyed by bean name -> {"EQUAL": EqualSplitStrategy, "PERCENTAGE": ..., "EXACT": ...}
    private final Map<String, SplitStrategy> strategies;

    private final ConfigService config = ConfigService.getInstance();

    // CONSTRUCTOR INJECTION: dependencies are final and passed in by Spring.
    // With a single constructor, @Autowired is optional (Spring uses it automatically).
    public ExpenseService(ExpenseRepository expenseRepository,
                          GroupService groupService,
                          UserService userService,
                          ExpenseEventPublisher eventPublisher,
                          Map<String, SplitStrategy> strategies) {
        this.expenseRepository = expenseRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.strategies = strategies;
    }

    /**
     * Saves expense + shares + activity log as ONE unit of work.
     * If any step throws a RuntimeException (e.g. InvalidSplitException),
     * Spring rolls back everything, so no half-saved expense is left in the DB.
     */
    @Transactional
    public Expense addExpense(Long groupId, ExpenseRequest request) {
        Group group = groupService.getGroup(groupId);

        // ---- 1. Basic validation ----
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new InvalidSplitException("Amount must be greater than 0");
        }
        if (request.getPaidBy() == null) {
            throw new IllegalArgumentException("paidBy (user id) is required");
        }
        User paidBy = userService.getUser(request.getPaidBy());
        if (!group.hasMember(paidBy.getId())) {
            throw new InvalidSplitException(paidBy.getName() + " is not a member of " + group.getName());
        }

        // ---- 2. Pick the strategy at runtime from the request body ----
        String splitType = request.getSplitType() == null ? "" : request.getSplitType().trim().toUpperCase();
        SplitStrategy strategy = strategies.get(splitType);
        if (strategy == null) {
            throw new InvalidSplitException("Unknown split type '" + request.getSplitType()
                    + "'. Use one of " + strategies.keySet());
        }

        List<SplitRequest> splits = request.getSplits();
        validateParticipants(group, splits);

        double amount = config.round(request.getAmount());

        // ---- 3. Save the expense first ----
        Expense expense = new Expense(request.getDescription().trim(), amount, splitType, paidBy, group);
        expense = expenseRepository.save(expense);

        // ---- 4. Calculate shares. If the split is invalid this throws and
        //         the expense saved in step 3 is ROLLED BACK. ----
        Map<Long, Double> shares = strategy.calculateShares(amount, splits);

        // ---- 5. Save shares (cascade = ALL saves them with the expense) ----
        for (Map.Entry<Long, Double> entry : shares.entrySet()) {
            User user = userService.getUser(entry.getKey());
            expense.addShare(new ExpenseShare(user, entry.getValue()));
        }
        expense = expenseRepository.save(expense);

        // ---- 6. OBSERVER PATTERN: tell all listeners (AuditLogger, NotificationService) ----
        eventPublisher.notifyExpenseAdded(expense);

        return expense;
    }

    // Every participant must exist, be in the group, and appear only once
    private void validateParticipants(Group group, List<SplitRequest> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("At least one participant is required in 'splits'");
        }
        Set<Long> seen = new HashSet<>();
        for (SplitRequest split : splits) {
            if (split.getUserId() == null) {
                throw new InvalidSplitException("Every split needs a userId");
            }
            User user = userService.getUser(split.getUserId());
            if (!group.hasMember(user.getId())) {
                throw new InvalidSplitException(user.getName() + " is not a member of " + group.getName());
            }
            if (!seen.add(user.getId())) {
                throw new InvalidSplitException(user.getName() + " appears more than once in the split");
            }
        }
    }

    /**
     * All expenses of a group. If userId is given, keep only expenses that the user
     * paid for OR has a share in (filtered with Streams + lambdas).
     */
    @Transactional(readOnly = true)
    public List<Expense> getExpenses(Long groupId, Long userId) {
        groupService.getGroup(groupId);
        List<Expense> expenses = expenseRepository.findByGroup_IdOrderByCreatedAtAsc(groupId);

        if (userId == null) {
            return expenses;
        }
        userService.getUser(userId); // 404 if the user does not exist

        return expenses.stream()
                .filter(e -> e.getPaidBy().getId().equals(userId)
                        || e.getShares().stream().anyMatch(s -> s.getUser().getId().equals(userId)))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteExpense(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
        expenseRepository.delete(expense); // shares are deleted too (cascade)
        eventPublisher.notifyExpenseDeleted(expense);
    }
}
```

### src/main/java/com/expensesplitter/service/GroupService.java

```java
package com.expensesplitter.service;

import com.expensesplitter.entity.ActivityLog;
import com.expensesplitter.entity.Group;
import com.expensesplitter.entity.User;
import com.expensesplitter.exception.GroupNotFoundException;
import com.expensesplitter.repository.ActivityLogRepository;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public Group createGroup(Group group) {
        if (group.getName() == null || group.getName().isBlank()) {
            throw new IllegalArgumentException("Group name is required");
        }
        Group saved = groupRepository.save(new Group(group.getName().trim()));
        activityLogRepository.save(new ActivityLog(saved.getId(), "Group '" + saved.getName() + "' created"));
        return saved;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroup(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException(id));
    }

    @Transactional
    public Group addMember(Long groupId, Long userId) {
        Group group = getGroup(groupId);
        User user = userService.getUser(userId);

        if (group.hasMember(userId)) {
            throw new IllegalArgumentException(user.getName() + " is already a member of " + group.getName());
        }
        group.addMember(user);
        activityLogRepository.save(new ActivityLog(groupId, user.getName() + " joined the group"));
        return groupRepository.save(group);
    }

    /**
     * Deletes a group with everything inside it: expenses (and their shares, by cascade),
     * member links and the activity log. Users themselves are kept.
     * @Transactional: if any step fails, nothing is deleted.
     */
    @Transactional
    public void deleteGroup(Long groupId) {
        Group group = getGroup(groupId);
        expenseRepository.deleteAll(expenseRepository.findByGroup_IdOrderByCreatedAtAsc(groupId));
        for (User member : group.getMembers()) {
            member.getGroups().remove(group);
        }
        group.getMembers().clear();
        activityLogRepository.deleteByGroupId(groupId);
        groupRepository.delete(group);
    }

    public List<ActivityLog> getActivity(Long groupId) {
        getGroup(groupId); // 404 if the group does not exist
        return activityLogRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
    }
}
```

### src/main/java/com/expensesplitter/service/UserService.java

```java
package com.expensesplitter.service;

import com.expensesplitter.entity.Group;
import com.expensesplitter.entity.User;
import com.expensesplitter.exception.UserInUseException;
import com.expensesplitter.exception.UserNotFoundException;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.repository.ExpenseShareRepository;
import com.expensesplitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    // Field injection (simple style). ExpenseService shows constructor injection.
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("A valid email is required");
        }
        String email = user.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        return userRepository.save(new User(user.getName().trim(), email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        // orElseThrow with a lambda: throw our custom exception if not found
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Deletes a user and removes them from all their groups.
     * Refused (409) if the user paid for or shares in any expense, because deleting
     * those expenses would silently change everyone else's balances.
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);

        long involved = expenseRepository.countByPaidBy_Id(id) + expenseShareRepository.countByUser_Id(id);
        if (involved > 0) {
            throw new UserInUseException(user.getName() + " is still part of expenses. "
                    + "Delete those expenses (or the whole group) first.");
        }

        // Remove the user from the owning side (Group.members) so join-table rows are deleted.
        // Copy into a new list first because we change the set while looping.
        for (Group group : new ArrayList<>(user.getGroups())) {
            group.getMembers().remove(user);
        }
        user.getGroups().clear();
        userRepository.delete(user);
    }
}
```

### src/main/java/com/expensesplitter/strategy/EqualSplitStrategy.java

```java
package com.expensesplitter.strategy;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.SplitRequest;
import com.expensesplitter.exception.InvalidSplitException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * EQUAL: amount / number of participants.
 * Rounding leftover (e.g. 1000 / 3 = 333.33 x 3 = 999.99) is added to the first person
 * so that the shares always add up to the exact amount.
 */
@Component("EQUAL")
public class EqualSplitStrategy implements SplitStrategy {

    private final ConfigService config = ConfigService.getInstance();

    @Override
    public Map<Long, Double> calculateShares(double amount, List<SplitRequest> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("EQUAL split needs at least one participant");
        }

        double each = config.round(amount / splits.size());
        double leftover = config.round(amount - each * splits.size());

        // LinkedHashMap keeps the participants in the order they were sent
        Map<Long, Double> shares = new LinkedHashMap<>();
        for (SplitRequest split : splits) {
            shares.put(split.getUserId(), each);
        }

        Long firstUser = splits.get(0).getUserId();
        shares.put(firstUser, config.round(shares.get(firstUser) + leftover));
        return shares;
    }
}
```

### src/main/java/com/expensesplitter/strategy/ExactSplitStrategy.java

```java
package com.expensesplitter.strategy;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.SplitRequest;
import com.expensesplitter.exception.InvalidSplitException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * EXACT: each participant's amount is given directly. Amounts must total the expense amount.
 */
@Component("EXACT")
public class ExactSplitStrategy implements SplitStrategy {

    private final ConfigService config = ConfigService.getInstance();

    @Override
    public Map<Long, Double> calculateShares(double amount, List<SplitRequest> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("EXACT split needs at least one participant");
        }

        boolean missingValue = splits.stream().anyMatch(s -> s.getValue() == null || s.getValue() < 0);
        if (missingValue) {
            throw new InvalidSplitException("Every participant needs an exact amount of 0 or more");
        }

        double total = splits.stream().mapToDouble(SplitRequest::getValue).sum();
        if (Math.abs(total - amount) > config.getTolerance()) {
            throw new InvalidSplitException("Exact amounts add up to " + config.format(total)
                    + " but the expense is " + config.format(amount));
        }

        Map<Long, Double> shares = new LinkedHashMap<>();
        splits.forEach(s -> shares.put(s.getUserId(), config.round(s.getValue())));
        return shares;
    }
}
```

### src/main/java/com/expensesplitter/strategy/PercentageSplitStrategy.java

```java
package com.expensesplitter.strategy;

import com.expensesplitter.config.ConfigService;
import com.expensesplitter.dto.SplitRequest;
import com.expensesplitter.exception.InvalidSplitException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PERCENTAGE: each participant pays value% of the amount. Percents must total 100.
 */
@Component("PERCENTAGE")
public class PercentageSplitStrategy implements SplitStrategy {

    private final ConfigService config = ConfigService.getInstance();

    @Override
    public Map<Long, Double> calculateShares(double amount, List<SplitRequest> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("PERCENTAGE split needs at least one participant");
        }

        // Every participant must send a non-negative percent
        boolean missingValue = splits.stream().anyMatch(s -> s.getValue() == null || s.getValue() < 0);
        if (missingValue) {
            throw new InvalidSplitException("Every participant needs a percentage of 0 or more");
        }

        // Stream + lambda to add up all the percents
        double totalPercent = splits.stream().mapToDouble(SplitRequest::getValue).sum();
        if (Math.abs(totalPercent - 100) > config.getTolerance()) {
            throw new InvalidSplitException("Percentages must add up to 100, but got " + config.round(totalPercent));
        }

        Map<Long, Double> shares = new LinkedHashMap<>();
        double assigned = 0;
        for (SplitRequest split : splits) {
            double share = config.round(amount * split.getValue() / 100);
            shares.put(split.getUserId(), share);
            assigned += share;
        }

        // Put any rounding leftover on the last participant so the total is exact
        Long lastUser = splits.get(splits.size() - 1).getUserId();
        shares.put(lastUser, config.round(shares.get(lastUser) + (amount - assigned)));
        return shares;
    }
}
```

### src/main/java/com/expensesplitter/strategy/SplitStrategy.java

```java
package com.expensesplitter.strategy;

import com.expensesplitter.dto.SplitRequest;

import java.util.List;
import java.util.Map;

/**
 * STRATEGY PATTERN: one interface, many interchangeable algorithms.
 * Each implementation is a Spring bean whose name is the split type
 * ("EQUAL", "PERCENTAGE", "EXACT"). ExpenseService receives all of them
 * as a Map<String, SplitStrategy> and picks one at runtime.
 */
public interface SplitStrategy {

    /**
     * @param amount total expense amount
     * @param splits participants (and their value, if the type needs one)
     * @return map of userId -> amount that user owes (rounded to 2 decimals)
     * @throws com.expensesplitter.exception.InvalidSplitException if the split is not valid
     */
    Map<Long, Double> calculateShares(double amount, List<SplitRequest> splits);
}
```

### src/test/java/com/expensesplitter/ExpenseSplitterApplicationTests.java

```java
package com.expensesplitter;

import com.expensesplitter.dto.BalanceResponse;
import com.expensesplitter.dto.ExpenseRequest;
import com.expensesplitter.dto.SettlementResponse;
import com.expensesplitter.dto.SplitRequest;
import com.expensesplitter.entity.Group;
import com.expensesplitter.entity.User;
import com.expensesplitter.exception.InvalidSplitException;
import com.expensesplitter.repository.ExpenseRepository;
import com.expensesplitter.service.BalanceService;
import com.expensesplitter.service.ExpenseService;
import com.expensesplitter.service.GroupService;
import com.expensesplitter.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs the same scenario as the Postman test sequence and checks the numbers.
 */
@SpringBootTest
class ExpenseSplitterApplicationTests {

    @Autowired private UserService userService;
    @Autowired private GroupService groupService;
    @Autowired private ExpenseService expenseService;
    @Autowired private BalanceService balanceService;
    @Autowired private ExpenseRepository expenseRepository;

    @Test
    void fullScenarioGivesExpectedBalancesAndSettlements() {
        User a = userService.createUser(new User("Aman", "aman@test.com"));
        User b = userService.createUser(new User("Bhavya", "bhavya@test.com"));
        User c = userService.createUser(new User("Chirag", "chirag@test.com"));
        User d = userService.createUser(new User("Diya", "diya@test.com"));

        Group g = groupService.createGroup(new Group("Goa Trip"));
        for (User u : List.of(a, b, c, d)) {
            groupService.addMember(g.getId(), u.getId());
        }

        expenseService.addExpense(g.getId(), request("Dinner", 1200, a, "EQUAL",
                split(a, null), split(b, null), split(c, null), split(d, null)));
        expenseService.addExpense(g.getId(), request("Cab", 1000, b, "PERCENTAGE",
                split(a, 25.0), split(b, 25.0), split(c, 30.0), split(d, 20.0)));
        expenseService.addExpense(g.getId(), request("Movie", 1000, c, "EXACT",
                split(a, 250.0), split(b, 300.0), split(c, 200.0), split(d, 250.0)));

        Map<String, Double> balances = balanceService.getBalances(g.getId()).stream()
                .collect(Collectors.toMap(BalanceResponse::getName, BalanceResponse::getBalance));
        assertEquals(400.0, balances.get("Aman"));
        assertEquals(150.0, balances.get("Bhavya"));
        assertEquals(200.0, balances.get("Chirag"));
        assertEquals(-750.0, balances.get("Diya"));

        List<String> settlements = balanceService.getSettlements(g.getId()).stream()
                .map(SettlementResponse::getMessage).collect(Collectors.toList());
        assertEquals(List.of("Diya pays Aman Rs 400.00", "Diya pays Chirag Rs 200.00",
                "Diya pays Bhavya Rs 150.00"), settlements);

        // Invalid split (percent total 90) must throw and leave NO extra expense behind (rollback)
        long before = expenseRepository.count();
        assertThrows(InvalidSplitException.class, () -> expenseService.addExpense(g.getId(),
                request("Bad", 500, a, "PERCENTAGE", split(a, 50.0), split(b, 40.0))));
        assertEquals(before, expenseRepository.count());
    }

    private ExpenseRequest request(String desc, double amount, User paidBy, String type, SplitRequest... splits) {
        ExpenseRequest r = new ExpenseRequest();
        r.setDescription(desc);
        r.setAmount(amount);
        r.setPaidBy(paidBy.getId());
        r.setSplitType(type);
        r.setSplits(List.of(splits));
        return r;
    }

    private SplitRequest split(User u, Double value) {
        return new SplitRequest(u.getId(), value);
    }
}
```

### Web interface: src/main/resources/static/index.html

(Single-file demo UI, see the project folder.)

## 5. Postman test sequence

### Postman test sequence

Base URL: `http://localhost:8080`. For every POST with a body, set the header `Content-Type: application/json`
(Body â†’ raw â†’ JSON in Postman).

The ready-made collection `postman/Expense-Splitter.postman_collection.json` contains all of these
requests with automatic tests. The IDs below assume a freshly started app.

## Scenario

| Expense | Amount | Paid by | Split |
|---|---|---|---|
| Dinner | 1200 | Aman | EQUAL among all 4 â†’ 300 each |
| Cab to airport | 1000 | Bhavya | PERCENTAGE 25 / 25 / 30 / 20 â†’ 250 / 250 / 300 / 200 |
| Movie tickets | 1000 | Chirag | EXACT 250 / 300 / 200 / 250 |

| Person | Paid | Share owed | Net balance |
|---|---|---|---|
| Aman | 1200 | 300 + 250 + 250 = 800 | **+400** |
| Bhavya | 1000 | 300 + 250 + 300 = 850 | **+150** |
| Chirag | 1000 | 300 + 300 + 200 = 800 | **+200** |
| Diya | 0 | 300 + 200 + 250 = 750 | **âˆ’750** |

---

## 1. Create 4 users

`POST /api/users`

```json
{ "name": "Aman", "email": "aman@demo.com" }
```

Expected **201 Created**:

```json
{ "id": 1, "name": "Aman", "email": "aman@demo.com" }
```

Repeat for `Bhavya` (id 2), `Chirag` (id 3) and `Diya` (id 4).

## 2. Create a group

`POST /api/groups`

```json
{ "name": "Goa Trip" }
```

Expected **201 Created**:

```json
{ "id": 1, "name": "Goa Trip", "members": [] }
```

## 3. Add members

`POST /api/groups/1/members/1`, then `/members/2`, `/members/3` and `/members/4` (no body).

Expected **200 OK** after the last one:

```json
{
  "id": 1,
  "name": "Goa Trip",
  "members": [
    { "id": 1, "name": "Aman", "email": "aman@demo.com" },
    { "id": 2, "name": "Bhavya", "email": "bhavya@demo.com" },
    { "id": 3, "name": "Chirag", "email": "chirag@demo.com" },
    { "id": 4, "name": "Diya", "email": "diya@demo.com" }
  ]
}
```

(The order of members may vary because a `Set` is used.)

## 4. Expense 1: EQUAL

`POST /api/groups/1/expenses`

```json
{
  "description": "Dinner",
  "amount": 1200,
  "paidBy": 1,
  "splitType": "EQUAL",
  "splits": [ { "userId": 1 }, { "userId": 2 }, { "userId": 3 }, { "userId": 4 } ]
}
```

Expected **201 Created**:

```json
{
  "id": 1,
  "description": "Dinner",
  "amount": 1200.0,
  "splitType": "EQUAL",
  "createdAt": "2026-10-22T11:59:31.564652",
  "paidBy": { "id": 1, "name": "Aman", "email": "aman@demo.com" },
  "shares": [
    { "id": 1, "user": { "id": 1, "name": "Aman", "email": "aman@demo.com" }, "shareAmount": 300.0 },
    { "id": 2, "user": { "id": 2, "name": "Bhavya", "email": "bhavya@demo.com" }, "shareAmount": 300.0 },
    { "id": 3, "user": { "id": 3, "name": "Chirag", "email": "chirag@demo.com" }, "shareAmount": 300.0 },
    { "id": 4, "user": { "id": 4, "name": "Diya", "email": "diya@demo.com" }, "shareAmount": 300.0 }
  ],
  "groupId": 1
}
```

The app console also prints: `[NOTIFICATION] Aman added expense Dinner (Rs 1200.00)`

## 5. Expense 2: PERCENTAGE

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

Expected **201 Created**, with shares of 250, 250, 300 and 200.

## 6. Expense 3: EXACT

```json
{
  "description": "Movie tickets",
  "amount": 1000,
  "paidBy": 3,
  "splitType": "EXACT",
  "splits": [
    { "userId": 1, "value": 250 },
    { "userId": 2, "value": 300 },
    { "userId": 3, "value": 200 },
    { "userId": 4, "value": 250 }
  ]
}
```

Expected **201 Created**, with shares of 250, 300, 200 and 250.

## 7. Balances

`GET /api/groups/1/balances` â†’ **200 OK**

```json
[
  { "userId": 1, "name": "Aman",   "balance": 400.0,  "status": "gets back Rs 400.00" },
  { "userId": 3, "name": "Chirag", "balance": 200.0,  "status": "gets back Rs 200.00" },
  { "userId": 2, "name": "Bhavya", "balance": 150.0,  "status": "gets back Rs 150.00" },
  { "userId": 4, "name": "Diya",   "balance": -750.0, "status": "owes Rs 750.00" }
]
```

## 8. Settlements

`GET /api/groups/1/settlements` â†’ **200 OK**

```json
[
  { "fromUserId": 4, "fromUser": "Diya", "toUserId": 1, "toUser": "Aman",   "amount": 400.0, "message": "Diya pays Aman Rs 400.00" },
  { "fromUserId": 4, "fromUser": "Diya", "toUserId": 3, "toUser": "Chirag", "amount": 200.0, "message": "Diya pays Chirag Rs 200.00" },
  { "fromUserId": 4, "fromUser": "Diya", "toUserId": 2, "toUser": "Bhavya", "amount": 150.0, "message": "Diya pays Bhavya Rs 150.00" }
]
```

Three payments for four people, which is the minimum (n âˆ’ 1).

## 9. Filter expenses by user

`GET /api/groups/1/expenses?userId=2` â†’ **200 OK**, listing only the expenses Bhavya paid for or has a share in.

## 10. Error cases

| Request | Expected |
|---|---|
| Percentages adding up to 90 | **400** `"Percentages must add up to 100, but got 90.0"` |
| EXACT amounts not matching the total | **400** `"Exact amounts add up to Rs 100.00 but the expense is Rs 500.00"` |
| `"splitType": "RANDOM"` | **400** `"Unknown split type 'RANDOM'. Use one of [EQUAL, EXACT, PERCENTAGE]"` |
| Same email twice | **400** `"Email already registered: aman@demo.com"` |
| `GET /api/groups/99/balances` | **404** `"Group not found with id 99"` |
| `POST /api/groups/1/members/42` | **404** `"User not found with id 42"` |

Error body format:

```json
{ "timestamp": "2026-10-22T12:00:27.92", "status": 400, "error": "Bad Request", "message": "Percentages must add up to 100, but got 90.0" }
```

After a 400 on an expense, call `GET /api/groups/1/expenses`: no half-saved expense appears, because `@Transactional` rolled it back.

## 11. Delete

`DELETE /api/expenses/3` â†’ **204 No Content** (its shares are deleted too, because of the cascade).
Run it again â†’ **404** `"Expense not found with id 3"`.

Settlements are now recalculated:
`Chirag pays Aman Rs 600.00`, `Diya pays Bhavya Rs 450.00`, `Diya pays Aman Rs 50.00`.

## 12. Delete users and groups

| Request | Expected |
|---|---|
| `DELETE /api/users/1` while Aman is still in expenses | **409 Conflict** `"Aman is still part of expenses. Delete those expenses (or the whole group) first."` |
| `DELETE /api/groups/1` | **204** (its expenses, shares, member links and activity log are deleted too) |
| `GET /api/groups/1` | **404** `"Group not found with id 1"` |
| `DELETE /api/users/1` (now that the group is gone) | **204** |
| `DELETE /api/users/1` again | **404** `"User not found with id 1"` |

## 6. README

### Expense Splitter (Splitwise-style) â€” Spring Boot

**Project Based Learning in Java, Experiment 3.3** Â· B.Tech IT, Semester 5

A REST backend for sharing group expenses, like Splitwise. You create users, put them in groups,
record who paid for what and how it should be split, and the app works out who owes whom and the
**fewest payments** needed to settle up. A small web interface (served by the same app) and a
Postman collection are included for demos.

#### Features

| Feature | How it is built |
|---|---|
| Users and groups (many-to-many) | JPA `@ManyToMany` with a `group_members` join table |
| Expenses with 3 split types | **Strategy pattern**: `EQUAL`, `PERCENTAGE`, `EXACT` beans injected as `Map<String, SplitStrategy>` and chosen at runtime from the request body |
| Net balance per member | Java Streams: `groupingBy` + `summingDouble` |
| Minimum settlement | Greedy algorithm with two `PriorityQueue` max-heaps (creditors and debtors) |
| Activity log and notifications | **Observer pattern**: `AuditLogger` saves to the DB, `NotificationService` prints to the console |
| App settings (currency, rounding) | **Singleton pattern**: `ConfigService.getInstance()` |
| All-or-nothing save | `@Transactional` on `addExpense`: an invalid split rolls back the expense too |
| Clean errors | `@ControllerAdvice` + `@ExceptionHandler` â†’ `{timestamp, status, error, message}` |
| Money rounding | `BigDecimal` with `HALF_UP` to 2 decimals; paisa left over from rounding goes to one participant so shares always add up exactly |

#### Tech stack

Java 17 Â· Spring Boot 3.3.5 Â· Spring Web Â· Spring Data JPA / Hibernate Â· H2 (in-memory; MySQL config included in comments) Â· Maven Â· Postman. No Lombok.

#### How to run

You only need **Java 17**. Maven is not required because the project includes the Maven Wrapper.

```bash
### Windows
mvnw.cmd spring-boot:run

### macOS / Linux
./mvnw spring-boot:run
```

Then open:

| URL | What |
|---|---|
| http://localhost:8080/ | Demo web interface (click **Load demo data**) |
| http://localhost:8080/h2-console | H2 database console. JDBC URL `jdbc:h2:mem:expensedb`, user `sa`, no password |

Run the automated test: `mvnw.cmd test`

You can also open the folder in IntelliJ IDEA or Eclipse as a Maven project and run `ExpenseSplitterApplication`.

##### Using Postman

1. In Postman click **Import** and choose `postman/Expense-Splitter.postman_collection.json`.
2. Start the app.
3. Run the folders in order (1 â†’ 7), or click the collection â†’ **Run** to run everything.
   Folder 7 deletes everything the run created, so you can run it again and again.
   IDs from each response are saved automatically, and every request has tests that check the status code and values.

##### Switching to MySQL

1. Uncomment `mysql-connector-j` in `pom.xml`.
2. Run `CREATE DATABASE expensedb;` in MySQL.
3. In `application.properties`, comment out the H2 block and uncomment the MySQL block (set your password).

#### API

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

#### Project structure

```
expense-splitter/
â”œâ”€â”€ pom.xml, mvnw, mvnw.cmd, .mvn/
â”œâ”€â”€ postman/Expense-Splitter.postman_collection.json
â”œâ”€â”€ docs/  (Postman test guide, viva cheat-sheet, lab record)
â””â”€â”€ src/
    â”œâ”€â”€ main/java/com/expensesplitter/
    â”‚   â”œâ”€â”€ ExpenseSplitterApplication.java
    â”‚   â”œâ”€â”€ config/      ConfigService (Singleton)
    â”‚   â”œâ”€â”€ controller/  UserController, GroupController, ExpenseController
    â”‚   â”œâ”€â”€ dto/         ExpenseRequest, SplitRequest, BalanceResponse, SettlementResponse, ErrorResponse
    â”‚   â”œâ”€â”€ entity/      User, Group, Expense, ExpenseShare, ActivityLog
    â”‚   â”œâ”€â”€ exception/   custom exceptions + GlobalExceptionHandler
    â”‚   â”œâ”€â”€ observer/    ExpenseListener, ExpenseEventPublisher, AuditLogger, NotificationService
    â”‚   â”œâ”€â”€ repository/  JpaRepository interfaces
    â”‚   â”œâ”€â”€ service/     UserService, GroupService, ExpenseService, BalanceService
    â”‚   â””â”€â”€ strategy/    SplitStrategy + Equal / Percentage / Exact
    â”œâ”€â”€ main/resources/  application.properties, static/index.html
    â””â”€â”€ test/java/...    ExpenseSplitterApplicationTests
```

## 7. Viva cheat-sheet

### Viva cheat-sheet: Expense Splitter

**1. What does your project do?**
It is a Splitwise-style REST backend. Users join groups and record expenses that are split EQUAL, by PERCENTAGE or by EXACT amounts. It shows each member's net balance and the minimum number of payments needed to settle the group.

**2. Strategy vs Observer: what is the difference?**
Strategy chooses **one algorithm out of many** at runtime (which split method to use). Observer lets **many objects react to one event** (an expense was added â†’ log it and notify people). Strategy is about *how* something is done; Observer is about *who gets told* when something happens.

**3. How is the Strategy pattern implemented?**
`SplitStrategy` is an interface with three `@Component` implementations named `"EQUAL"`, `"PERCENTAGE"` and `"EXACT"`. Spring injects them all into `Map<String, SplitStrategy>` (key = bean name). `ExpenseService` calls `strategies.get(request.getSplitType())`. Adding a new split type means adding one new class, with no if-else chain to edit (Open/Closed principle).

**4. How is the Observer pattern implemented?**
`ExpenseListener` is the observer interface. `ExpenseEventPublisher` is the subject: Spring injects every `ExpenseListener` bean into its list. After an expense is saved it calls `notifyExpenseAdded()`, which runs `AuditLogger` (saves an `ActivityLog` row) and `NotificationService` (prints to the console).

**5. How is the Singleton pattern implemented?**
`ConfigService` has a private constructor, a `private static volatile` instance and `getInstance()` with double-checked locking. It holds the currency symbol and rounding rules. Note that Spring beans are also singletons by default (one per container), but `ConfigService` shows the classic GoF version.

**6. Why `@Transactional` on `addExpense`?**
Saving an expense touches three tables: the expense, its shares and the activity log. They must all be saved or none of them. In the code the expense is saved **before** the split is calculated, so if the split is invalid, `InvalidSplitException` (a RuntimeException) makes Spring **roll back** and the expense row disappears. The JUnit test checks this.

**7. Explain the settlement algorithm.**
First calculate each member's net balance. Put creditors (+) in one max-heap and debtors (âˆ’) in another, using `PriorityQueue` with a comparator. Repeatedly take the biggest creditor and the biggest debtor; the debtor pays `min(credit, debt)`. Whoever still has money left goes back into their heap. Each step settles at least one person, so at most **n âˆ’ 1** payments are needed. Time complexity: O(n log n).

**8. How are balances calculated with Streams?**
`paid = expenses.stream().collect(groupingBy(e -> e.getPaidBy().getId(), summingDouble(Expense::getAmount)))`.
`owed` is the same, but uses `flatMap` over all the shares. Net balance = paid âˆ’ owed. Positive means the person gets money back; negative means they owe.

**9. What are IoC and DI?**
Inversion of Control: the Spring container, not our code, creates objects and manages their life cycle. Dependency Injection is how it supplies the objects a class needs. `ExpenseService` uses **constructor injection** (its dependencies are `final`, which makes it easy to test). Other classes use `@Autowired` **field injection**. Constructor injection is the recommended style.

**10. `@Component` vs `@Service` vs `@Repository` vs `@RestController`?**
All four register a Spring bean. `@Service` marks business logic, `@Repository` marks data access (and translates database exceptions), and `@RestController` = `@Controller` + `@ResponseBody`, so return values are written as JSON.

**11. What does `JpaRepository` give you?**
`save`, `findById`, `findAll`, `delete`, `count`, paging and so on, with no implementation code. It also generates queries from method names, e.g. `findByGroup_IdOrderByCreatedAtAsc` and `existsByEmail`.

**12. Explain your entity relationships.**
User â†” Group is `@ManyToMany`, with the `group_members` join table owned by `Group`. Expense â†’ User (`paidBy`) and Expense â†’ Group are `@ManyToOne`. Expense â†’ ExpenseShare is `@OneToMany(mappedBy="expense", cascade=ALL, orphanRemoval=true)`, so deleting an expense deletes its shares. `@JsonIgnore` on back-references prevents infinite JSON loops.

**13. H2 vs MySQL?**
H2 is an in-memory Java database: no installation, fast, and wiped on restart, which is ideal for labs and tests. MySQL is a real server database where data persists, used in production. Switching only needs a different driver and datasource URL, because JPA/Hibernate hides the SQL dialect.

**14. How do you handle errors?**
Custom unchecked exceptions (`UserNotFoundException`, `GroupNotFoundException`, `ExpenseNotFoundException`, `InvalidSplitException`). A `@ControllerAdvice` class has `@ExceptionHandler` methods that map them to 404 or 400 and return `{timestamp, status, error, message}` instead of a stack trace.

**15. Which HTTP status codes do you use and why?**
201 Created for a new user, group or expense. 200 OK for reads and for adding a member. 204 No Content for delete. 400 Bad Request for invalid input or split. 404 Not Found for a missing id. 405 Method Not Allowed for the wrong HTTP method. 409 Conflict when deleting a user who is still part of expenses.

**Bonus: why can't you delete a user who has expenses?**
Their expense shares are needed to calculate everyone else's balance. Deleting them silently would make the group's totals wrong, so the API returns 409 and asks you to delete the expenses or the group first. Deleting a group is allowed: it removes its expenses and logs inside one `@Transactional` method.

**16. Why BigDecimal for rounding, and what about leftover paise?**
`double` cannot store values like 0.1 exactly, so `ConfigService.round()` uses `BigDecimal.setScale(2, HALF_UP)`. For 1000 split 3 ways, each share is 333.33, and the leftover 0.01 goes to the first participant so the shares add up to exactly 1000.00.

**17. Which collections do you use, and where?**
`HashMap` (the paid/owed maps), `ArrayList` (results and the listener list), `PriorityQueue` (settlement heaps), `HashSet` (group members and duplicate-participant checks) and `LinkedHashMap` (shares in the order participants were sent).

**18. Where do you use lambdas?**
Comparators for the heaps `(a, b) -> Double.compare(b.amount, a.amount)`, `orElseThrow(() -> new UserNotFoundException(id))`, stream filters and maps, and `listeners.forEach(l -> l.onExpenseAdded(e))`.

