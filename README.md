# 言磎精品

在线地址： https://acidiconionas-yanxi.vercel.app/

## 1. 项目概述
言磎精品是一个在线教育平台，主要面向教师和学生用户，提供作业管理和班级管理等功能。项目采用前后端分离架构，包含宣传页面和功能页面两大部分。

## 2. 技术栈
- 前端：Vue 3
- 后端：Java
- 数据库：MySQL
- 部署：Vercel（前端）

## 3. 系统架构
项目分为三个主要部分：
- `yanxi-web`：前端项目
- `yanxi-backend`：后端项目
- `yanxi-api`：API接口层

## 4. 核心功能

### 4.1 宣传页面（静态页面）
- Home（首页）
- About（关于）
- Courses（课程）
- Resources（资源）

### 4.2 教师端功能
- 用户认证（登录）
- 班级管理（增删改查）
- 学生管理（添加、删除、查询）
- 作业管理（发布、查看、反馈）
- 作业搜索

### 4.3 学生端功能
- 用户管理（注册、登录、密码修改）
- 班级加入（通过班级编码）
- 作业管理（查看、提交）
- 作业搜索

## 5. 项目启动说明

### 5.1 环境要求
- JDK 8+
- Node.js 14+
- MySQL 5.7+
- Maven 3.6+

### 5.2 后端启动步骤
1. 配置数据库
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE yanxi;
USE yanxi;

# 导入数据库结构
mysql -u root -p yanxi < db_schema.sql
```

2. 启动后端服务
```bash
cd yanxi-backend
mvn spring-boot:run
```

### 5.3 前端启动步骤
```bash
cd yanxi-web
npm install
npm run dev
```

### 5.4 访问地址
- 本地开发环境：
  - 前端：http://localhost:5173
  - 后端：http://localhost:8080
- 生产环境：
  - 前端：https://acidiconionas-yanxi.vercel.app/

## 6. 注意事项
1. 首次运行需要配置数据库连接信息
2. 确保所有依赖包正确安装
3. 注意文件上传大小限制的配置
4. 建议在开发环境中使用测试数据