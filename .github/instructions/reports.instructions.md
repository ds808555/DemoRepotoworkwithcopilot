---
applyTo: "src/main/java/com/anhtester/reports/**/*.java"
---
# Reporting instructions

- Route report writes through `ExtentReportManager` APIs, not direct Extent usage in test classes.
- Keep report output paths aligned with `FrameworkConstants` (`EXTENT_REPORT_FOLDER`, `EXTENT_REPORT_NAME`, `OVERRIDE_REPORTS`).
- For screenshot logging, preserve safe-session checks before capture (`DriverManager` active + `TakesScreenshot` support).
- Keep suite summary/metadata additions lightweight and Spark-compatible (simple tables/text are preferred).
- Preserve open/flush behavior consistency so finish hooks can zip/send/open reports in `TestListener`.
- Do not hardcode environment details in report code; read from `FrameworkConstants` and config properties.
- Allure metadata/environment writes should remain compatible with existing `TestListener.onFinish` flow.
