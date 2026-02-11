# MES Spring Boot 2 教學專案

以製造業 **MES（Manufacturing Execution System，製造執行系統）** 為領域範例，透過 Maven 多模組架構，漸進式展示 **DDD 戰術設計**、**六角形架構**、**CQRS** 與 **SOLID 原則**。

## 技術棧

| 技術 | 版本 |
|---|---|
| JDK | 1.8 |
| Spring Boot | 2.7.18 |
| Spring Cloud | 2021.0.9 |
| MyBatis Starter | 2.3.2 |
| H2 Database | Spring Boot 管理 |
| Lombok | Spring Boot 管理 |

## 專案結構

```
spring2-learning/                 (Parent POM)
├── mes-common/                   共用 DDD / CQRS 基礎類別（Shared Kernel）
├── mes-boot-basics/              Module 1 — Spring Boot 2 基礎 + DDD 建構塊
├── mes-web-api/                  Module 2 — REST API + 完整 CQRS
├── mes-mybatis/                  Module 3 — MyBatis 持久層 + Repository 適配器
└── mes-kafka/                    Module 4 — Spring Cloud Kafka + Domain Events
```

## 快速開始

### 環境需求

- JDK 8+
- Maven 3.6+
- （Module 4 本地執行需要 Kafka，測試時使用 Embedded Kafka 無需額外安裝）

### 編譯與測試

```bash
# 全專案編譯 + 測試（269 tests）
mvn clean test

# 只編譯不跑測試
mvn clean compile
```

### 啟動各模組

```bash
# Module 1 — 工單管理（預設 port 8080）
mvn spring-boot:run -pl mes-boot-basics

# Module 2 — 生產追蹤（port 8081）
mvn spring-boot:run -pl mes-web-api

# Module 3 — 設備管理（port 8082）
mvn spring-boot:run -pl mes-mybatis

# Module 4 — 品質檢驗（port 8083，需本地 Kafka）
mvn spring-boot:run -pl mes-kafka
```

---

## 模組詳解

### mes-common — DDD 共用基礎（Shared Kernel）

所有模組共用的 DDD 與 CQRS 基礎類別，**不依賴任何 Spring 框架**，是純 Java 的領域基礎設施。

```
com.mes.common/
├── ddd/
│   ├── annotation/       @AggregateRoot, @ValueObject, @DomainService 標記
│   ├── model/            BaseEntity, BaseAggregateRoot, BaseValueObject, Identity
│   ├── event/            DomainEvent, BaseDomainEvent, DomainEventPublisher
│   ├── repository/       Repository<T, ID> 泛型介面
│   └── specification/    Specification<T> + and/or/not 組合器
├── cqrs/                 Command, Query, CommandHandler, QueryHandler, CommandBus, QueryBus
└── exception/            DomainException, EntityNotFoundException, BusinessRuleViolationException
```

#### 核心概念

| 類別 | 用途 | 學習重點 |
|---|---|---|
| `BaseAggregateRoot<ID>` | 聚合根基類，內含 `domainEvents` 列表 | 領域事件的註冊與清除機制 |
| `BaseValueObject` | Value Object 基類，透過 `getEqualityComponents()` 實現值相等 | VO 的 equals/hashCode 由屬性值決定 |
| `Identity<T>` | 強型別 ID，避免原始型別混用 | 防止 `workOrderId` 和 `equipmentId` 互相傳錯 |
| `Specification<T>` | 規格模式，支援 `and()` / `or()` / `not()` 組合 | 將複雜業務規則抽為可組合的物件 |
| `CommandBus` / `QueryBus` | CQRS 的訊息匯流排介面 | 讀寫分離的入口點 |

---

### Module 1: mes-boot-basics — 工單管理

> **領域**: WorkOrder（工單）— 製造業最基礎的作業單位

#### 學習重點

- Spring Boot 2 自動配置（auto-configuration）
- `@ConfigurationProperties` 型別安全的設定綁定
- Profile 機制（`application.yml` + `application-dev.yml`）
- 建構子注入（Constructor Injection）
- `ApplicationRunner` 啟動初始化

#### DDD 模式展示

