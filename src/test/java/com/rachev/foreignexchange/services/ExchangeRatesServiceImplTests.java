package com.rachev.foreignexchange.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rachev.foreignexchange.services.impl.ExchangeRatesServiceImpl;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class ExchangeRatesServiceImplTests {

  private static final String RETURN_VALUE = "value";
  private static final String BASE = "BGN";
  private static final String[] CONVERT_TO = {"USD"};

  private ObjectMapper objectMapper;
  private RestTemplate restTemplate;
  private ExchangeRatesService exchangeRatesService;

  @BeforeEach
  public void setup() {
    objectMapper = mock(ObjectMapper.class);
    restTemplate = mock(RestTemplate.class);
    exchangeRatesService = new ExchangeRatesServiceImpl(restTemplate, objectMapper);
  }

  @Test
  public void test_GetLatestRates_OK() throws IOException {

    when(objectMapper.readValue(anyString(), eq(String.class)))
        .thenReturn(RETURN_VALUE);

    when(objectMapper.writeValueAsString(any()))
        .thenReturn(RETURN_VALUE);

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenReturn(RETURN_VALUE);

    exchangeRatesService.getLatestRates(BASE)
        .test()
        .assertComplete()
        .assertNoErrors()
        .assertValue(RETURN_VALUE);
  }

  @Test
  public void test_GetLatestRates_NoExternalResponse_Error() throws IOException {
    when(objectMapper.readValue(anyString(), eq(String.class)))
        .thenReturn(RETURN_VALUE);

    when(objectMapper.writeValueAsString(any()))
        .thenReturn(RETURN_VALUE);

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenReturn(StringUtils.EMPTY);

    exchangeRatesService.getLatestRates(BASE)
        .test()
        .assertNotComplete()
        .assertError(IllegalStateException.class);
  }

  @Test
  public void test_GetLatestRates_RestTemplateException_Error() throws IOException {
    when(objectMapper.readValue(anyString(), eq(String.class)))
        .thenReturn(RETURN_VALUE);

    when(objectMapper.writeValueAsString(any()))
        .thenReturn(RETURN_VALUE);

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenCallRealMethod();

    exchangeRatesService.getLatestRates(BASE)
        .test()
        .assertNotComplete()
        .assertError(Throwable.class);
  }

  @Test
  public void test_GetParticularLatestDates_OK() throws IOException {
    when(objectMapper.readValue(anyString(), eq(String.class)))
        .thenReturn(RETURN_VALUE);

    when(objectMapper.writeValueAsString(any()))
        .thenReturn(RETURN_VALUE);

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenReturn(RETURN_VALUE);

    exchangeRatesService.getParticularLatestRates(BASE, CONVERT_TO)
        .test()
        .assertComplete()
        .assertNoErrors()
        .assertValue(RETURN_VALUE);
  }

  @Test
  public void test_GetParticularLatestDates_EmptyResponse_Error() throws IOException {
    when(objectMapper.readValue(anyString(), eq(String.class)))
        .thenReturn(RETURN_VALUE);

    when(objectMapper.writeValueAsString(any()))
        .thenReturn(RETURN_VALUE);

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenReturn(StringUtils.EMPTY);

    exchangeRatesService.getParticularLatestRates(BASE, CONVERT_TO)
        .test()
        .assertNotComplete()
        .assertError(IllegalStateException.class);
  }

  @Test
  public void test_GetParticularLatestDates_RestTemplateException_Error() throws IOException {
    when(objectMapper.readValue(anyString(), eq(String.class)))
        .thenReturn(RETURN_VALUE);

    when(objectMapper.writeValueAsString(any()))
        .thenReturn(RETURN_VALUE);

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenCallRealMethod();

    exchangeRatesService.getParticularLatestRates(BASE, CONVERT_TO)
        .test()
        .assertNotComplete()
        .assertError(Throwable.class);
  }
}
