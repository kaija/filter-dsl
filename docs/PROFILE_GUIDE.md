# User Profile 使用指南

## 概述

Profile 模型代表使用者的完整屬性，包含固定屬性、自動計算屬性和動態屬性。

## 固定屬性（Fixed Fields）

### 地理位置

| 欄位 | 類型 | 說明 | 範例 |
|------|------|------|------|
| `country` | String | 國家 | "Taiwan", "United States" |
| `city` | String | 城市 | "Taipei", "San Francisco" |
| `region` | String | 地區/州 | "Taipei City", "California" |
| `continent` | String | 洲 | "Asia", "North America" |
| `timezone` | String | 時區 | "Asia/Taipei", "America/Los_Angeles" |

### 使用者識別

| 欄位 | 類型 | 說明 | 範例 |
|------|------|------|------|
| `uuid` | String | 匿名使用者識別碼 | "user-123-abc" |
| `user_id` | String | 認證使用者 ID | "john.doe@example.com" |

### 人口統計

| 欄位 | 類型 | 說明 | 範例 |
|------|------|------|------|
| `gender` | String | 性別 | "male", "female", "other" |
| `birthday` | String | 生日（ISO 格式）| "1990-05-15" |
| `language` | String | 語言偏好 | "zh-TW", "en-US" |

### 首次來源（First Referral）

整合所有首次訪問的來源資訊：

| 欄位 | 類型 | 說明 | 範例 |
|------|------|------|------|
| `timestamp` | String | 首次訪問時間 | "2024-01-15T10:00:00Z" |
| `source` | String | 首次來源 | "google.com", "facebook.com" |
| `medium` | String | 首次媒介 | "organic", "cpc", "referral" |
| `campaign` | String | 首次活動 | "summer_sale", "brand_awareness" |
| `landing_page` | String | 首次落地頁 | "/products", "/promo" |
| `referrer_url` | String | 首次推薦網址 | "https://google.com/search" |

## 自動計算屬性

這些屬性會在調用 `ProfileComputer.computeProperties()` 時自動計算：

| 屬性 | 來源 | 說明 | 範例 |
|------|------|------|------|
| `age` | 從 `birthday` 計算 | 使用者年齡（整數）| 34 |
| `age_range` | 從 `age` 分類 | 年齡區間 | "25-34", "35-44" |
| `first_visit_date` | 從 `first_referral.timestamp` | 首次訪問日期 | "2024-01-15T10:00:00Z" |

## 動態屬性

### Custom Properties（自訂屬性）

任意業務相關的屬性，可靈活新增：

```java
profile.setCustomProperty("membership_level", "premium");
profile.setCustomProperty("loyalty_points", 1500);
profile.setCustomProperty("lifetime_value", 25000.0);
profile.setCustomProperty("vip_status", true);
```

**常見自訂屬性範例**：
- **電商**: membership_level, loyalty_points, lifetime_value, last_purchase_date, favorite_category
- **內容**: subscription_tier, articles_read, video_watch_time, content_preferences
- **遊戲**: player_level, achievements_unlocked, in_game_currency, last_login_streak
- **SaaS**: plan_type, feature_usage, api_calls_count, team_size, trial_end_date

### Computed Properties（計算屬性）

透過 DSL 表達式定義，從 Visit/Event 資料動態計算。

**可用的 DSL 計算範例**：

```java
// 裝置屬性（使用 TOP 函數統計最常出現的值）
profile.defineComputedProperty("os", "TOP(userData.visits, 'os')");
profile.defineComputedProperty("browser", "TOP(userData.visits, 'browser')");
profile.defineComputedProperty("device", "TOP(userData.visits, 'device')");

// 簡單計數和聚合
profile.defineComputedProperty("total_visits", "COUNT(userData.visits)");
profile.defineComputedProperty("total_events", "COUNT(userData.events)");

// 條件計數
profile.defineComputedProperty("purchase_count", "COUNT(WHERE(userData.events, \"EQ(EVENT('eventName'), 'purchase')\"))");

// 最大/最小值
profile.defineComputedProperty("last_visit_date", "MAX(userData.visits, 'timestamp')");
profile.defineComputedProperty("first_event_date", "MIN(userData.events, 'timestamp')");

// 條件判斷
profile.defineComputedProperty("user_type", "IF(GT(COUNT(userData.visits), 1), 'returning', 'new')");
profile.defineComputedProperty("is_active", "GT(COUNT(userData.events), 10)");

// TOP 函數進階用法
profile.defineComputedProperty("top_3_browsers", "TOP(userData.visits, 'browser', 3)");
profile.defineComputedProperty("most_visited_page", "TOP(userData.visits, 'landingPage')");
```

**TOP 函數說明**：

TOP 函數用於統計集合中最常出現的值，支援以下用法：

- `TOP(collection)` - 返回最常出現的值
- `TOP(collection, n)` - 返回前 n 個最常出現的值（列表）
- `TOP(collection, propertyName)` - 返回屬性中最常出現的值
- `TOP(collection, propertyName, n)` - 返回屬性中前 n 個最常出現的值（列表）