| 模式 | 實作 | 說明 |
|---|---|---|
| Aggregate Root | `WorkOrder` | 工單聚合根，包含完整狀態機（CREATED → IN_PROGRESS → COMPLETED / CANCELLED） |
| Value Object | `ProductInfo`, `Quantity`, `DateRange`, `Priority` | 自我驗證，不可變，值相等 |
| Domain Event | `WorkOrderCreatedEvent`, `WorkOrderStartedEvent` ... | 狀態變更時自動註冊事件 |
| Factory | `WorkOrderFactory` | 封裝建立邏輯，確保新建物件的一致性 |
| Repository | `WorkOrderRepository` (介面) | Domain 層定義 Port |
| Specification | `OverdueWorkOrderSpec`, `HighPriorityWorkOrderSpec` | 可組合的業務規則 |
| Domain Service | `WorkOrderDomainService` | 跨聚合的排程衝突檢查 |

#### 六角形架構

```
com.mes.boot.workorder/
├── domain/                        [核心 — 無框架依賴]
│   ├── model/                     WorkOrder, Value Objects
│   ├── event/                     Domain Events
│   ├── repository/                WorkOrderRepository（Port）
│   ├── service/                   WorkOrderDomainService
│   ├── factory/                   WorkOrderFactory
│   └── specification/             業務規格
├── application/                   [應用層 — Use Cases]
│   ├── service/                   WorkOrderApplicationService
│   ├── dto/                       CreateWorkOrderRequest, WorkOrderResponse
│   └── assembler/                 WorkOrderAssembler（Domain ↔ DTO）
└── infrastructure/                [適配器 — 技術實作]
    ├── persistence/               InMemoryWorkOrderRepository（Adapter）
    ├── event/                     LoggingDomainEventPublisher
    └── config/                    WorkOrderProperties, WorkOrderConfig
```

#### Quantity Value Object 範例

```java
// Value Object 自我驗證 — 建立時就確保資料正確
public class Quantity extends BaseValueObject {
    private final int planned;
    private final int defective;

    public Quantity(int planned, int defective) {
        if (planned < 0) throw new IllegalArgumentException("Planned quantity cannot be negative");
        if (defective < 0) throw new IllegalArgumentException("Defective quantity cannot be negative");
        if (defective > planned) throw new BusinessRuleViolationException("Defective cannot exceed planned");
        this.planned = planned;
        this.defective = defective;
    }

    public double getYieldRate() {
        if (planned == 0) return 0.0;
        return (planned - defective) / (double) planned;
    }
}
```

---

### Module 2: mes-web-api — 生產追蹤

> **領域**: ProductionRecord（生產紀錄）— 追蹤每條產線的即時生產狀況

#### 學習重點

- REST API 設計（`@RestController`、HTTP Method 語義、狀態碼）
- Bean Validation（`@Valid`、`@NotBlank`、`@Min`）
- 全域例外處理（`@RestControllerAdvice`）
- `ApiResponse<T>` 統一回應封裝
- **完整 CQRS 模式** — Command / Query 分離

#### CQRS 完整實作

本模組是 CQRS 的核心學習重點，展示讀寫完全分離的架構：

```
                    ┌─────────────────────────────┐
                    │       REST Controllers       │
                    ├──────────────┬───────────────┤
                    │ CommandCtrl  │  QueryCtrl    │
                    │ (POST/PUT)   │  (GET)        │
                    └──────┬───────┴───────┬───────┘
                           │               │
                    ┌──────▼──────┐ ┌──────▼──────┐
                    │ CommandBus  │ │  QueryBus   │
                    └──────┬──────┘ └──────┬──────┘
                           │               │
              ┌────────────▼─┐       ┌─────▼────────────┐
              │ CommandHandler│       │  QueryHandler     │
              │ (寫入 Domain) │       │ (讀取 → DTO View) │
              └──────────────┘       └──────────────────┘
```

