---
applyTo: "src/test/java/com/anhtester/projects/**/{testcases,tests}/**/*.java"
---
# Test case instructions (TestNG)

- Extend `BaseTest` for all UI tests; do not initialize WebDriver directly in test classes.
- Follow current naming style: `TC_<ScenarioName>` methods and one clear scenario per `@Test`.
- Use page accessors from common pages (`getLoginPageCMS()`, `getOrderPage()`) instead of `new` page object calls where possible.
- Keep test classes orchestration-focused: setup data + call page methods; keep UI assertions mainly in page layer.
- Load Excel test data using `ExcelHelpers` + `FrameworkConstants` paths (for example `EXCEL_CMS_LOGIN`, `EXCEL_CMS_DATA`).
- Use existing row/column conventions already present in each test class before adding new data rows.
- Avoid adding `@Listeners` in test classes because `BaseTest` already registers `TestListener` globally.
- If a new test class should run in CI/default Maven run, update relevant suite XML under `src/test/resources/suites/`.
- For focused local run from PowerShell, use quoted form: `mvn "-Dtest=LoginTest" test`.
- Keep priority ordering only when required for dependent scenario readability; otherwise prefer independent tests.
