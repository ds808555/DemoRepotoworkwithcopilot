package com.anhtester.listeners;

import com.anhtester.annotations.FrameworkAnnotation;
import com.anhtester.constants.FrameworkConstants;
import com.anhtester.driver.DriverManager;
import com.anhtester.enums.AuthorType;
import com.anhtester.enums.Browser;
import com.anhtester.enums.CategoryType;
import com.anhtester.helpers.*;
import com.anhtester.keywords.WebUI;
import com.anhtester.reports.AllureManager;
import com.anhtester.reports.ExtentReportManager;
import com.anhtester.reports.TelegramManager;
import com.anhtester.utils.BrowserInfoUtils;
import com.anhtester.utils.EmailSendUtils;
import com.anhtester.utils.LogUtils;
import com.anhtester.utils.ZipUtils;
import com.aventstack.extentreports.Status;
import com.github.automatedowl.tools.AllureEnvironmentWriter;
import com.google.common.collect.ImmutableMap;
import org.testng.*;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.stream.Collectors;

import static com.anhtester.constants.FrameworkConstants.*;

public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener {

    static int count_totalTCs;
    static int count_passedTCs;
    static int count_skippedTCs;
    static int count_failedTCs;
    static long suiteStartTimeMs;
    static long totalTestDurationMs;

    private static final long SLOW_TEST_THRESHOLD_MS = 20000;

    private ScreenRecorderHelpers screenRecorder;

    public TestListener() {
        try {
            screenRecorder = new ScreenRecorderHelpers();
        } catch (IOException | AWTException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getTestName(ITestResult result) {
        return result.getTestName() != null ? result.getTestName() : result.getMethod().getConstructorOrMethod().getName();
    }

    public String getTestDescription(ITestResult result) {
        return result.getMethod().getDescription() != null ? result.getMethod().getDescription() : getTestName(result);
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            ExtentReportManager.logMessage(Status.INFO, "Step Start: " + method.getTestMethod().getMethodName());
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            ExtentReportManager.logMessage(Status.INFO, "Step End: " + method.getTestMethod().getMethodName());
        }
    }

    @Override
    public void onStart(ISuite iSuite) {
        LogUtils.info("********** RUN STARTED **********");
        LogUtils.info("========= INSTALLING CONFIGURATION DATA =========");
        count_totalTCs = 0;
        count_passedTCs = 0;
        count_failedTCs = 0;
        count_skippedTCs = 0;
        totalTestDurationMs = 0;
        suiteStartTimeMs = System.currentTimeMillis();
//        try {
//            FileUtils.deleteDirectory(new File("target/allure-results"));
//            System.out.println("Deleted Directory target/allure-results");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        PropertiesHelpers.loadAllFiles();
        AllureManager.setAllureEnvironmentInformation();
        ExtentReportManager.initReports();
        LogUtils.info("========= INSTALLED CONFIGURATION DATA =========");
        LogUtils.info("=====> Starting Suite: " + iSuite.getName());
    }

    @Override
    public void onFinish(ISuite iSuite) {
        LogUtils.info("********** RUN FINISHED **********");
        LogUtils.info("=====> End Suite: " + iSuite.getName());
        long runDurationMs = Math.max(0, System.currentTimeMillis() - suiteStartTimeMs);
        double avgDurationMs = count_totalTCs == 0 ? 0 : (double) totalTestDurationMs / count_totalTCs;

        ExtentReportManager.createSuiteSummary(
            iSuite.getName(),
            count_totalTCs,
            count_passedTCs,
            count_failedTCs,
            count_skippedTCs,
            runDurationMs,
            avgDurationMs
        );

        //End Suite and execute Extents Report
        ExtentReportManager.flushReports();
        //Zip Folder reports
        ZipUtils.zipReportFolder();
        //Send notification to Telegram
        TelegramManager.sendReportPath();
        //Send mail
        EmailSendUtils.sendEmail(count_totalTCs, count_passedTCs, count_failedTCs, count_skippedTCs);

        //Write information in Allure Report
        AllureEnvironmentWriter.allureEnvironmentWriter(ImmutableMap.<String, String>builder().put("Target Execution", FrameworkConstants.TARGET).put("Global Timeout", String.valueOf(FrameworkConstants.WAIT_DEFAULT)).put("Page Load Timeout", String.valueOf(FrameworkConstants.WAIT_PAGE_LOADED)).put("Headless Mode", FrameworkConstants.HEADLESS).put("Local Browser", String.valueOf(Browser.CHROME)).put("Remote URL", FrameworkConstants.REMOTE_URL).put("Remote Port", FrameworkConstants.REMOTE_PORT).put("TCs Total", String.valueOf(count_totalTCs)).put("TCs Passed", String.valueOf(count_passedTCs)).put("TCs Skipped", String.valueOf(count_skippedTCs)).put("TCs Failed", String.valueOf(count_failedTCs)).build());

        //FileHelpers.copyFile("src/test/resources/config/allure/environment.xml", "target/allure-results/environment.xml");
        FileHelpers.copyFile("src/test/resources/config/allure/categories.json", "target/allure-results/categories.json");
        FileHelpers.copyFile("src/test/resources/config/allure/executor.json", "target/allure-results/executor.json");

//        try {
//            // Generate Allure report
//            generateAllureReport();
//            // Expose the report using ngrok
//            exposeReportWithNgrok();
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }

//    private void generateAllureReport() throws IOException, InterruptedException {
//        // Run the allure generate command
//        ProcessBuilder pb = new ProcessBuilder("allure.bat", "generate", "target/allure-results", "-o", "allure-report", "--clean");
//        pb.inheritIO(); // Outputs to console
//        Process process = pb.start();
//        process.waitFor();
//        System.out.println("Allure report generated successfully.");
//    }

//    private void exposeReportWithNgrok() throws IOException, InterruptedException {
//        // Step 1: Serve the allure-report folder with Python HTTP server (port 8000)
//        ProcessBuilder servePb = new ProcessBuilder("python", "-m", "http.server", "8000");
//        servePb.directory(new java.io.File(SystemHelpers.getCurrentDir() + "allure-report")); // Set working directory to allure-report
//        servePb.inheritIO();
//        Process serveProcess = servePb.start();
//
//        // Give the server a moment to start
//        Thread.sleep(5000);
//
//        // Step 2: Start ngrok to expose port 8000
//        ProcessBuilder ngrokPb = new ProcessBuilder("C:\\ngrok\\ngrok.exe", "http", "8000");
//        ngrokPb.inheritIO();
//        Process ngrokProcess = ngrokPb.start();
//
//        Thread.sleep(5000);
//
//        // Đọc và hiển thị đầu ra trong thread riêng
//        new Thread(() -> {
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(ngrokProcess.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
//                    if (line.contains("https://")) {
//                        String url = line.split("->")[0].trim().replace("Forwarding", "").trim();
//                        System.out.println("Public URL: " + url);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        // Đọc lỗi (nếu có)
//        new Thread(() -> {
//            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(ngrokProcess.getErrorStream()))) {
//                String line;
//                while ((line = errorReader.readLine()) != null) {
//                    System.err.println("ngrok Error: " + line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        Thread.sleep(5000);
//        System.out.println("ngrok is running. Check console for the public URL.");
//    }

    public AuthorType[] getAuthorType(ITestResult iTestResult) {
        if (iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class) == null) {
            return null;
        }
        AuthorType authorType[] = iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class).author();
        return authorType;
    }

    public CategoryType[] getCategoryType(ITestResult iTestResult) {
        if (iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class) == null) {
            return null;
        }
        CategoryType categoryType[] = iTestResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(FrameworkAnnotation.class).category();
        return categoryType;
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        LogUtils.info("Test case: " + getTestName(iTestResult) + " is starting...");
        count_totalTCs = count_totalTCs + 1;

        ExtentReportManager.createTest(iTestResult.getName());
        ExtentReportManager.addAuthors(getAuthorType(iTestResult));
        ExtentReportManager.addCategories(getCategoryType(iTestResult));
        ExtentReportManager.addDevices();
        ExtentReportManager.assignReportTags(
            inferProject(iTestResult),
            inferModule(iTestResult),
            inferTestType(iTestResult)
        );

        ExtentReportManager.logTestMetadata(
            iTestResult.getTestContext().getSuite().getName(),
            iTestResult.getTestClass().getName(),
            iTestResult.getMethod().getMethodName(),
            getTestDescription(iTestResult),
            inferModule(iTestResult),
            inferTestType(iTestResult),
            inferDataRow(iTestResult),
            inferExpectedValue(iTestResult),
            inferActualValue(iTestResult)
        );

        ExtentReportManager.info(BrowserInfoUtils.getOSInfo());

        if (VIDEO_RECORD.toLowerCase().trim().equals(YES)) {
            screenRecorder.startRecording(getTestName(iTestResult));
        }

    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        LogUtils.info("Test case: " + getTestName(iTestResult) + " is passed.");
        count_passedTCs = count_passedTCs + 1;
        long duration = Math.max(0, iTestResult.getEndMillis() - iTestResult.getStartMillis());
        totalTestDurationMs += duration;

        if (SCREENSHOT_PASSED_TCS.equals(YES)) {
            CaptureHelpers.captureScreenshot(DriverManager.getDriver(), getTestName(iTestResult));
            ExtentReportManager.addScreenShot(Status.PASS, getTestName(iTestResult));
        }

        ExtentReportManager.logTimingInsight(duration, SLOW_TEST_THRESHOLD_MS);
        ExtentReportManager.logMessage(Status.PASS, "Test case: " + getTestName(iTestResult) + " is passed.");

        if (VIDEO_RECORD.trim().toLowerCase().equals(YES)) {
            WebUI.sleep(2);
            screenRecorder.stopRecording(true);
        }
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        LogUtils.error("FAILED !! Test case " + getTestName(iTestResult) + " is failed.");
        LogUtils.error(iTestResult.getThrowable());

        count_failedTCs = count_failedTCs + 1;
        long duration = Math.max(0, iTestResult.getEndMillis() - iTestResult.getStartMillis());
        totalTestDurationMs += duration;

        if (SCREENSHOT_FAILED_TCS.equals(YES)) {
            CaptureHelpers.captureScreenshot(DriverManager.getDriver(), getTestName(iTestResult));
            ExtentReportManager.addScreenShot(Status.FAIL, getTestName(iTestResult));
        }

        ExtentReportManager.logTimingInsight(duration, SLOW_TEST_THRESHOLD_MS);
        ExtentReportManager.logExpectedActual(inferExpectedValue(iTestResult), inferActualValue(iTestResult));
        ExtentReportManager.logMessage(Status.FAIL, iTestResult.getThrowable().toString());

        if (VIDEO_RECORD.toLowerCase().trim().equals(YES)) {
            WebUI.sleep(2);
            screenRecorder.stopRecording(true);
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        LogUtils.warn("WARNING!! Test case: " + getTestName(iTestResult) + " is skipped.");
        count_skippedTCs = count_skippedTCs + 1;
        long duration = Math.max(0, iTestResult.getEndMillis() - iTestResult.getStartMillis());
        totalTestDurationMs += duration;

        if (SCREENSHOT_SKIPPED_TCS.equals(YES)) {
            CaptureHelpers.captureScreenshot(DriverManager.getDriver(), getTestName(iTestResult));
        }

        ExtentReportManager.logTimingInsight(duration, SLOW_TEST_THRESHOLD_MS);
        ExtentReportManager.logMessage(Status.SKIP, "Test case: " + getTestName(iTestResult) + " is skipped.");

        if (VIDEO_RECORD.toLowerCase().trim().equals(YES)) {
            screenRecorder.stopRecording(true);
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        ExtentReportManager.logMessage("Test failed but it is in defined success ratio: " + getTestName(iTestResult));
    }

    private String inferProject(ITestResult iTestResult) {
        String className = iTestResult.getTestClass().getName().toLowerCase();
        if (className.contains(".cms.")) {
            return "CMS";
        }
        if (className.contains(".crm.")) {
            return "CRM";
        }
        return "General";
    }

    private String inferModule(ITestResult iTestResult) {
        String className = iTestResult.getTestClass().getName();
        String[] parts = className.split("\\.");
        if (parts.length >= 2) {
            return parts[parts.length - 2];
        }
        return iTestResult.getTestClass().getRealClass().getSimpleName();
    }

    private String inferTestType(ITestResult iTestResult) {
        String methodName = iTestResult.getMethod().getMethodName().toLowerCase();
        if (methodName.contains("fail") || methodName.contains("invalid") || methodName.contains("negative")) {
            return "Negative";
        }
        if (methodName.contains("success") || methodName.contains("valid") || methodName.contains("positive")) {
            return "Positive";
        }
        return "General";
    }

    private String inferDataRow(ITestResult iTestResult) {
        Object[] parameters = iTestResult.getParameters();
        if (parameters == null || parameters.length == 0) {
            return "-";
        }

        Object first = parameters[0];
        if (first instanceof Hashtable<?, ?> table) {
            Object tc = table.get("TestCaseName");
            if (tc == null) {
                tc = table.get("testCaseName");
            }
            if (tc != null) {
                return String.valueOf(tc);
            }
        }

        return Arrays.stream(parameters)
                .map(this::safeParameter)
                .collect(Collectors.joining(" | "));
    }

    private String inferExpectedValue(ITestResult iTestResult) {
        Object[] parameters = iTestResult.getParameters();
        if (parameters != null && parameters.length > 0 && parameters[0] instanceof Hashtable<?, ?> table) {
            Object expected = table.get("expected");
            if (expected == null) {
                expected = table.get("Expected");
            }
            if (expected != null) {
                return String.valueOf(expected);
            }
        }
        return "Validated by assertions in test flow";
    }

    private String inferActualValue(ITestResult iTestResult) {
        if (iTestResult.getThrowable() != null) {
            return iTestResult.getThrowable().getMessage();
        }
        return "Matched expected assertions";
    }

    private String safeParameter(Object value) {
        if (value == null) {
            return "null";
        }
        String text = value.toString();
        if (text.length() > 100) {
            return text.substring(0, 100) + "...";
        }
        return text;
    }

}