| 元件 | 寫入端（Command） | 讀取端（Query） |
|---|---|---|
| Controller | `ProductionCommandController` (POST/PUT) | `ProductionQueryController` (GET) |
| 匯流排 | `SimpleCommandBus` | `SimpleQueryBus` |
| 訊息物件 | `StartProductionCommand`, `RecordOutputCommand` ... | `GetProductionRecordQuery`, `ListProductionByLineQuery` ... |
| Handler | `StartProductionCommandHandler` ... | `GetProductionRecordQueryHandler` ... |
| 回傳型別 | void / ID | `ProductionRecordView`, `ProductionSummaryView` |

#### REST API 端點

```
POST   /api/v1/productions              開始生產
PUT    /api/v1/productions/{id}/output   記錄產出
PUT    /api/v1/productions/{id}/pause    暫停生產
PUT    /api/v1/productions/{id}/complete 完成生產
GET    /api/v1/productions/{id}          查詢單筆
GET    /api/v1/productions?lineId=&status=  依產線查詢
GET    /api/v1/productions/summary?lineId=  產線摘要
```

#### 統一回應格式

```json
{
  "code": 200,
  "message": "Success",
  "data": { ... },
  "timestamp": "2024-01-15T10:30:00"
}
```

---

### Module 3: mes-mybatis — 設備管理

> **領域**: Equipment（設備）含 MaintenanceRecord（保養紀錄）— 展示聚合根內包含子 Entity

#### 學習重點

- MyBatis XML Mapper + Annotation 混合使用
- Data Object（DO）與 Domain Model 的轉換
- **Repository Adapter 模式** — Domain Port → MyBatis 實作的橋接
- H2 記憶體資料庫 + `schema.sql` / `data.sql` 自動初始化
- **CQRS 讀取路徑優化** — Query Handler 繞過 Domain Model，直接用 Mapper 查詢

#### 持久層架構

這是本模組最重要的教學重點 — 如何在六角形架構中整合 MyBatis：

```
  Domain Layer                     Infrastructure Layer
 ┌────────────────┐               ┌─────────────────────────────┐
 │ Equipment      │               │ MyBatisEquipmentRepository  │
 │ (Aggregate)    │◄─────────────►│  ├── EquipmentMapper (XML)  │
 │                │   Repository  │  ├── MaintenanceRecordMapper │
 │ Maintenance    │   interface   │  ├── EquipmentConverter      │
 │ Record (Entity)│   (Port)      │  ├── EquipmentDO             │
 └────────────────┘               │  └── MaintenanceRecordDO     │
                                  └─────────────────────────────┘
```

#### DO ↔ Domain Model 轉換

```java
// EquipmentDO（扁平 POJO，對應資料庫欄位）
public class EquipmentDO {
    private String id;
    private String name;
    private String equipmentType;
    private String status;
    private String locationBuilding;  // Location VO 被攤平
    private String locationFloor;
    private String locationZone;
    private String locationPosition;
}

// EquipmentConverter 負責轉換
public Equipment toDomain(EquipmentDO eDO, List<MaintenanceRecordDO> records) {
    // 從扁平欄位重建 Value Object
    Location location = new Location(
        eDO.getLocationBuilding(),
        eDO.getLocationFloor(),
        eDO.getLocationZone(),
        eDO.getLocationPosition()
    );
    // ... 組裝完整 Aggregate Root
}
```

#### CQRS 讀寫分離 — Query 繞過 Domain Model

```java
// 寫入路徑：Command → Domain Model → Repository → Mapper
// 讀取路徑：Query → Mapper（直接查 DB）→ DTO
@Component
public class ListEquipmentByStatusQueryHandler
        implements QueryHandler<ListEquipmentByStatusQuery, List<EquipmentSummaryView>> {

    private final EquipmentMapper equipmentMapper; // 直接注入 Mapper，不經過 Repository

    @Override
    public List<EquipmentSummaryView> handle(ListEquipmentByStatusQuery query) {
        List<EquipmentDO> results = equipmentMapper.selectByStatus(query.getStatus());
        // 直接從 DO 轉 View DTO，省去 Domain Model 重建成本
        return results.stream().map(this::toView).collect(Collectors.toList());
    }
}
```

#### H2 Console

啟動後可透過 `http://localhost:8082/h2-console` 查看資料庫。

- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: （空白）

---

### Module 4: mes-kafka — 品質檢驗

> **領域**: InspectionOrder（品檢單）含 InspectionResult（檢驗結果）— 展示事件驅動架構與跨 Bounded Context 整合

#### 學習重點

- Spring Cloud Stream + Kafka Binder
- `StreamBridge` 發送訊息
- `Consumer<Message<T>>` 接收訊息
- Domain Event 序列化 / 反序列化（Jackson）
- **跨 Bounded Context 的事件驅動整合**

#### 事件驅動架構

```
  ┌──────────────┐         Kafka Topic              ┌──────────────┐
  │ 生產模組      │    mes.production.events         │ 品檢模組      │
  │ (mes-web-api)│ ─── ProductionCompleted ──────► │ (mes-kafka)  │
  │              │                                  │              │
  │              │    mes.quality.events            │              │
  │              │ ◄── QualityAlert ──────────────  │              │
  └──────────────┘                                  └──────────────┘
```

#### Kafka 訊息通道配置

| 通道 | Topic | 方向 | 用途 |
|---|---|---|---|
| `qualityEventsOut-out-0` | `mes.quality.events` | 發送 | 品檢完成、品質警報 |
| `qualityEventsIn-in-0` | `mes.quality.events` | 接收 | 消費品質事件 |
| `productionEventsIn-in-0` | `mes.production.events` | 接收 | 生產完成時自動建立品檢單 |

#### 跨 Context 事件消費

```java
// 當生產模組發出 ProductionCompletedEvent 時，品檢模組自動建立最終檢驗單
@Component
public class ProductionEventConsumer {

    @Bean
    public Consumer<Message<String>> productionEventsIn() {
        return message -> {
            // 反序列化跨 Context 事件
            ProductionCompletedPayload payload = objectMapper.readValue(
                message.getPayload(), ProductionCompletedPayload.class);

            // 自動建立 FINAL 類型品檢單
            CreateInspectionCommand command = new CreateInspectionCommand(
                payload.getWorkOrderId(), payload.getProductCode(), "FINAL");
            commandBus.dispatch(command);
        };
    }
}
```

#### 品質 Domain Service — SPC 簡化規則

```java
// 統計製程控制（SPC）— 連續 7 點同側規則
public boolean isWithinSPC(List<MeasuredValue> values, QualityStandard standard) {
    double mean = standard.getMean();
    int sameSideCount = 0;
    Boolean lastSide = null;

    for (MeasuredValue v : values) {
        boolean aboveMean = v.getValue() > mean;
        if (lastSide != null && lastSide == aboveMean) {
            sameSideCount++;
            if (sameSideCount >= 7) return false; // 製程失控
        } else {
            sameSideCount = 1;
        }
        lastSide = aboveMean;
    }
    return true;
}
```

---

## 六角形架構（統一結構）

每個模組遵循相同的分層結構，**Domain 層完全不依賴任何框架**：

```
com.mes.{module}.{context}/
├── domain/                        [核心 — 純 Java，無框架依賴]
│   ├── model/                     Aggregate Root, Entity, Value Object
│   ├── event/                     Domain Events
│   ├── repository/                Port Interface（由 Infrastructure 實作）
│   ├── service/                   Domain Service
│   ├── factory/                   Factory
│   └── specification/             Specification（Module 1）
├── application/                   [應用層 — 編排 Use Cases]
│   ├── command/ + handler/        CQRS Command 端
│   ├── query/ + handler/ + dto/   CQRS Query 端
│   ├── service/                   Application Service（Module 1）
│   └── assembler/                 Domain ↔ DTO 轉換
├── infrastructure/                [基礎設施 — 技術實作]
│   ├── persistence/               Repository Adapter（InMemory / MyBatis）
│   ├── messaging/                 Event Adapter（Module 4 Kafka）
│   ├── bus/                       CommandBus / QueryBus 實作
│   ├── config/                    Spring Config
│   └── event/                     Event Publisher Adapter
└── adapter/in/web/                [入站適配器 — REST Controller]
    ├── CommandController           寫入端（POST / PUT / DELETE）
    ├── QueryController             讀取端（GET）
    ├── GlobalExceptionHandler      全域例外處理
    └── ApiResponse                 統一回應封裝
```

