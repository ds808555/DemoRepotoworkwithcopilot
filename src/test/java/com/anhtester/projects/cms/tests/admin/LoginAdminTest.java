package com.anhtester.projects.cms.tests.admin;

import com.anhtester.common.BaseTest;
import com.anhtester.constants.FrameworkConstants;
import com.anhtester.helpers.ExcelHelpers;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Epic("Regression Test CMS")
@Feature("Login Test")
public class LoginAdminTest extends BaseTest {

    @Test(priority = 6)
    public void TC_LoginSuccessAdminPage() {
        ExcelHelpers excel = new ExcelHelpers();
        excel.setExcelFile(FrameworkConstants.EXCEL_CMS_LOGIN, "Login");
        getLoginPageCMS().loginSuccessAdminPage(excel.getCellData(5, "email"), excel.getCellData(5, "password"));
    }
}
