package com.gmail.dissa.vadim.superkid.exception;

import com.gmail.dissa.vadim.superkid.config.MailConfig;
import com.gmail.dissa.vadim.superkid.config.RootConfig;
import com.gmail.dissa.vadim.superkid.config.WebConfig;
import com.gmail.dissa.vadim.superkid.controller.HomeController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.ModelAndView;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration(classes = MailConfig.class),
        @ContextConfiguration(classes = RootConfig.class),
        @ContextConfiguration(classes = WebConfig.class)
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class SuperkidExceptionTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test(expected = SuperkidException.class)
    public void testBadRequestException() {
        HomeController homeController = (HomeController) applicationContext.getAutowireCapableBeanFactory().getBean("homeController");
        homeController.product("00000", new ModelAndView());
    }
}