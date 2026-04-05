## 3.0.0（尚未发布）

本次为重大重构版本，包含大量破坏性变更。整体架构全面现代化，简化使用与维护成本，提升引擎性能与稳定性。

### 新特性

+ 支持使用 Java 原生 Builder 模式构建 Wit 引擎实例，API 更流畅易用。
+ 支持对 Record 对象执行 `getProperty(bean, propertyName)` 属性获取操作。
+ 新增 Native Class 语法，可在脚本中直接获取 Java Class 对象。
+ 新增内置断言方法。
+ 断言失败时抛出 `WitAssertionError`，并自动记录完整脚本调用栈信息。
+ `SecurityLoaderDecorator`（原 `SecurityLoader`）使用 `PathTrie` 实现路径允许/拒绝规则，替代原简单前缀匹配。
+ `ClasspathLoader` 支持传入指定 `ClassLoader` 加载资源。
+ `FileSystemLoader`（原 `FileLoader`）支持传入指定 `FileSystem` 加载资源。
+ 脚本 Map 字面量现在默认创建 `LinkedHashMap` 实例，保持键值插入顺序。

### 优化

+ 空安全注解迁移至 JSpecify 标准。
+ 完善单元测试，提升代码覆盖率。
+ 优化语句块运行时执行模型，采用分批执行减少控制状态检查，提升执行性能。

### 问题修复

+ 修复 ALU 在处理 Double/Float 类型计算时因类型转换导致的精度丢失问题。
+ 修复 ALU `isTruly()` 布尔判断逻辑，现在 0、0.0、-0 均判定为 false。

### 破坏性变更

+ 最低支持 JDK 17，不再支持 Java 8。
+ 迁移至 Jakarta EE 11，不再支持 Java EE 8。
+ 全面重构项目结构，重新设计包路径、组件命名与模块职责。
+ 移除配置文件支持，统一使用 Builder 模式构建引擎实例。
+ 清理所有已废弃方法。
+ 扩展模块合并至核心模块，简化整体结构。
+ Core 模块使用 `slf4j-api` 作为日志门面，成为唯一必需外部依赖。
+ 资源加载默认模式改为 `BeginWith.SCRIPT`，替代原 `codeFirst=false` 行为。
