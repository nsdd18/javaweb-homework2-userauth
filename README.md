# 软件2501班 第二次作业 —— 登录 / 注册 / 忘记密码

> **题目**：① 完成登录页面、注册页面、忘记密码三个前端页面；② 完成处理登录页面、注册页面、忘记密码三个页面对应的 Servlet；③ 项目代码托管使用 git；④ 作业提交 git 仓库即可。

| 项目 | 内容 |
| --- | --- |
| 姓名 | 杨鑫 |
| 学号 | 25040500 |
| 班级 | 软件2501班 |
| 开发工具 | IntelliJ IDEA 2025.3 (Ultimate) |
| 构建工具 | Maven 3.9.9 |
| JDK | 17+（本项目按 Java 17 编译） |
| 服务器 | Apache Tomcat 10.1.33 |
| 数据库 | MySQL 8.0.12 |
| Servlet 规范 | Jakarta Servlet 6.0（Tomcat 10 的包名为 `jakarta.servlet.*`） |
| 访问地址 | <http://localhost:8080/homework2/> |
| 代码仓库 | <https://github.com/nsdd18/javaweb-homework2-userauth> |

---

## 〇、作业提交信息（git 仓库）

```bash
git clone https://github.com/nsdd18/javaweb-homework2-userauth.git
```

本地项目目录：`JavaWeb-Homework2-杨鑫-25040500`
GitHub 仓库名：`javaweb-homework2-userauth`（仓库名用英文，避免中文路径在部分工具下出问题；文件夹名仍按要求保留中文）

---

## 一、项目结构

```
JavaWeb-Homework2-杨鑫-25040500
├── pom.xml                                  # Maven 配置（war 打包）
├── init_database.sql                        # ★ 建库、建表、建账号脚本
├── .gitignore / .gitattributes              # git 忽略规则 / 换行符统一为 LF
├── README.md                                # 本说明文档
├── 推送到GitHub步骤.md                       # 提交本作业到 GitHub 的步骤
├── 运行截图/                                 # 三个页面与运行结果截图
└── src/main
    ├── java/com/yangxin
    │   ├── entity/User.java                 # 用户实体（对应 user 表）
    │   ├── dao/UserDao.java                 # ★ 数据访问层（增、查、改密码）
    │   ├── util/DbUtil.java                 # ★ 读取 db.properties、获取连接
    │   ├── util/PasswordUtil.java           # ★ 密码加盐 SHA-256 哈希与校验
    │   ├── util/Validator.java              # 服务端表单校验 + XSS 转义
    │   ├── filter/EncodingFilter.java       # UTF-8 编码过滤器
    │   └── servlet
    │       ├── LoginServlet.java            # ★ 对应登录页面
    │       ├── RegisterServlet.java         # ★ 对应注册页面
    │       ├── ForgotPasswordServlet.java   # ★ 对应忘记密码页面
    │       └── LogoutServlet.java           # 退出登录（附加）
    ├── resources/db.properties              # 数据库连接配置
    └── webapp
        ├── index.jsp                        # 项目首页（三个页面的入口）
        ├── login.jsp                        # ★ 前端页面一：登录
        ├── register.jsp                     # ★ 前端页面二：注册
        ├── forgot.jsp                       # ★ 前端页面三：忘记密码（两步）
        ├── result.jsp                       # 统一的处理结果页面
        ├── css/  (bootstrap.min.css, style.css)
        ├── js/   (bootstrap.bundle.min.js)
        └── WEB-INF/web.xml                  # 部署描述符
```

> 说明：三个前端页面虽然只用到 EL 表达式回显错误提示，但 Tomcat 只会对 `*.jsp` 做 EL 解析，
> 因此页面采用 JSP 实现（`login.jsp`、`register.jsp`、`forgot.jsp`），页面的主要工作是表单与样式。

## 二、三个页面与三个 Servlet 的对应关系

| 前端页面 | 访问地址 | 对应 Servlet | 映射路径 | 功能 |
| --- | --- | --- | --- | --- |
| login.jsp | `/login` | `LoginServlet` | `@WebServlet("/login")` | 校验用户名密码，成功后把用户存入 Session |
| register.jsp | `/register` | `RegisterServlet` | `@WebServlet("/register")` | 校验表单 → 查重 → 密码加盐哈希 → 写入 MySQL |
| forgot.jsp | `/forgot` | `ForgotPasswordServlet` | `@WebServlet("/forgot")` | 两步：①用户名+邮箱验证身份 ②设置新密码 |

每个 Servlet 都用同一个模式：
`doGet()` 转发到对应页面显示表单，`doPost()` 读取表单参数并处理业务，处理完转发到 `result.jsp` 显示结果。

## 三、数据库设计

库名 `javaweb_homework`，表 `user`：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | INT UNSIGNED AUTO_INCREMENT | 主键 |
| username | VARCHAR(50) UNIQUE | 登录用户名，唯一索引 `uk_username` |
| password | CHAR(64) | `SHA-256(盐值 + 密码)` 的十六进制字符串，**不存明文** |
| salt | CHAR(32) | 每个用户独立的 16 字节随机盐 |
| email | VARCHAR(100) | 注册邮箱，找回密码时核对身份 |
| phone | VARCHAR(20) | 手机号（选填） |
| create_time | DATETIME | 注册时间（默认 CURRENT_TIMESTAMP） |
| update_time | DATETIME | 最后更新时间（ON UPDATE CURRENT_TIMESTAMP） |

初始化（只需执行一次）：

```bash
mysql -uroot -p < init_database.sql
```

