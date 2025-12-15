# Seminar Management System - 项目概述

## 项目简介
FCI学院研究生学术研讨会管理系统，基于 Java Swing 开发的桌面应用程序。

## 技术栈
- Java 17+
- Swing (GUI框架)
- MVC 架构模式
- 文件存储 (JSON/序列化)

## 角色体系 (RBAC)
| 角色 | 登录要求 | 权限级别 |
|------|---------|---------|
| Guest | 否 | 只读 - 查看日程和空位 |
| Student | 是 | 有限写入 - 注册和提交材料 |
| Evaluator | 是 | 受控写入 - 评估分配的演示 |
| Coordinator | 是 | 完全控制 - 管理整个系统 |

## 核心模块
1. 用户管理模块 (Coordinator)
2. 研讨会日程模块 (所有角色)
3. 会话管理模块 (Coordinator)
4. 注册模块 (Student, Coordinator)
5. 文件上传模块 (Student)
6. 评估模块 (Evaluator, Coordinator查看)
7. 海报展板管理模块 (Coordinator)
8. 奖项管理模块 (Coordinator)
9. 报告与分析模块 (Coordinator)
