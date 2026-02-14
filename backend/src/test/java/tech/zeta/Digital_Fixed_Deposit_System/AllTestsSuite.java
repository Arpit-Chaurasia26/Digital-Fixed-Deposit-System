package tech.zeta.Digital_Fixed_Deposit_System;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Digital Fixed Deposit System - Complete Test Suite (500+ Tests)")
@SelectPackages({
    "tech.zeta.Digital_Fixed_Deposit_System.config",
    "tech.zeta.Digital_Fixed_Deposit_System.controller",
    "tech.zeta.Digital_Fixed_Deposit_System.service",
    "tech.zeta.Digital_Fixed_Deposit_System.repository",
    "tech.zeta.Digital_Fixed_Deposit_System.entity",
    "tech.zeta.Digital_Fixed_Deposit_System.dto",
    "tech.zeta.Digital_Fixed_Deposit_System.exception",
    "tech.zeta.Digital_Fixed_Deposit_System.mapper",
    "tech.zeta.Digital_Fixed_Deposit_System.util"
})
@IncludeClassNamePatterns({".*Test", ".*Tests", ".*IT", ".*Suite", ".*Spec"})
public class AllTestsSuite {
    static {
        System.setProperty("spring.profiles.active", "test");
    }
}
