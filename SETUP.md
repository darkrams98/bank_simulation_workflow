# Manual Database Setup (GUI only)

## MongoDB (Compass)

| Database | Collections |
|---|---|
| `psp_db` | `psp_transactions`, `psp_message_logs` |
| `shaparak_db` | `shaparak_transactions`, `shaparak_message_logs` |

## SQLite (DB Browser for SQLite)

Create an empty `data` folder next to each JAR, then create these empty database files:

| Service | File |
|---|---|
| view-service | `data/view.db` |
| bank-service (profile `bank1`) | `data/bank1.db` |
| bank-service (profile `bank2`) | `data/bank2.db` |

Tables are mapped by JPA (`ddl-auto: update`) on first start. After the first start, insert the demo rows below through the GUI.

### view.db → products

```sql
INSERT INTO products (name, description, price, stock, active) VALUES ('Laptop', 'Gaming laptop', 25000000, 10, 1);
INSERT INTO products (name, description, price, stock, active) VALUES ('Headphone', 'Wireless headphone', 3500000, 25, 1);
INSERT INTO products (name, description, price, stock, active) VALUES ('Keyboard', 'Mechanical keyboard', 1800000, 40, 1);
```

### bank1.db → accounts (BIN 603799, 589210)

```sql
INSERT INTO accounts (card_number, card_holder_name, account_number, bin, balance, active) VALUES ('6037991234567890', 'ALIREZA MOGHADASI', 'IR1001', '603799', 50000000, 1);
INSERT INTO accounts (card_number, card_holder_name, account_number, bin, balance, active) VALUES ('5892100000001111', 'SARA AHMADI', 'IR1002', '589210', 1000000, 1);
```

### bank2.db → accounts (BIN 627353, 621986)

```sql
INSERT INTO accounts (card_number, card_holder_name, account_number, bin, balance, active) VALUES ('6273531111222233', 'REZA KARIMI', 'IR2001', '627353', 80000000, 1);
INSERT INTO accounts (card_number, card_holder_name, account_number, bin, balance, active) VALUES ('6219861111222233', 'MARYAM NOURI', 'IR2002', '621986', 500000, 0);
```

# Build

```bash
cd view-service      && mvn clean package -DskipTests
cd ../psp-service    && mvn clean package -DskipTests
cd ../shaparak-service && mvn clean package -DskipTests
cd ../bank-service   && mvn clean package -DskipTests
```

# Run order

```bash
java -jar bank-service/target/bank-service-1.0.0.jar --spring.profiles.active=bank1
java -jar bank-service/target/bank-service-1.0.0.jar --spring.profiles.active=bank2
java -jar shaparak-service/target/shaparak-service-1.0.0.jar
java -jar psp-service/target/psp-service-1.0.0.jar
java -jar view-service/target/view-service-1.0.0.jar
```

| Service | Port |
|---|---|
| view-service | 8081 |
| psp-service | 8082 |
| shaparak-service | 8083 |
| bank-service (bank1) | 8084 |
| bank-service (bank2) | 8085 |

# End-to-end simulation flow

```bash
curl -X POST http://localhost:8081/api/auth/register -H 'Content-Type: application/json' \
  -d '{"username":"alireza","email":"alireza@test.com","password":"secret123"}'

TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"alireza","password":"secret123"}' | python3 -c 'import sys,json;print(json.load(sys.stdin)["accessToken"])')

curl http://localhost:8081/api/products -H "Authorization: Bearer $TOKEN"

curl -X POST http://localhost:8081/api/cards -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"cardNumber":"6037991234567890","cardHolderName":"ALIREZA MOGHADASI"}'

curl -X POST http://localhost:8081/api/payments/purchase -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"productId":1,"cardId":1}'
```

| Scenario | Card | Expected status |
|---|---|---|
| Successful purchase routed to BANK1 | `6037991234567890` | `SUCCESS` |
| Insufficient funds on BANK1 | `5892100000001111` (buy Laptop) | `INSUFFICIENT_FUNDS` |
| Successful purchase routed to BANK2 | `6273531111222233` | `SUCCESS` |
| Blocked card on BANK2 | `6219861111222233` | `CARD_INACTIVE` |
| Unknown BIN | `9999991111222233` | `BANK_NOT_FOUND` |
| Bank JAR stopped | any card of that bank | `TIMEOUT` |
