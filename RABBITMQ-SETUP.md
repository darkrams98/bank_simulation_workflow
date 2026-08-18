# RabbitMQ Manual GUI Configuration (http://localhost:15672)

## 1. Exchange

Tab: **Exchanges → Add a new exchange**

| Field | Value |
|---|---|
| Name | `payment.exchange` |
| Type | `direct` |
| Durability | `Durable` |
| Auto delete | `No` |
| Internal | `No` |
| Virtual host | `/` |

## 2. Queues

Tab: **Queues and Streams → Add a new queue** (repeat three times)

| Name | Type | Durability | Auto delete | Virtual host |
|---|---|---|---|---|
| `bank1.queue` | Classic | Durable | No | `/` |
| `bank2.queue` | Classic | Durable | No | `/` |
| `shaparak.reply.queue` | Classic | Durable | No | `/` |

## 3. Bindings

Tab: **Exchanges → payment.exchange → Add binding from this exchange**

| To queue | Routing key |
|---|---|
| `bank1.queue` | `bank.1` |
| `bank2.queue` | `bank.2` |
| `shaparak.reply.queue` | `shaparak.reply` |

## 4. Summary of the synchronous request-reply path

```
SHAPARAK  --publish--> payment.exchange (routing key: bank.1 | bank.2)
                          |
                          +--> bank1.queue  --> BANK1 (@RabbitListener)
                          +--> bank2.queue  --> BANK2 (@RabbitListener)

BANK  --reply (replyTo = payment.exchange/shaparak.reply, same correlationId)-->
          payment.exchange (routing key: shaparak.reply)
                          |
                          +--> shaparak.reply.queue --> RabbitTemplate reply container
                                                        (unblocks convertSendAndReceive)
```

## 5. BIN routing table

| BIN (first 6 digits) | Routing key | Queue | Bank |
|---|---|---|---|
| `603799` | `bank.1` | `bank1.queue` | BANK1 (port 8084) |
| `589210` | `bank.1` | `bank1.queue` | BANK1 (port 8084) |
| `627353` | `bank.2` | `bank2.queue` | BANK2 (port 8085) |
| `621986` | `bank.2` | `bank2.queue` | BANK2 (port 8085) |

## 6. User / vhost

| Field | Value |
|---|---|
| Virtual host | `/` |
| Username | `guest` |
| Password | `guest` |
| Permissions | configure `.*`, write `.*`, read `.*` |
