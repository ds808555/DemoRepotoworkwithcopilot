---
applyTo: "src/main/java/com/anhtester/keywords/**/*.java"
---
# Keywords instructions (WebUI/submodule)

- Treat `src/main/java/com/anhtester/keywords` as a git submodule (`.gitmodules`); verify submodule state before assuming missing methods are framework bugs.
- Keep keyword methods framework-generic and reusable across CRM/CMS; avoid embedding page-specific locators or business flows.
- Preserve the existing static-call style used by pages/tests (`WebUI.clickElement`, `WebUI.setText`, `WebUI.verify*`, `WebUI.waitForPageLoaded`).
- Ensure all keyword operations use the active ThreadLocal session from `DriverManager.getDriver()`; never instantiate drivers in keyword classes.
- Keep failure behavior aligned with current `FailureHandling` patterns (`STOP_ON_FAILURE` default, soft logging where explicitly requested).
- Favor explicit waits and stable synchronization over unconditional sleeps; only retain sleeps where existing app timing requires them.
- Keep assertion/verification messages actionable because these messages surface in Extent/Allure reports.
- Avoid introducing dependencies on TestNG reporter context in core keyword methods.
- Maintain Java 17 and Selenium 4.34 compatibility with existing method signatures to avoid breaking page-object callers.
- If adding a new keyword used broadly, mirror naming style and parameter patterns already present in `WebUI` to keep call sites consistent.
