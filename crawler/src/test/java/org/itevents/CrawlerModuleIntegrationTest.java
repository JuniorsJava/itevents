package org.itevents;

import org.itevents.dao.EventDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 * Created by vaa25 on 10.03.2016.
 * http://stackoverflow.com/questions/19392684/import-all-dependencies-from-one-gradle-module-to-another
 * https://objectpartners.com/2012/01/26/building-with-gradle/
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class CrawlerModuleIntegrationTest {

    @Inject
    private EventDao eventDao;

    @Test
    public void shouldSeeEventDaoFromRestservice() {
        assertNotNull(eventDao);
    }
}