### 依賴方向

```
  adapter/in/web  ──►  application  ──►  domain  ◄──  infrastructure
  （入站適配器）         （應用層）         （核心）       （出站適配器）
```

**關鍵原則**：箭頭指向 domain — domain 不知道也不依賴外層的任何東西。

---

## SOLID 原則對照

| 原則 | 展現方式 | 對應程式碼 |
|---|---|---|
| **S** — 單一職責 | 每個 Handler 只處理一種 Command/Query | `StartProductionCommandHandler` 只負責開始生產 |
| **O** — 開放封閉 | 新增功能只需新增 Handler，不修改既有程式碼 | 新增 `PauseProductionCommand` + Handler 即可 |
| **L** — 里氏替換 | InMemory / MyBatis Repository 可互換 | `EquipmentRepository` 介面，兩種實作皆可替換 |
| **I** — 介面隔離 | `CommandBus` 與 `QueryBus` 分離 | 讀寫端各自獨立的介面 |
| **D** — 依賴反轉 | Domain 定義 Port，Infrastructure 實作 Adapter | `WorkOrderRepository`(介面) ← `InMemoryWorkOrderRepository`(實作) |

---

## 測試策略

每個模組包含不同層級的測試，**所有測試使用 AssertJ + `@DisplayName` 中文描述**：

| 測試類型 | 適用模組 | 說明 |
|---|---|---|
| **Unit Test** | 全部 | Domain Model 行為、VO 驗證、Specification 組合、Handler 邏輯 |
| `@SpringBootTest` | Module 1, 3 | Application Service 整合、Repository Adapter、Context 載入 |
| `@WebMvcTest` | Module 2 | MockMvc 測試 REST Controller + 驗證 + 例外處理 |
| `@SpringBootTest + H2` | Module 3 | MyBatis Mapper CRUD、Repository 完整流程 |
| Embedded Kafka | Module 4 | 端到端事件流、序列化 / 反序列化 |

### 測試數量

| 模組 | 測試數 |
|---|---|
| mes-common | 16 |
| mes-boot-basics | 79 |
| mes-web-api | 62 |
| mes-mybatis | 54 |
| mes-kafka | 58 |
| **總計** | **269** |

---

## 學習路線建議

```
Step 1                Step 2                Step 3                Step 4
┌──────────┐         ┌──────────┐         ┌──────────┐         ┌──────────┐
│mes-common│  ──►    │mes-boot  │  ──►    │mes-web   │  ──►    │mes-kafka │
│          │         │-basics   │         │-api      │         │          │
│ DDD 基礎  │         │ Spring   │         │ REST +   │         │ 事件驅動  │
│ 類別      │         │ Boot 基礎 │         │ CQRS     │         │ Kafka    │
└──────────┘         └──────────┘         └──────────┘         └──────────┘
                                                │
                                          ┌─────▼─────┐
                                          │mes-mybatis │
                                          │ 持久層整合   │
                                          └───────────┘
```

1. **先讀 `mes-common`** — 理解 DDD 基礎類別（Entity、VO、Aggregate Root、Domain Event）
2. **再讀 `mes-boot-basics`** — 看一個完整的 Aggregate Root 如何運作，熟悉六角形分層
3. **接著讀 `mes-web-api`** — 學習完整 CQRS 模式與 REST API 設計
4. **`mes-mybatis` 與 `mes-kafka` 可並行學習** — 分別深入持久層適配器與事件驅動

### 每個模組建議閱讀順序

1. `domain/model/` — 先看 Aggregate Root 和 Value Object
2. `domain/event/` — 看有哪些 Domain Event
3. `domain/repository/` — 看 Port Interface 定義
4. `application/` — 看 Use Case 如何編排
5. `infrastructure/` — 看技術實作如何接上 Port
6. `adapter/in/web/` — 看入站適配器
7. `src/test/` — **所有測試都是最好的文件**，從測試理解行為

---

## 授權

本專案僅供教學用途。
