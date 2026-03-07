---
applyTo: "src/test/java/com/anhtester/projects/**/pages/**/*.java"
---
# Page object instructions (CMS/CRM)

- Keep locators as `private By` fields and name them by UI intent (`buttonSubmitLogin`, `messageRequiredEmail`).
- Prefer static `WebUI` calls (`clickElement`, `setText`, `waitForPageLoaded`, `verifyElementVisible`) over raw Selenium calls.
- Model full user actions in page methods (open page + interact + verify result), as in `LoginPageCMS` methods.
- Keep validations inside page methods using `WebUI.verify*`/`verifyEquals` instead of pushing assertions into test classes.
- For navigation helpers, return page/common objects consistently (`CommonPageCMS`, `CategoryPage`, etc.).
- Reuse existing lazy access patterns from `CommonPageCMS` (`getLoginPageCMS()`, `getProfilePage()`) instead of manually wiring dependencies.
- Use explicit waits first; only keep short `sleep()` where the existing page flow already relies on it and no stable wait exists.
- When adding a new flow, follow existing URL constants from `FrameworkConstants` (`URL_CMS_USER`, `URL_CMS_ADMIN`, `URL_CRM`).
- Avoid creating or quitting drivers in page classes; driver lifecycle belongs to `BaseTest` + `DriverManager`.
- Keep page methods deterministic and side-effect scoped to one scenario so they remain reusable across tests.
