package tech.zeta.Digital_Fixed_Deposit_System;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import tech.zeta.Digital_Fixed_Deposit_System.controller.admin.AdminFDControllerTest;
import tech.zeta.Digital_Fixed_Deposit_System.controller.fd.FixedDepositControllerTest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.fd.FdDtoTest;
import tech.zeta.Digital_Fixed_Deposit_System.entity.fd.FixedDepositTest;
import tech.zeta.Digital_Fixed_Deposit_System.exception.ExceptionTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositMaturitySchedulerTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.FixedDepositServiceTest;
import tech.zeta.Digital_Fixed_Deposit_System.service.fd.InterestCalculationServiceTest;
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
        DateUtilsTest.class
})
public class AllTestSuite {
}
