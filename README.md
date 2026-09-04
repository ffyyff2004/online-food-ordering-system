# 网上订餐系统

面向中小餐饮商家的“用户点餐 + 商家后台管理”系统。

## 当前阶段

当前已完成 Spring Boot 后端、用户端和商家后台的核心功能：

- 用户注册、登录（BCrypt 密码加密，返回 JWT）
- 菜品分类查询
- 菜品分页、关键词搜索、详情查询
- MyBatis 分层结构
- MySQL 核心表结构和演示数据
- 统一接口返回格式、参数校验、跨域配置
- 用户资料、收货地址、收藏、评价和意见反馈
- 订单事务、库存扣减、模拟支付和订单状态管理
- 商家后台经营概览、菜品/分类管理、反馈处理和操作日志
- 商家后台用户管理和公告管理，用户端公开展示已上线公告

## 环境要求

- Java 17 或更高版本
- Maven 3.9+
- MySQL 8+

## 初始化数据库

在 MySQL 客户端执行 `database/schema.sql`。

默认连接配置为：`localhost:3306/food_ordering`，用户 `root`，密码 `123456`。
也可以通过 `DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USERNAME`、`DB_PASSWORD` 环境变量覆盖。

## 启动后端

如果 MySQL 没有运行，可以先执行：

```powershell
.\scripts\start-mysql.ps1
```

然后启动后端：

```powershell
mvn spring-boot:run
```

接口基地址：`http://localhost:8080/api`

核心接口：

- `POST /auth/register`、`POST /auth/login`
- `GET /foods`、`GET /foods/categories`、`GET /foods/{id}`
- `GET /cart`、`POST /cart/items`、`PUT /cart/items/{foodId}`、`DELETE /cart/items/{foodId}`
- `POST /orders`、`GET /orders`、`GET /orders/{orderNo}`、`POST /orders/{orderNo}/pay`、`POST /orders/{orderNo}/cancel`
- `GET /notices`（公开查询已上线公告）

购物车和订单接口需要在请求头携带：`Authorization: Bearer 登录返回的token`。

示例：

```powershell
Invoke-RestMethod http://localhost:8080/api/foods/categories
Invoke-RestMethod 'http://localhost:8080/api/foods?page=1&size=10'
```

订单创建、扣减库存和清空购物车在同一事务中完成，避免数据不一致。

## 用户端前端

前端位于 `frontend` 目录，首次使用时执行：

```powershell
Set-Location frontend
npm install
npm run dev
```

浏览器打开 `http://localhost:5173`，即可使用登录、菜品浏览、购物车、下单和模拟支付。

商家后台打开：`http://localhost:5173/admin.html`。

## 商家后台接口

初始管理员账号：`admin`，密码：`admin123456`。

- `POST /admin/auth/login`
- `GET/POST/PUT /admin/categories`
- `GET/POST/PUT /admin/foods`
- `PUT /admin/foods/{id}/status?status=0|1`
- `PUT /admin/foods/{id}/stock?stock=数量`
- `GET /admin/orders?status=状态`
- `PUT /admin/orders/{orderNo}/status?status=PREPARING|READY|COMPLETED|CANCELLED`（按订单当前状态逐步流转）
- `GET /admin/users?keyword=关键词&status=0|1`
- `PUT /admin/users/{id}/status?status=0|1`
- `GET /admin/notices`
- `POST /admin/notices`
- `PUT /admin/notices/{id}`
- `PUT /admin/notices/{id}/status?status=0|1`

## 用户个人中心接口

- `GET/PUT /profile`
- `GET/POST/PUT/DELETE /profile/addresses`
- `GET /favorites`、`POST/DELETE /favorites/{foodId}`
- `GET /comments/food/{foodId}`、`POST /comments`
- `GET/POST /feedback`
- `GET /admin/feedback`、`PUT /admin/feedback/{id}/reply`
- `GET /admin/stats/summary`
- `GET /admin/logs?limit=100`
- `GET /admin/comments`

评价必须满足：当前用户本人、订单中的对应菜品，并且订单状态为 `COMPLETED`。

后台新增经营概览和操作日志页面，可查看营业额、订单量、用户数、热销菜品以及管理员操作记录。

后台还提供用户启用/停用、公告发布/编辑/上线/下线功能。
后台还可以查看用户对菜品的评分和评价内容。

订单状态按流程流转：待支付 → 已支付 → 制作中 → 待取餐 → 已完成；处理中订单可以取消，已完成和已取消订单不能再次修改。

核心测试：`src/test/java/com/foodordering/service/OrderServiceTest.java`，当前覆盖正常下单和库存不足两种场景。
