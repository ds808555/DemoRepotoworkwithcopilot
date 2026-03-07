---
applyTo: "src/main/java/com/anhtester/{driver,constants,config}/**/*.java"
---
# Driver and configuration instructions

- Keep driver lifecycle centralized: creation in `TargetFactory`/`BrowserFactory`, storage in `DriverManager` (ThreadLocal), cleanup via `DriverManager.quit()`.
- Do not create/close WebDriver from tests/pages/helpers; rely on `BaseTest` setup/teardown.
- Respect execution routing by `FrameworkConstants.TARGET` (`local` vs `remote`) in `TargetFactory`.
- Preserve browser fallback logic in `TargetFactory.createInstance(String browser)`: use config browser when provided, otherwise suite parameter.
- Keep browser options and capability flags inside `BrowserFactory` enum entries (CHROME/EDGE/FIREFOX/SAFARI), not scattered elsewhere.
- Read runtime values through `FrameworkConstants` (backed by `PropertiesHelpers`), not direct ad-hoc property reads in random classes.
- When introducing a new config key, update both:
  - `src/test/resources/config/config.properties`
  - `src/main/java/com/anhtester/constants/FrameworkConstants.java`
- Avoid static assumptions that require TestNG reporter context in framework-core code paths.
- Keep remote grid URL/port behavior compatible with existing `REMOTE_URL` + `REMOTE_PORT` composition.
- Maintain Java 17 compatibility and existing Maven/Surefire execution assumptions.
