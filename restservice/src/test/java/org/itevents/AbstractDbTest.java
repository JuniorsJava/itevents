package org.itevents;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml",
                        "classpath:dbUnitDatabaseConnection.xml"})
@TestPropertySource("classpath:test-local.properties")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, TransactionDbUnitTestExecutionListener.class})
public abstract class AbstractDbTest {
    protected final static String PATH = "file:src/test/resources/dbunit/";
    protected final static int ABSENT_ID = 0;
    protected final static int ID_1 = -1;
    protected final static Date ABSENT_DATE = new Date(1);

}
