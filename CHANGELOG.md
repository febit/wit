## 3.0.0 (not yet released)

这是一个重大的重置版本，包含大量破坏性更改，旨在简化库的使用和维护，同时引入现代 Java 特性。

### FEATURES:

+ 支持 Record 类，的 Getter 方法。
+ 支持通过 Java 原生的代码 Builder 模式构建 Wit 引擎实例，提供更流畅的 API 体验。

#### IMPROVEMENTS:

+ 迁移至 jspecify, 标注 nullability。

#### BUG FIXES:

+ 修复 ALU 计算 Double 与 Float 类型数值时类型转换导致的精度问题。

### BREAKING CHANGES:

+ 移除对 Java 8 的支持，要求最低 JDK 17，采用现代化的 Java 特性提升性能和可读性
+ 移除对 Java EE 8 的支持，改为支持 Jakarta EE 9
+ 移除配置文件的支持, 请使用 Builder 模式构建 Wit 引擎实例
+ 规范化组件名称, 提高可读性和一致性
+ 大量的结构解耦和简化, 移除过时和冗余的方法，提升库的易用性和维护性
+ 扩展模块合并至 Core 模块，简化模块结构
+ Core 模块引入新的模块依赖 `slf4j-api`，也是目前唯一的外部依赖
