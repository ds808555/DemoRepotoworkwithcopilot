---
applyTo: "src/main/java/com/anhtester/{helpers,utils}/**/*.java"
---
# Helpers and utils instructions

- Keep helpers/utils generic and reusable across CRM/CMS; avoid embedding product-specific page logic.
- Prefer existing helper abstractions (`ExcelHelpers`, `PropertiesHelpers`, `SystemHelpers`, `CaptureHelpers`) before adding new utility classes.
- Maintain current path strategy for files and exports: `SystemHelpers.getCurrentDir()` + `FrameworkConstants` constants.
- For utility methods used in listeners/reports, preserve null-safety and execution-context safety (test context may be unavailable in some hooks).
- Keep logging routed through `LogUtils` for framework consistency.
- Avoid introducing direct driver creation or teardown in utils/helpers; only interact with active sessions via `DriverManager.getDriver()` where necessary.
- For archive/export operations (for example zip/report helpers), keep behavior toggle-driven from `config.properties`.
- When changing data/serialization helpers, ensure compatibility with existing Excel/JSON usage in DataProviders and CMS tests.
- Do not hardcode environment-specific paths, credentials, or machine-dependent values.
- Keep methods small, deterministic, and side-effect scoped so they remain safe in parallel TestNG execution.
