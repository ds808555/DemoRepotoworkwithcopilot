# Path-specific Copilot instruction index

This folder contains path-scoped instruction files for GitHub Copilot using frontmatter `applyTo` patterns.

## File map

| File | applyTo scope | Purpose |
|---|---|---|
| `page-objects.instructions.md` | `src/test/java/com/anhtester/projects/**/pages/**/*.java` | Page object locator/action/assertion conventions |
| `test-cases.instructions.md` | `src/test/java/com/anhtester/projects/**/testcases/**/*.java` | TestNG test orchestration patterns |
| `dataprovider.instructions.md` | `src/test/java/com/anhtester/dataprovider/**/*.java` | Excel/Hashtable DataProvider patterns |
| `listeners-and-reports.instructions.md` | `src/test/java/com/anhtester/listeners/**/*.java` | Listener lifecycle/retry/report trigger rules |
| `reports.instructions.md` | `src/main/java/com/anhtester/reports/**/*.java` | Extent/Allure report implementation rules |
| `driver-config.instructions.md` | `src/main/java/com/anhtester/{driver,constants,config}/**/*.java` | Driver factory and config-key behavior |
| `helpers-utils.instructions.md` | `src/main/java/com/anhtester/{helpers,utils}/**/*.java` | Shared helper/utility constraints |
| `keywords.instructions.md` | `src/main/java/com/anhtester/keywords/**/*.java` | `WebUI` keyword and submodule-safe guidance |

## Maintenance notes

- Keep each file focused on repo-specific behavior (not generic coding advice).
- When adding new code areas, create a new `*.instructions.md` with a narrow `applyTo` pattern.
- If a path is covered by multiple files, the guidance may combine; avoid contradictory rules.
- After changing package structure, review `applyTo` globs to prevent stale scope mappings.
