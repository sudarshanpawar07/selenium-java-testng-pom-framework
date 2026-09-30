package gettingstarted;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestNGTest {
    @BeforeMethod
    public void beforeMethod() {
        System.out.println("@BeforeMethod");
    }

    @Test
    public void test() {
        System.out.println("test");

    }
    @Test
    public void testing() {
        System.out.println("testing");
    }
    @AfterMethod
    public void afterMethod() {
        System.out.println("@AfterMethod");
    }
}
