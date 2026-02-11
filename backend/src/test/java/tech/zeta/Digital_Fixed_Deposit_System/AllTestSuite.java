package tech.zeta.Digital_Fixed_Deposit_System;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import tech.zeta.Digital_Fixed_Deposit_System.config.PasswordConfigTest;
import tech.zeta.Digital_Fixed_Deposit_System.config.WebConfigTest;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProviderTest;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.JwtAuthenticationEntryPointTest;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.JwtAuthenticationFilterTest;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.SecurityConfigTest;
import tech.zeta.Digital_Fixed_Deposit_System.controller.admin.AdminFDControllerTest;
import tech.zeta.Digital_Fixed_Deposit_System.controller.auth.AuthControllerTest;
import tech.zeta.Digital_Fixed_Deposit_System.controller.fd.FixedDepositControllerTest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.AuthResponseTest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.LoginRequestTest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.RegisterRequestTest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.common.ApiResponseTest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FdDtoTest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.auth.RefreshTokenTest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDepositTest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.RoleTest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.UserTest;
import tech.zeta.Digital_Fixed_Deposit_System.exception.ExceptionTest;
import tech.zeta.Digital_Fixed_Deposit_System.exception.GlobalExceptionHandlerTest;
import tech.zeta.Digital_Fixed_Deposit_System.repository.RefreshTokenRepositoryTest;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepositoryTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.AuthServiceTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.AuthTokensTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.IRefreshTokenServiceTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.ITokenServiceTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.RefreshTokenServiceTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.auth.TokenServiceTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositMaturitySchedulerTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositServiceTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.InterestCalculationServiceTest;
import tech.zeta.Digital_Fixed_Deposit_System.util.CookieUtilTest;
import tech.zeta.Digital_Fixed_Deposit_System.util.DateUtilsTest;

@Suite
@SelectClasses({
        DigitalFixedDepositSystemApplicationTests.class,
        AdminFDControllerTest.class,
        FixedDepositControllerTest.class,
        FdDtoTest.class,
        FixedDepositTest.class,
        ExceptionTest.class,
        FixedDepositMaturitySchedulerTest.class,
        FixedDepositServiceTest.class,
        InterestCalculationServiceTest.class,
        DateUtilsTest.class,
        AuthControllerTest.class,
        AuthServiceTest.class,
        AuthTokensTest.class,
        TokenServiceTest.class,
        RefreshTokenServiceTest.class,
        ITokenServiceTest.class,
        IRefreshTokenServiceTest.class,
        ApiResponseTest.class,
        AuthResponseTest.class,
        LoginRequestTest.class,
        RegisterRequestTest.class,
        CookieUtilTest.class,
        RoleTest.class,
        UserTest.class,
        RefreshTokenTest.class,
        UserRepositoryTest.class,
        RefreshTokenRepositoryTest.class,
        GlobalExceptionHandlerTest.class,
        PasswordConfigTest.class,
        WebConfigTest.class,
        CurrentUserProviderTest.class,
        JwtAuthenticationEntryPointTest.class,
        JwtAuthenticationFilterTest.class,
        SecurityConfigTest.class
})
public class AllTestSuite {
}