範例：
```java
// 最常使用的作業系統
"TOP(userData.visits, 'os')" // 返回 "Windows 10"

// 前 3 個最常使用的瀏覽器
"TOP(userData.visits, 'browser', 3)" // 返回 ["Chrome", "Safari", "Firefox"]

// 最常觸發的事件名稱
"TOP(userData.events, 'eventName')" // 返回 "page_view"
```

**標準計算屬性**：

使用 `ProfileComputer.createWithStandardComputedProperties()` 會自動包含以下計算屬性：

- `os`: 最常使用的作業系統 - `TOP(userData.visits, 'os')`
- `browser`: 最常使用的瀏覽器 - `TOP(userData.visits, 'browser')`
- `device`: 最常使用的裝置類型 - `TOP(userData.visits, 'device')`
- `total_visits`: 總訪問次數 - `COUNT(userData.visits)`
- `total_events`: 總事件數 - `COUNT(userData.events)`
- `last_visit_date`: 最後訪問日期 - `MAX(userData.visits, 'timestamp')`
- `user_type`: 使用者類型 - `IF(GT(COUNT(userData.visits), 1), 'returning', 'new')`

## 使用範例

### 建立 Profile

```java
Profile profile = Profile.builder()
    // 地理位置
    .country("Taiwan")
    .city("Taipei")
    .region("Taipei City")
    .timezone("Asia/Taipei")
    
    // 使用者識別
    .uuid("user-123")
    .userId("john@example.com")
    
    // 人口統計
    .gender("male")
    .birthday("1990-05-15")
    .language("zh-TW")
    
    // 首次來源
    .firstReferral(Profile.FirstReferral.builder()
        .timestamp("2023-06-01T10:00:00Z")
        .source("google.com")
        .medium("organic")
        .campaign("summer_sale")
        .landingPage("/products")
        .referrerUrl("https://google.com/search")
        .build())
    
    // 自訂屬性
    .customProperty("membership_level", "premium")
    .customProperty("loyalty_points", 1500)
    
    // 計算屬性（使用 DSL 表達式）
    .computedProperty("os", "TOP(userData.visits, 'os')")
    .computedProperty("browser", "TOP(userData.visits, 'browser')")
    .computedProperty("device", "TOP(userData.visits, 'device')")
    .computedProperty("total_visits", "COUNT(userData.visits)")
    .build();
```

### 從 Visit 設定 First Referral

```java
Visit firstVisit = Visit.builder()
    .timestamp("2024-01-15T10:00:00Z")
    .isFirstVisit(true)
    .referrerType("cpc")
    .referrerUrl("https://facebook.com/ads")
    .landingPage("/promo")
    .build();

Profile profile = Profile.builder()
    .uuid("user-123")
    .country("Taiwan")
    .language("zh-TW")
    .build();

// 自動從 Visit 設定 first_referral
ProfileComputer.setFirstReferralFromVisit(profile, firstVisit);
```

### 使用標準計算屬性模板

```java
Profile profile = ProfileComputer.createWithStandardComputedProperties();

// 已包含以下計算屬性定義：
// - os, browser, device
// - total_visits, total_events
// - last_visit_date
// - user_type

// 設定固定屬性
profile.setUuid("user-123");
profile.setUserId("john@example.com");
profile.setCountry("Taiwan");
profile.setCity("Taipei");
profile.setGender("male");
profile.setBirthday("1990-05-15");
profile.setLanguage("zh-TW");
```

### 計算動態屬性

```java
ProfileComputer computer = new ProfileComputer(evaluator, contextManager);
Profile computedProfile = computer.computeProperties(profile, userData);

// 存取計算結果
Integer age = (Integer) computedProfile.getCustomProperty("age");
String ageRange = (String) computedProfile.getCustomProperty("age_range");
String os = (String) computedProfile.getCustomProperty("os");
Integer totalVisits = (Integer) computedProfile.getCustomProperty("total_visits");
```

### 存取屬性

```java
// 固定屬性
String country = profile.getCountry();
String gender = profile.getGender();
String birthday = profile.getBirthday();

// First Referral
String firstSource = profile.getFirstReferral().getSource();
String firstMedium = profile.getFirstReferral().getMedium();

// 自動計算屬性
Integer age = (Integer) profile.getCustomProperty("age");
String ageRange = (String) profile.getCustomProperty("age_range");

// DSL 計算屬性
String os = (String) profile.getCustomProperty("os");
Integer totalVisits = (Integer) profile.getCustomProperty("total_visits");

// 自訂屬性
String membershipLevel = (String) profile.getCustomProperty("membership_level");
Integer loyaltyPoints = (Integer) profile.getCustomProperty("loyalty_points");
```

## 在 DSL 中使用

### 固定屬性

```java
profile("country") == "Taiwan"
profile("city") == "Taipei"
profile("gender") == "male"
profile("language") == "zh-TW"
```

