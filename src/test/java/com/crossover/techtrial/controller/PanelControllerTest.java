package com.crossover.techtrial.controller;

import com.crossover.techtrial.repository.HourlyElectricityRepository;
import com.crossover.techtrial.repository.PanelRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * PanelControllerTest class will test all APIs in PanelController.java.
 * @author Crossover
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:applicationTest.properties")
public class PanelControllerTest {
  
  MockMvc mockMvc;
  
  @Mock
  private PanelController panelController;
  
  @Autowired
  private TestRestTemplate template;

  @Autowired
  HourlyElectricityRepository hourlyElectricityRepository;

  @Autowired
  PanelRepository panelRepository;

  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(panelController).build();
  }

  @After
  public void destroy() {
    clearRepositories();
  }

  @Test
  public void testUniquePanelShouldBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
  }

  @Test
  public void testTwoPanelsShouldBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createPanel("232324", 202);
  }

  @Test
  public void testPanelWithSameSerialShouldNotBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    ResponseEntity<Object> response = createPanel("232323", 400);
    checkWasException(response);
  }

  @Test
  public void testHourlyElectricityWithNormalParamsShouldBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    ResponseEntity<Object> response = createHourlyElectricity("232323","500", "2018-11-23T10:00:00");
    assertEquals(200, response.getStatusCode().value());
    assertEquals(500, ((Map<String,String>)response.getBody()).get("generatedElectricity"));
    assertEquals("2018-11-23T10:00:00", ((Map<String,String>)response.getBody()).get("readingAt"));
  }

  @Test
  public void testHourlyElectricityWithOutPanelShouldNotBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    ResponseEntity<Object> response = createHourlyElectricity("111111","500", "2018-11-23T10:00:00");
    assertEquals(400, response.getStatusCode().value());
    checkWasException(response);
  }

  @Test
  public void testHourlyElectricityWithCorruptedTimeShouldNotBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    ResponseEntity<Object> response = createHourlyElectricity("111111","500", "2018-11-23T10:11:11");
    assertEquals(400, response.getStatusCode().value());
    checkWasException(response);
  }

  @Test
  public void testHourlyElectricityWithOutTimeShouldNotBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    ResponseEntity<Object> response = createHourlyElectricity("232323","500", null);
    assertEquals(400, response.getStatusCode().value());
    checkWasException(response);
  }

  @Test
  public void testHourlyElectricityWithOutGeneratedElShouldNotBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    ResponseEntity<Object> response = createHourlyElectricity("232323",null, "2018-11-23T10:00:00");
    assertEquals(400, response.getStatusCode().value());
    checkWasException(response);
  }

  @Test
  public void testHourlyElectricityWithSameTimeShouldNotBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    ResponseEntity<Object> response = createHourlyElectricity("232323","500", "2018-11-23T10:00:00");
    assertEquals(200, response.getStatusCode().value());
    response = createHourlyElectricity("232323","100", "2018-11-23T10:00:00");
    assertEquals(400, response.getStatusCode().value());
    checkWasException(response);
  }

  @Test
  public void testTwoHourlyElectricityWithNormalParamsShouldBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    ResponseEntity<Object> response = createHourlyElectricity("232323","500", "2018-11-23T10:00:00");
    assertEquals(200, response.getStatusCode().value());
    assertEquals(500, ((Map<String,String>)response.getBody()).get("generatedElectricity"));
    assertEquals("2018-11-23T10:00:00", ((Map<String,String>)response.getBody()).get("readingAt"));
    response = createHourlyElectricity("232323","500", "2018-11-23T11:00:00");
    assertEquals(200, response.getStatusCode().value());
    assertEquals(500, ((Map<String,String>)response.getBody()).get("generatedElectricity"));
    assertEquals("2018-11-23T11:00:00", ((Map<String,String>)response.getBody()).get("readingAt"));
  }

  @Test
  public void testTwoHourlyElectricityWithSameTimeForTwoPanelsShouldBeRegistered() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    ResponseEntity<Object> response = createHourlyElectricity("232323","500", "2018-11-23T10:00:00");
    assertEquals(200, response.getStatusCode().value());
    assertEquals(500, ((Map<String,String>)response.getBody()).get("generatedElectricity"));
    assertEquals("2018-11-23T10:00:00", ((Map<String,String>)response.getBody()).get("readingAt"));
    createPanel("232324", 202);
    response = createHourlyElectricity("232324","500", "2018-11-23T10:00:00");
    assertEquals(200, response.getStatusCode().value());
    assertEquals(500, ((Map<String,String>)response.getBody()).get("generatedElectricity"));
    assertEquals("2018-11-23T10:00:00", ((Map<String,String>)response.getBody()).get("readingAt"));
  }

  @Test
  public void testHourlyElectricityShouldBeGetted() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createHourlyElectricity("232323", "500", "2018-11-23T10:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/hourly", Object.class);

    List<Map<String, Object> > content = ((Map<String,List<Map<String, Object> > >)response.getBody()).get("content");
    assertEquals(1, content.size());
    assertEquals(500, content.get(0).get("generatedElectricity"));
    assertEquals("2018-11-23T10:00:00", content.get(0).get("readingAt"));
  }

  @Test
  public void testTwoHourlyElectricityShouldBeGetted() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createHourlyElectricity("232323", "400", "2018-11-23T11:00:00");
    createHourlyElectricity("232323", "500", "2018-11-23T10:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/hourly", Object.class);

    List<Map<String, Object> > content = ((Map<String,List<Map<String, Object> > >)response.getBody()).get("content");
    assertEquals(2, content.size());
    assertEquals(400, content.get(0).get("generatedElectricity"));
    assertEquals("2018-11-23T11:00:00", content.get(0).get("readingAt"));
    assertEquals(500, content.get(1).get("generatedElectricity"));
    assertEquals("2018-11-23T10:00:00", content.get(1).get("readingAt"));
  }

  @Test
  public void testTwoHourlyElectricityShouldBeGettedInRightOrder() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createHourlyElectricity("232323", "500", "2018-11-23T10:00:00");
    createHourlyElectricity("232323", "400", "2018-11-23T11:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/hourly", Object.class);

    List<Map<String, Object> > content = ((Map<String,List<Map<String, Object> > >)response.getBody()).get("content");
    assertEquals(2, content.size());
    assertEquals(400, content.get(0).get("generatedElectricity"));
    assertEquals("2018-11-23T11:00:00", content.get(0).get("readingAt"));
    assertEquals(500, content.get(1).get("generatedElectricity"));
    assertEquals("2018-11-23T10:00:00", content.get(1).get("readingAt"));
  }

  @Test
  public void testHourlyElectricityShouldBeGettedForOnlyNeedPanel() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createHourlyElectricity("232323", "500", "2018-11-23T10:00:00");
    createPanel("232324", 202);
    createHourlyElectricity("232324", "400", "2018-11-23T11:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/hourly", Object.class);

    List<Map<String, Object> > content = ((Map<String,List<Map<String, Object> > >)response.getBody()).get("content");
    assertEquals(1, content.size());
    assertEquals(500, content.get(0).get("generatedElectricity"));
    assertEquals("2018-11-23T10:00:00", content.get(0).get("readingAt"));
  }

  @Test
  public void testHourlyElectricityShouldNotBeGettedForEmptyPanel() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createPanel("232324", 202);
    createHourlyElectricity("232324", "400", "2018-11-23T11:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/hourly", Object.class);

    List<Map<String, Object> > content = ((Map<String,List<Map<String, Object> > >)response.getBody()).get("content");
    assertEquals(0, content.size());
  }

  @Test
  public void testHourlyElectricityShouldReturnEmptyPageIfNotHavingPanel() throws Exception {
    clearRepositories();
    createPanel("232323", 202);

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232324/hourly", Object.class);
    assertEquals(404, response.getStatusCode().value());
  }

  @Test
  public void testDailyElectricityShouldBeCounted() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createHourlyElectricity("232323", "500", "2018-11-23T10:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/daily", Object.class);

    List<Map<String, Object> > content = (List<Map<String, Object> >)response.getBody();
    assertEquals(1, content.size());
    assertEquals("2018-11-23", content.get(0).get("date"));
    assertEquals(500, content.get(0).get("sum"));
    assertEquals(500.0, content.get(0).get("average"));
    assertEquals(500, content.get(0).get("min"));
    assertEquals(500, content.get(0).get("max"));
  }

  @Test
  public void testTwoHourlyElectricityForOneDailyShouldBeCounted() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createHourlyElectricity("232323", "500", "2018-11-23T10:00:00");
    createHourlyElectricity("232323", "400", "2018-11-23T11:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/daily", Object.class);

    List<Map<String, Object> > content = (List<Map<String, Object> >)response.getBody();
    assertEquals(1, content.size());
    assertEquals("2018-11-23", content.get(0).get("date"));
    assertEquals(900, content.get(0).get("sum"));
    assertEquals(450.0, content.get(0).get("average"));
    assertEquals(400, content.get(0).get("min"));
    assertEquals(500, content.get(0).get("max"));
  }

  @Test
  public void testTwoDailyElectricityShouldBeCounted() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createHourlyElectricity("232323", "500", "2018-11-23T10:00:00");
    createHourlyElectricity("232323", "400", "2018-11-24T10:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/daily", Object.class);

    List<Map<String, Object> > content = (List<Map<String, Object> >)response.getBody();
    assertEquals(2, content.size());
    assertEquals("2018-11-24", content.get(0).get("date"));
    assertEquals(400, content.get(0).get("sum"));
    assertEquals(400.0, content.get(0).get("average"));
    assertEquals(400, content.get(0).get("min"));
    assertEquals(400, content.get(0).get("max"));
    assertEquals("2018-11-23", content.get(1).get("date"));
    assertEquals(500, content.get(1).get("sum"));
    assertEquals(500.0, content.get(1).get("average"));
    assertEquals(500, content.get(1).get("min"));
    assertEquals(500, content.get(1).get("max"));
  }

  @Test
  public void testTodayDailyElectricityShouldNotCounted() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createHourlyElectricity("232323", "500", LocalDate.now().toString()+"T10:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/daily", Object.class);

    List<Map<String, Object> > content = (List<Map<String, Object> >)response.getBody();
    assertEquals(200, response.getStatusCode().value());
    assertEquals(0, ((List<Object>)response.getBody()).size());
  }


  @Test
  public void testDailyElectricityShouldBeGettedForOnlyNeedPanel() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createHourlyElectricity("232323", "500", "2018-11-23T10:00:00");
    createPanel("232324", 202);
    createHourlyElectricity("232324", "400", "2018-11-23T11:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/daily", Object.class);

    List<Map<String, Object> > content = (List<Map<String, Object> >)response.getBody();
    assertEquals(1, content.size());
    assertEquals("2018-11-23", content.get(0).get("date"));
    assertEquals(500, content.get(0).get("sum"));
    assertEquals(500.0, content.get(0).get("average"));
    assertEquals(500, content.get(0).get("min"));
    assertEquals(500, content.get(0).get("max"));
  }

  @Test
  public void testDailyElectricityShouldNotBeGettedForEmptyPanel() throws Exception {
    clearRepositories();
    createPanel("232323", 202);
    createPanel("232324", 202);
    createHourlyElectricity("232324", "400", "2018-11-23T11:00:00");

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232323/daily", Object.class);
    assertEquals(200, response.getStatusCode().value());
    assertEquals(0, ((List<Object>)response.getBody()).size());
  }

  @Test
  public void testDailyElectricityShouldReturnEmptyPageIfNotHavingPanel() throws Exception {
    clearRepositories();
    createPanel("232323", 202);

    ResponseEntity<Object> response = template.getForEntity("/api/panels/232324/daily", Object.class);
    assertEquals(404, response.getStatusCode().value());
  }

  private void clearRepositories() {
    hourlyElectricityRepository.deleteAll();
    panelRepository.deleteAll();
  }

  private void checkWasException(ResponseEntity<Object> response) {
    assertEquals("Unable to process this request.", ((Map<String,String>)response.getBody()).get("message"));
  }

  private ResponseEntity<Object> createPanel(String serial, int needStatusCode) {
    HttpEntity<Object> panel = getHttpEntity(
            "{\"serial\": \"" + serial + "\", \"longitude\": \"54.123232\","
                    + " \"latitude\": \"54.123232\",\"brand\":\"tesla\" }");
    ResponseEntity<Object> response = template.postForEntity("/api/register", panel, Object.class);
    assertEquals(needStatusCode, response.getStatusCode().value());
    return response;
  }

  private ResponseEntity<Object> createHourlyElectricity(String panelId, String generatedElectricity, String readingAt) {
    HttpEntity<Object> hourlyEl = getHttpEntity(
            ((null != generatedElectricity) ? "{\"generatedElectricity\": \"" + generatedElectricity + "\", " : "") +
                  ((null != generatedElectricity) ? "\"readingAt\": \"" + readingAt + "\"}" : ""));
    return template.postForEntity("/api/panels/" + panelId + "/hourly", hourlyEl, Object.class);
  }

  private HttpEntity<Object> getHttpEntity(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<Object>(body, headers);
  }
}
