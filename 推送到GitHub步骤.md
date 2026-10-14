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