### 自動計算屬性

```java
profile("age") >= 25 && profile("age") < 35
profile("age_range") == "25-34"
```

### DSL 計算屬性

```java
profile("os") == "Windows 10"
profile("browser") == "Chrome"
profile("device") == "mobile"
profile("total_visits") > 10
profile("user_type") == "returning"
```

### 自訂屬性

```java
profile("membership_level") == "premium"
profile("loyalty_points") > 1000
profile("lifetime_value") > 10000
```

### First Referral

```java
profile("first_referral").source == "google.com"
profile("first_referral").medium == "organic"
profile("first_referral").campaign == "summer_sale"
```

## 完整範例

```java
// 1. 建立 Profile
Profile profile = Profile.builder()
    .uuid("user-12345")
    .userId("john.doe@example.com")
    .country("Taiwan")
    .city("Taipei")
    .gender("male")
    .birthday("1990-05-15")
    .language("zh-TW")
    .build();

// 2. 設定首次來源（從首次 Visit）
Visit firstVisit = getFirstVisit(userId);
ProfileComputer.setFirstReferralFromVisit(profile, firstVisit);

// 3. 新增業務屬性
profile.setCustomProperty("membership_level", "premium");
profile.setCustomProperty("loyalty_points", 3500);
profile.setCustomProperty("lifetime_value", 15000.0);

// 4. 定義計算屬性（使用 DSL 表達式）
profile.defineComputedProperty("os", "TOP(userData.visits, 'os')");
profile.defineComputedProperty("browser", "TOP(userData.visits, 'browser')");
profile.defineComputedProperty("total_visits", "COUNT(userData.visits)");

// 5. 計算動態屬性
ProfileComputer computer = new ProfileComputer(evaluator, contextManager);
profile = computer.computeProperties(profile, userData);

// 6. 使用完整的使用者畫像
System.out.println("User: " + profile.getUserId());
System.out.println("Location: " + profile.getCity() + ", " + profile.getCountry());
System.out.println("Age: " + profile.getCustomProperty("age"));
System.out.println("Browser: " + profile.getCustomProperty("browser"));
System.out.println("Total Visits: " + profile.getCustomProperty("total_visits"));
System.out.println("Membership: " + profile.getCustomProperty("membership_level"));
System.out.println("First Source: " + profile.getFirstReferral().getSource());
```

## DSL 篩選範例

### 地理位置篩選

```java
// 台灣使用者
profile("country") == "Taiwan"

// 台北使用者
profile("city") == "Taipei"

// 亞洲使用者
profile("continent") == "Asia"
```

### 人口統計篩選

```java
// 年輕成人
profile("age") >= 18 && profile("age") < 35

// 特定年齡區間
profile("age_range") == "25-34"

// 男性使用者
profile("gender") == "male"
```

### 裝置篩選

```java
// 行動裝置使用者
profile("device") == "mobile"

// iOS 使用者
profile("os") CONTAINS "iOS"

// Chrome 使用者
profile("browser") == "Chrome"
```

### 行為篩選

```java
// 高活躍使用者
profile("total_visits") > 20

// 新使用者
profile("user_type") == "new"

// 回訪使用者
profile("user_type") == "returning"
```

### 生命週期篩選

```java
// 從自然搜尋獲取的新使用者
profile("user_type") == "new" && profile("first_referral").medium == "organic"

// 從特定活動獲取的使用者
profile("first_referral").campaign == "summer_sale"

// 從 Google 來的使用者
profile("first_referral").source == "google.com"
```

### 業務屬性篩選

```java
// VIP 客戶
profile("membership_level") == "premium" && profile("loyalty_points") > 5000

// 高價值客戶
profile("lifetime_value") > 10000

// 最近購買的客戶
dateDiff(now(), profile("last_purchase_date"), "days") < 30
```

## 最佳實踐

1. **固定屬性**: 用於真正不變的使用者資料（地理位置、性別、生日、語言）
2. **自訂屬性**: 用於業務相關的靜態資料（會員等級、積分）
3. **計算屬性**: 用於需要從 Visit/Event 聚合的資料（裝置、瀏覽器、訪問次數）
4. **First Referral**: 在首次訪問時設定一次，之後不再修改
5. **標準模板**: 使用 `ProfileComputer.createWithStandardComputedProperties()` 快速建立

## 注意事項

1. **Birthday vs Age**: 使用 birthday 儲存，age 會自動計算
2. **First Referral**: 整合了 first_source, first_medium, first_campaign
3. **自動計算**: age, age_range, first_visit_date 會自動計算
4. **型別轉換**: customProperty 返回 Object，需要型別轉換
5. **DSL 統一**: 在 DSL 中，所有屬性都用 `profile()` 函數存取

## 相關文檔

- [Function Reference](FUNCTION_REFERENCE.md) - DSL 函數參考
- [Use Case Examples](USE_CASE_EXAMPLES.md) - 實際使用案例
- [API Documentation](API.md) - 完整 API 文檔
