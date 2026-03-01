# Copilot Instructions for this Repository

## Big picture (how the framework is wired)
- This is a Java 17 + Maven Selenium/TestNG automation framework with CRM/CMS projects in one repo.
- Core test flow is: `BaseTest` -> `TargetFactory` -> `BrowserFactory` -> `DriverManager` (ThreadLocal) -> page methods using `WebUI` -> reporting/listeners (`TestListener`, Extent, Allure).
- Use these files to understand the runtime pipeline:
  - `src/test/java/com/anhtester/common/BaseTest.java`
  - `src/main/java/com/anhtester/driver/TargetFactory.java`
  - `src/main/java/com/anhtester/driver/BrowserFactory.java`
  - `src/main/java/com/anhtester/driver/DriverManager.java`
  - `src/test/java/com/anhtester/listeners/TestListener.java`
  - `src/main/java/com/anhtester/reports/ExtentReportManager.java`

## Project structure conventions
- `src/main/java/com/anhtester` holds framework engine code (`driver`, `keywords`, `helpers`, `reports`, `utils`, `constants`).
- `src/test/java/com/anhtester/projects` holds product-specific pages/testcases.
- CMS tests use lazy page accessors from `CommonPageCMS` (e.g., `getLoginPageCMS()`), then tests call page methods; keep that pattern.
- Locators are typically private `By` fields in page classes, and page actions use static imports from `WebUI`.

## Configuration model (important)
- Runtime config comes from `src/test/resources/config/config.properties` and is read into `FrameworkConstants` static fields once.
- When changing config behavior, check both:
  - `src/main/java/com/anhtester/constants/FrameworkConstants.java`
  - `src/test/resources/config/config.properties`
- Remote execution is controlled by `TARGET`, `REMOTE_URL`, `REMOTE_PORT`; browser selection comes from `BROWSER`/suite parameter.

## Build/test workflows (repo-specific)
- Default CI command: `mvn clean test allure:report` (see `.github/workflows/maven.yml`).
- Current Surefire default suite is hardcoded to CMS login suite:
  - `src/test/resources/suites/CMS/LoginTestCMS.xml`
  - configured in `pom.xml` under `maven-surefire-plugin` `suiteXmlFiles`.
- To run one class locally from Maven, use PowerShell-safe quoting:
  - `mvn "-Dtest=LoginTest" test`
- Allure local viewing from existing results:
  - `allure serve target/allure-results`

## Retry, listeners, and reports
- Retries are globally injected by TestNG transformer:
  - `AnnotationTransformer` + `Retry` using `RETRY_TEST_FAIL` from config.
- `BaseTest` already registers `@Listeners(TestListener.class)`; avoid duplicate listener registration in tests unless required.
- Extent outputs to `exports/ExtentReports/ExtentReports.html` (or timestamped file when `OVERRIDE_REPORTS=no`).
- Optional integrations (Telegram/email/zip/video/screenshots) are feature-flagged in `config.properties`; keep behavior toggle-driven.

## Data and assertion patterns
- DataProviders commonly return `Hashtable<String,String>` from Excel (`DataProviderManager` + model key classes).
- Keep page methods assertion-oriented using existing `WebUI.verify*` methods instead of ad-hoc sleeps/assert style where possible.
- Preserve ThreadLocal driver safety (`DriverManager`) and `ThreadGuard.protect(...)` usage in `BaseTest`.

## Known implementation detail to respect
- `src/main/java/com/anhtester/keywords` is a git submodule (`.gitmodules`). If methods seem missing/outdated, verify submodule state before refactoring.
- `BrowserInfoUtils.getBrowserInfo()` depends on TestNG reporter context; avoid calling it from non-test context code paths.
