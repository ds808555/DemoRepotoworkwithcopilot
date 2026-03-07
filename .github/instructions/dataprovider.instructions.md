---
applyTo: "src/test/java/com/anhtester/dataprovider/**/*.java"
---
# DataProvider instructions

- Return `Object[][]` with `Hashtable<String, String>` entries to match current framework consumers.
- Keep provider names descriptive and stable (`getSignInDataHashTable`, `getClientDataHashTable`) because tests reference them directly.
- Resolve file paths with `SystemHelpers.getCurrentDir()` + `FrameworkConstants` constants, not hard-coded absolute paths.
- Use `ExcelHelpers.getDataHashTable(...)` pattern for consistency with existing sheets and model key classes.
- Keep `parallel=true/false` explicit per provider to control test execution behavior intentionally.
- Use model key classes (`SignInModel`, `ClientModel`) for key access where available to avoid string drift.
- Do not introduce random schema changes in Excel columns without updating all dependent tests/pages.