package com.rahul.local.test.protobuf;

import com.rahul.local.test.protobuf.Training.Course;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ProtobufLocalClient {
  protected Logger logger = Logger.getLogger(ProtobufLocalClient.class.getName());

  @Autowired
  TestRestTemplate template;

  @Test
  public void testFindByNumber() {
    Course a = this.template.getForObject("/courses/{id}", Course.class, "1");
    logger.info("Course[\n" + a + "]");
    Assert.assertEquals(a.getId(), 1);
    Assert.assertEquals(a.getCourseName(), "REST with Spring");
    Assert.assertEquals(a.getStudentList().size(), 3);
  }
  @Test
  public void testFindByCorse2() {
    Course a = this.template.getForObject("/courses/{2}", Course.class, "2");
    logger.info("Course[\n" + a + "]");
    Assert.assertEquals(a.getId(), 2);
    Assert.assertEquals(a.getCourseName(), "Learn Spring Security");
  }

  @TestConfiguration
  static class Config {
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
      return new RestTemplateBuilder().additionalMessageConverters(new ProtobufHttpMessageConverter());
    }
  }

}
