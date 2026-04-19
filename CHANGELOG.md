## 3.1.0 (not yet released)

## 3.0.0 (2026-04-19)

This is a major reset release with a large number of breaking changes. It aims to modernize the architecture, simplify
usage and maintenance, and improve performance and robustness.

### FEATURES

+ Support building Wit engine instances using native Java Builder pattern for a smoother API experience.
+ Support `getProperty(bean, propertyName)` for Record objects.
+ Add `RuntimeReference` to defer target resolution until just before property access.
+ Add Native Class syntax to directly access Java Class objects in scripts.
+ Add built-in assertion methods.
+ Throw `WitAssertionError` with full script stack trace when assertion fails.
+ `SecurityLoader` uses `PathTrie` for path allow/deny rules instead of simple
  prefix matching.
+ `ClasspathLoader` supports specifying a `ClassLoader` for resource loading.
+ `FileSystemLoader` (formerly `FileLoader`) supports specifying a `FileSystem` for resource loading.
+ Map literals in scripts now create `LinkedHashMap` instances to preserve insertion order.
+ Enhance `$GLOBAL`/`$LOCAL` to support property, index, and function-call access, e.g., reads can use
  `$LOCAL.property`, `$LOCAL[key]`, or `$LOCAL(key)`, and property/index writes are equivalent to
  `$LOCAL(key, expr)`.

### IMPROVEMENTS

+ Migrated nullability annotations to JSpecify standard.
+ Improved unit tests and increased code coverage.
+ Optimized runtime execution of Statement blocks with batch processing to reduce control state checks and improve
  performance.
+ Optimized reflection calls with MethodHandle/VarHandle for better performance.

### BUG FIXES

+ Fixed precision loss caused by type conversion in ALU when calculating Double and Float values.
+ Fixed ALU `isTruly()` logic for numeric values; 0, 0.0, and -0 are now correctly treated as false.

### BREAKING CHANGES

+ Updated minimum JDK version to 17; Java 8 is no longer supported.
+ Migrated to Jakarta EE 11; Java EE 8 is no longer supported.
+ Full project restructuring with redesigned package structure, component naming, and module responsibilities.
+ Removed configuration file support; use builder pattern to construct engine instances.
+ Removed all deprecated methods.
+ Merged extension modules into core for a simplified module structure.
+ Core now uses `slf4j-api` for logging as the only required external dependency.
+ Changed default resource loading mode to `BeginWith.SCRIPT`, replacing the old `codeFirst=false` behavior.
