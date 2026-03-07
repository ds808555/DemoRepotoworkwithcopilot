---
applyTo: "src/test/java/com/anhtester/listeners/**/*.java"
---
# Listener instructions

- Keep report behavior toggle-driven from `config.properties` (`SCREENSHOT_*`, `VIDEO_RECORD`, `SEND_REPORT_TO_TELEGRAM`, `ZIP_FOLDER`, `SEND_EMAIL_TO_USERS`).
- Preserve lifecycle responsibilities in `TestListener`: counters/start-finish hooks, logging, report flush, zip, telegram, email.
- Retry behavior is centralized via `AnnotationTransformer` + `Retry`; avoid per-test custom retry wiring unless necessary.
- Be careful with `BrowserInfoUtils.getBrowserInfo()` because it depends on current TestNG result context.
