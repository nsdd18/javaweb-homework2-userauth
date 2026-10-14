# 推送到 GitHub 的步骤（软2501班第二次作业）

本地项目：`C:\Users\yangxin\Desktop\JavaWeb-Homework2-杨鑫-25040500`
目标仓库：`https://github.com/nsdd18/javaweb-homework2-userauth`

本地 git 仓库**已经初始化并提交好了**（2 次提交，main 分支，32 个文件），
远程地址也已配置为 `origin`。你只需要做下面 3 步。

---

## 第 1 步：在 GitHub 上新建空仓库

1. 打开 <https://github.com/new>
2. **Repository name** 填：`javaweb-homework2-userauth`
3. **Description** 填：`软件2501班第二次作业：登录/注册/忘记密码三个页面及对应 Servlet（Servlet + MySQL）`
4. 选择 **Public**（老师需要能访问）
5. ⚠️ **不要**勾选 "Add a README file"、不要选 .gitignore、不要选 license
   （本地已经有文件了，勾了会冲突）
6. 点 **Create repository**

## 第 2 步：推送代码

打开 PowerShell，执行：

> **状态：本作业已经推送完成**，下面的第 1、2 步留作以后改代码时参考。

```powershell
cd "C:\Users\yangxin\Desktop\JavaWeb-Homework2-杨鑫-25040500"
git add -A
git commit -m "说明这次改了什么"
git push
```

（首次推送才需要 `git push -u origin main` 来设置上游分支，已经设置过了，以后直接 `git push`。）

### ⚠️ 本机特有的 TLS 问题及解决办法（重要）

本机装了 **Steam++（Watt Toolkit）** 做 GitHub 加速，它在 hosts 里把 `github.com` 等
**25 个域名**都指向了 `127.0.0.1` 走本地代理。

Git 默认用 Windows 的 TLS 后端（schannel），连这个本地代理时会因为
"无法查询证书吊销状态"直接失败：

```
schannel: next InitializeSecurityContext failed: CRYPT_E_NO_REVOCATION_CHECK (0x80092012)
```

我实测了几种方案（每种连打 6 次）：

| 方案 | 成功率 | 说明 |
| --- | --- | --- |
| 默认（走代理，schannel） | 0/6 | 报上面的错 |
| `http.sslVerify=false` | 6/6 | 能用，但**关掉了证书校验**，不推荐 |
| 走代理 + 只加信任 SteamTools 证书 | 0/6 | schannel 依然卡在吊销检查 |
| **openssl 后端 + 信任 SteamTools 证书** | 6/6 | 校验保持开启（备选方案） |
| **直连 GitHub 真实 IP** | 6/6 | 校验保持开启（当前采用） |

**当前采用**：本仓库 `.git/config` 里配了一条 `http.curloptResolve`，
让 git 绕过本地代理直连 GitHub 真实 IP，TLS 校验完整保留：

```bash
git config --local http.curloptResolve github.com:443:20.205.243.166
```

这是**仓库级**配置，不影响你其他项目；也没有去改全局的 `http.sslVerify`。

**如果哪天又连不上**（GitHub 换 IP 了），先试备选方案：

```powershell
cd "C:\Users\yangxin\Desktop\JavaWeb-Homework2-杨鑫-25040500"
git config --local http.sslBackend openssl
git config --local http.sslCAInfo "C:\Users\yangxin\.git-certs\steamtools-ca.pem"
```

或者更新直连 IP：

```powershell
# 注意要用 8.8.8.8 查询，因为本机 hosts 被加速工具改成了 127.0.0.1
(Resolve-DnsName github.com -Server 8.8.8.8 -Type A).IPAddress
git config --local http.curloptResolve "github.com:443:查到的IP"
```

> 说明：Steam++ 的根证书（`CN=SteamTools Certificate`）已经装在系统证书库里，
> 上面用到的 `steamtools-ca.pem` 就是从系统证书库导出到 `C:\Users\yangxin\.git-certs\` 的。
> 如果哪天重装了 Steam++，这个证书会变，需要重新导出。

如果你不想用加速工具，关闭 Steam++ 的 GitHub 加速后 git 也能直连（但国内可能不稳定）。

## 第 3 步：确认

推送成功后打开 <https://github.com/nsdd18/javaweb-homework2-userauth>
应该能看到 `src/`、`pom.xml`、`init_database.sql`、`README.md`、`运行截图/` 等文件。

作业要求「提交 git 仓库即可」，把这个仓库地址填到作业提交框里：

```
https://github.com/nsdd18/javaweb-homework2-userauth
```

---

## 附：如果不想用命令行（图形化方式）

你桌面上已经装了 **GitHub Desktop**：

1. 先按上面第 1 步在网页上建好空仓库
2. 打开 GitHub Desktop → `File` → `Add Local Repository`
3. 选择 `C:\Users\yangxin\Desktop\JavaWeb-Homework2-杨鑫-25040500`
4. 右上角点 **Publish repository**
   - ⚠️ 注意把 **Keep this code private 的勾去掉**（否则老师看不到）
   - Name 改成 `javaweb-homework2-userauth`
5. 点 **Publish repository**

因为本地仓库里已经配好了 `origin`，GitHub Desktop 一般会直接识别到远程仓库，
此时按钮会变成 **Push origin**，直接点即可。

---

## 附：后续再改代码怎么重新推送

```powershell
cd "C:\Users\yangxin\Desktop\JavaWeb-Homework2-杨鑫-25040500"
git add -A
git commit -m "说明这次改了什么"
git push
```
