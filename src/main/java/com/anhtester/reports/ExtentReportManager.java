package com.anhtester.reports;

import com.anhtester.constants.FrameworkConstants;
import com.anhtester.driver.DriverManager;
import com.anhtester.enums.AuthorType;
import com.anhtester.enums.Browser;
import com.anhtester.enums.CategoryType;
import com.anhtester.utils.*;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
//import tech.grasshopper.reporter.ExtentPDFReporter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExtentReportManager {

    private static ExtentReports extentReports;
    private static String link = "";

    public static void initReports() {
        if (Objects.isNull(extentReports)) {
            extentReports = new ExtentReports();

            if (FrameworkConstants.OVERRIDE_REPORTS.trim().equals(FrameworkConstants.NO)) {
                LogUtils.info("OVERRIDE EXTENT REPORTS = " + FrameworkConstants.OVERRIDE_REPORTS);
                link = FrameworkConstants.EXTENT_REPORT_FOLDER_PATH + File.separator + DateUtils.getCurrentDateTimeCustom("_") + "_" + FrameworkConstants.EXTENT_REPORT_FILE_NAME;
                LogUtils.info("Link Extent Report: " + link);
            } else {
                LogUtils.info("OVERRIDE EXTENT REPORTS = " + FrameworkConstants.OVERRIDE_REPORTS);
                link = FrameworkConstants.EXTENT_REPORT_FILE_PATH;
                LogUtils.info("Link Extent Report: " + link);
            }

//            ExtentPDFReporter pdf = new ExtentPDFReporter("reports/ExtentReports/PdfReport.pdf");
//            try {
//                pdf.loadJSONConfig(new File("src/test/resources/pdf-config.json"));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            extentReports.attachReporter(pdf);

            ExtentSparkReporter spark = new ExtentSparkReporter(link);
            extentReports.attachReporter(spark);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle(FrameworkConstants.REPORT_TITLE);
            spark.config().setReportName(FrameworkConstants.REPORT_TITLE);
            extentReports.setSystemInfo("Framework Name", FrameworkConstants.REPORT_TITLE);
            extentReports.setSystemInfo("Author", FrameworkConstants.AUTHOR);

            LogUtils.info("Extent Reports is installed.");
        }
    }

    public static void flushReports() {
        if (Objects.nonNull(extentReports)) {
            extentReports.flush();
        }
        ExtentTestManager.unload();
        ReportUtils.openReports(link);
    }

    public static void createTest(String testCaseName) {
        String icon = safeBrowserIcon();
        String displayName = normalizeTestName(testCaseName);
        String title = icon.isEmpty() ? displayName : icon + " " + displayName;
        ExtentTestManager.setExtentTest(extentReports.createTest(title));
        //ExtentTestManager.setExtentTest(extentReports.createTest(testCaseName));
    }

    public static void createTest(String testCaseName, String description) {
        ExtentTestManager.setExtentTest(extentReports.createTest(normalizeTestName(testCaseName), description));
    }

    public static void createSuiteSummary(String suiteName,
                                          int total,
                                          int passed,
                                          int failed,
                                          int skipped,
                                          long runDurationMs,
                                          double avgDurationMs) {
        String[][] data = {
                {"Suite", suiteName},
                {"Project", detectProjectName(suiteName)},
                {"Browser", detectBrowserName()},
                {"Headless", FrameworkConstants.HEADLESS},
                {"Total", String.valueOf(total)},
                {"Passed", String.valueOf(passed)},
                {"Failed", String.valueOf(failed)},
                {"Skipped", String.valueOf(skipped)},
                {"Avg Duration", formatDuration((long) avgDurationMs)},
                {"Run Time", formatDuration(runDurationMs)}
        };

        createTest("📊 Suite Summary - " + normalizeTestName(suiteName));
        addCategories(new CategoryType[]{CategoryType.REGRESSION});
        info(MarkupHelper.createTable(data));
        logMessage(Status.INFO, "Use category/device filters to isolate module/type and slow tests quickly.");
    }

    public static void logTestMetadata(String suiteName,
                                       String className,
                                       String methodName,
                                       String description,
                                       String module,
                                       String testType,
                                       String dataRow,
                                       String expected,
                                       String actual) {
        String[][] data = {
                {"Suite", emptyToDash(suiteName)},
                {"Class", emptyToDash(className)},
                {"Method", emptyToDash(methodName)},
                {"Module", emptyToDash(module)},
                {"Type", emptyToDash(testType)},
                {"Data Row", emptyToDash(dataRow)},
                {"Browser", detectBrowserName()},
                {"Headless", emptyToDash(FrameworkConstants.HEADLESS)},
                {"Description", emptyToDash(description)}
        };

        info(MarkupHelper.createTable(data));
        logExpectedActual(expected, actual);
    }

    public static void logExpectedActual(String expected, String actual) {
        String[][] data = {
                {"Expected", emptyToDash(expected)},
                {"Actual", emptyToDash(actual)}
        };
        info(MarkupHelper.createTable(data));
    }

    public static void assignReportTags(String project, String module, String testType) {
        List<String> tags = new ArrayList<>();
        if (project != null && !project.isEmpty()) {
            tags.add("Project:" + project);
        }
        if (module != null && !module.isEmpty()) {
            tags.add("Module:" + module);
        }
        if (testType != null && !testType.isEmpty()) {
            tags.add("Type:" + testType);
        }

        for (String tag : tags) {
            ExtentTestManager.getExtentTest().assignCategory(tag);
        }
    }

    public static void logTimingInsight(long durationMs, long slowThresholdMs) {
        if (durationMs >= slowThresholdMs) {
            logMessage(Status.WARNING, "Slow test detected: " + formatDuration(durationMs) + " (threshold: " + formatDuration(slowThresholdMs) + ")");
        } else {
            logMessage(Status.INFO, "Execution time: " + formatDuration(durationMs));
        }
    }

    public static void removeTest(String testCaseName) {
        extentReports.removeTest(testCaseName);
    }

    /**
     * Adds the screenshot.
     *
     * @param message the message
     */
    public static void addScreenShot(String message) {
        if (!canCaptureScreenshot()) {
            LogUtils.warn("Skip screenshot: WebDriver session is not active.");
            return;
        }
        String base64Image;
        try {
            base64Image = "data:image/png;base64," + ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            LogUtils.warn("Skip screenshot: unable to capture current browser window.");
            return;
        }

        ExtentTestManager.getExtentTest().log(Status.INFO, buildCompactScreenshotHtml(base64Image, message));

        //File Path from Screenshot of Java
        //ExtentTestManager.getExtentTest().log(Status.INFO, MediaEntityBuilder.createScreenCaptureFromPath(String.valueOf(CaptureHelpers.getScreenshotFile(message))).build());

    }

    /**
     * Adds the screenshot.
     *
     * @param status  the status
     * @param message the message
     */
    public static void addScreenShot(Status status, String message) {
        if (!canCaptureScreenshot()) {
            LogUtils.warn("Skip screenshot: WebDriver session is not active.");
            return;
        }
        String base64Image;
        try {
            base64Image = "data:image/png;base64," + ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            LogUtils.warn("Skip screenshot: unable to capture current browser window.");
            return;
        }

        ExtentTestManager.getExtentTest().log(status, buildCompactScreenshotHtml(base64Image, message));

        //File Path from Screenshot of Java
        //ExtentTestManager.getExtentTest().log(status, MediaEntityBuilder.createScreenCaptureFromPath(CaptureHelpers.getScreenshotAbsolutePath(message)).build());

    }

    synchronized public static void addAuthors(AuthorType[] authors) {
        if (authors == null) {
            ExtentTestManager.getExtentTest().assignAuthor("ANHTESTER");
        } else {
            for (AuthorType author : authors) {
                ExtentTestManager.getExtentTest().assignAuthor(author.toString());
            }
        }
    }

    // public static void addCategories(String[] categories) {
    synchronized public static void addCategories(CategoryType[] categories) {
        if (categories == null) {
            ExtentTestManager.getExtentTest().assignCategory("REGRESSION");
        } else {
            // for (String category : categories) {
            for (CategoryType category : categories) {
                ExtentTestManager.getExtentTest().assignCategory(category.toString());
            }
        }
    }

    synchronized public static void addDevices() {
        ExtentTestManager.getExtentTest().assignDevice(BrowserInfoUtils.getBrowserInfo());
//		ExtentReportManager.getExtentTest()
//				.assignDevice(BrowserIconUtils.getBrowserIcon() + " : " + BrowserInfoUtils.getBrowserInfo());
    }

    public static void logMessage(String message) {
        ExtentTestManager.getExtentTest().log(Status.INFO, message);
    }

    public static void logMessage(Status status, String message) {
        ExtentTestManager.getExtentTest().log(status, message);
    }

    public static void logMessage(Status status, Object message) {
        ExtentTestManager.getExtentTest().log(status, (Throwable) message);
    }

    public static String formatDuration(long durationMs) {
        long totalSeconds = Math.max(0, durationMs / 1000);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }

    public static void pass(String message) {
        //LogUtils.info("ExtentReportManager class: " + ExtentTestManager.getExtentTest());
        ExtentTestManager.getExtentTest().pass(message);
    }

    public static void pass(Markup message) {
        ExtentTestManager.getExtentTest().pass(message);
    }

    public static void fail(String message) {
        ExtentTestManager.getExtentTest().fail(message);
    }

    public static void fail(Object message) {
        ExtentTestManager.getExtentTest().fail((String) message);
    }

    public static void fail(Markup message) {
        ExtentTestManager.getExtentTest().fail(message);
    }

    public static void skip(String message) {
        ExtentTestManager.getExtentTest().skip(message);
    }

    public static void skip(Markup message) {
        ExtentTestManager.getExtentTest().skip(message);
    }

    public static void info(Markup message) {
        ExtentTestManager.getExtentTest().info(message);
    }

    public static void info(String message) {
        ExtentTestManager.getExtentTest().info(message);
    }

    public static void warning(String message) {
        ExtentTestManager.getExtentTest().log(Status.WARNING, message);
    }

    private static boolean canCaptureScreenshot() {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (!(driver instanceof TakesScreenshot)) {
                return false;
            }
            if (driver instanceof RemoteWebDriver remoteWebDriver) {
                return remoteWebDriver.getSessionId() != null;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String normalizeTestName(String testCaseName) {
        if (testCaseName == null) {
            return "-";
        }
        return testCaseName.trim();
    }

    private static String detectBrowserName() {
        String browser = FrameworkConstants.BROWSER;
        if (browser == null || browser.trim().isEmpty()) {
            return Browser.CHROME.name();
        }
        return browser.trim().toUpperCase();
    }

    private static String detectProjectName(String suiteName) {
        if (suiteName == null) {
            return "General";
        }
        String normalized = suiteName.toLowerCase();
        if (normalized.contains("cms")) {
            return "CMS";
        }
        if (normalized.contains("crm")) {
            return "CRM";
        }
        return "General";
    }

    private static String emptyToDash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value.trim();
    }

    private static String buildCompactScreenshotHtml(String base64Image, String title) {
        String safeTitle = emptyToDash(title);
        return "<div><strong>" + safeTitle + "</strong><br/>"
                + "<a href='" + base64Image + "' target='_blank'>"
                + "<img src='" + base64Image + "' style='max-width:220px;border:1px solid #ccc;border-radius:4px;' alt='" + safeTitle + "'/></a>"
                + "</div>";
    }

    private static String safeBrowserIcon() {
        try {
            String icon = IconUtils.getBrowserIcon();
            return icon == null ? "" : icon.trim();
        } catch (Exception ignored) {
            return "";
        }
    }

}