脚本会创建数据库、`user` 表，以及应用专用账号 `javaweb`（密码 `javaweb123`，只对 `javaweb_homework` 库有权限，不使用 root）。

数据库连接配置在 `src/main/resources/db.properties`：

```properties
db.url=jdbc:mysql://localhost:3306/javaweb_homework?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
db.username=javaweb
db.password=javaweb123
```

> ⚠️ 踩坑记录：`characterEncoding` 必须写 `UTF-8`。如果写成 `utf8mb4`，
> MySQL Connector/J 会抛 `Unsupported character encoding 'utf8mb4'` 异常。

**本机密码不一样怎么办**：不要去改 `db.properties`（它要进 git），
在同目录新建 `db.local.properties` 覆盖需要的键即可，`DbUtil` 会自动读取它，
该文件已被 `.gitignore` 排除：

```properties
db.username=root
db.password=你的密码
```

## 四、在 IDEA 中运行

1. 用 IDEA 打开项目根目录（`pom.xml` 作为 Maven 项目导入）。
2. `File → Project Structure → Project`，SDK 选择 **JDK 17 及以上**。
3. `Run → Edit Configurations → + → Tomcat Server → Local`，
   `Application server` 选择本机 Tomcat 目录（`D:\devtools\apache-tomcat-10.1.33`）。
4. 切到 `Deployment` 页签 → `+` → `Artifact` → 选择 `javaweb-userauth-homework:war exploded`，
   `Application context` 填 `/homework2`。
5. 启动 Tomcat，浏览器访问 <http://localhost:8080/homework2/>。

### 命令行方式（不用 IDEA）

```bash
mvn clean package
# 把 target/homework2.war 复制到 Tomcat 的 webapps 目录，启动 Tomcat
```

**前置条件**：MySQL 必须已启动（phpStudy 面板里启动 MySQL 8.0.12）。

## 五、使用流程（可直接按这个顺序演示）

1. 打开首页 → 点「进入注册页」→ 填写用户名、密码、确认密码、邮箱 → 提交，
   页面提示「注册成功」，数据已写入 MySQL。
2. 点「去登录」→ 用刚才的账号登录（先故意输错密码可以看到红色错误提示且用户名被回填）；
   密码正确则提示「登录成功」，并显示用户编号、用户名、邮箱、注册时间。
3. 在登录页点「忘记密码？」→ 输入**用户名 + 注册时填写的邮箱** → 验证通过后进入第二步 →
   设置新密码 → 提示「密码重置成功」。
4. 用**新密码**登录成功，用旧密码登录失败，说明密码确实被改掉了。

## 六、安全性设计（区别于“只做出来”的实现）

| 措施 | 实现位置 | 说明 |
| --- | --- | --- |
| 密码加盐哈希 | `PasswordUtil` | 每用户独立随机盐 + SHA-256，数据库里查不到明文 |
| 时间恒定比较 | `PasswordUtil.verify` | 逐字节异或比较，避免比较耗时泄露信息 |
| 防 SQL 注入 | `UserDao` | 全部使用 `PreparedStatement` 预编译参数 |
| 防 XSS | `Validator.escapeHtml` | 用户输入回显到页面时做 HTML 转义 |
| 服务端二次校验 | `Validator` | HTML5 校验可被绕过，服务端再验一遍用户名/密码/邮箱/手机号格式 |
| 防越权重置密码 | `ForgotPasswordServlet` | “已验证身份”的状态放在 **Session** 里，而不是隐藏域；直接构造 `step=2` 请求会被拒绝 |
| 登录提示不泄露信息 | `LoginServlet` | 用户不存在与密码错误统一提示「用户名或密码错误」，避免枚举账号 |
| 统一编码 | `EncodingFilter` | `@WebFilter("/*")` 把所有请求响应设为 UTF-8，中文不乱码 |

## 七、自测结果

用脚本对部署好的应用发真实 HTTP 请求，走完「注册 → 登录 → 忘记密码 → 新密码登录」全流程，
并直连数据库核对数据，共 **22 项断言全部通过**：

```
步骤 0  GET /login 页面渲染（JSTL 可用、无编译错误）        3/3
步骤 1  注册成功 + 数据库核对（64 位哈希、无明文）            4/4
步骤 1b 重复用户名注册被拒绝                                  1/1
步骤 1c 两次密码不一致被拒绝                                  1/1
步骤 2  错误密码登录失败 + 用户名回填                         2/2
步骤 3  正确密码登录成功                                      2/2
步骤 4  错误邮箱验证身份失败                                  1/1
步骤 4b 绕过第一步直接改密码被拒绝                            1/1
步骤 5  身份验证通过 → 重置密码成功                           2/2
步骤 6  新密码可登录、旧密码失效                              2/2
步骤 7  首页可访问、退出登录回到登录页                        2/2
```

对应截图见 `运行截图/` 目录。

## 八、运行截图清单

| 文件 | 内容 |
| --- | --- |
| 1-登录页面.png | 登录页面（前端页面一） |
| 2-注册页面.png | 注册页面（前端页面二） |
| 3-忘记密码页面-第一步.png | 忘记密码页面第一步（前端页面三） |
| 4-项目首页.png | 项目首页，三个页面入口 |
| 5-注册成功.png | 注册成功，数据已入库 |
| 6-登录失败提示.png | 密码错误时的红色提示 + 用户名回填 |
| 7-登录成功.png | 登录成功，显示用户信息 |
| 8-忘记密码-第二步设置新密码.png | 身份验证通过后设置新密码 |
