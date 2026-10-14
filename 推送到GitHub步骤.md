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

```powershell
cd "C:\Users\yangxin\Desktop\JavaWeb-Homework2-杨鑫-25040500"
git push -u origin main
```

会弹出 GitHub 登录窗口（浏览器或凭据管理器）：

- **推荐**：弹出浏览器后点授权即可（Git for Windows 自带的 Git Credential Manager）
- 如果要求输入用户名密码：用户名填 `nsdd18`，
  密码**不能填 GitHub 登录密码**，必须填 **Personal Access Token**
  （生成地址：<https://github.com/settings/tokens> → Generate new token (classic) → 勾选 `repo` → 生成后复制）

### ⚠️ 如果报 TLS / SSL 错误（本机特有，已提前配好）

本机装了 **Steam++（Watt Toolkit）** 做 GitHub 加速，它把 `github.com` 等域名在 hosts 里
指向了 `127.0.0.1` 走本地代理。用手写链接去连它时，Windows 的 schannel 会因为
"无法查询证书吊销状态"而报错：

```
schannel: next InitializeSecurityContext failed: CRYPT_E_NO_REVOCATION_CHECK (0x80092012)
```

**已经为你配好了**：本仓库的 `.git/config` 里加了一条 `http.curloptResolve`，
让 git 直接连 GitHub 的真实 IP，绕过本地代理，TLS 校验保持完整：

```bash
git config --local http.curloptResolve github.com:443:20.205.243.166
```

注意这是**仓库级**配置，不影响你其他项目的 git；也没有去动全局的
`http.schannelCheckRevoke`（那会削弱所有 HTTPS 仓库的安全性）。

如果哪天 GitHub 换了 IP 导致又连不上，用下面两行更新即可：

```powershell
# 查当前真实 IP（注意要用 8.8.8.8 查，本机 hosts 被加速工具改了）
(Resolve-DnsName github.com -Server 8.8.8.8 -Type A).IPAddress
# 用查到的 IP 更新配置
git config --local http.curloptResolve "github.com:443:查到的IP"
```

另一种做法是关闭 Steam++ 的 GitHub 加速再推送（但国内直连可能不稳定）。

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
