package com.rachev.foreignexchange.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rachev.foreignexchange.models.ExchangeRatesApiResponse;
import com.rachev.foreignexchange.models.ExchangeRatesForIntervalApiResponse;
import com.rachev.foreignexchange.services.impl.ExchangeRatesHistoricalServiceImpl;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class ExchangeRatesHistoricalServiceImplTests {

  private static final String RETURN_VALUE = "value";
  private static final String DATE = "2012-01-12";
  private static final String BASE = "BGN";
  private static final String CONVERT_TO = "USD";
  private static final Double CONVERSION_RATE = 0.55;

  private ObjectMapper objectMapper;
  private RestTemplate restTemplate;
  private ExchangeRatesHistoricalService exchangeRatesHistoricalService;

  @BeforeEach
  public void setup() {
    
    objectMapper = new ObjectMapper();
    restTemplate = mock(RestTemplate.class);
    
    exchangeRatesHistoricalService = 
        new ExchangeRatesHistoricalServiceImpl(restTemplate, objectMapper);
  }

  @Test
  public void test_GetRatesForDate_OK() throws IOException {

    ExchangeRatesForIntervalApiResponse exchangeRatesForIntervalApiResponse =
        new ExchangeRatesForIntervalApiResponse();
    exchangeRatesForIntervalApiResponse.setBase(BASE);
    exchangeRatesForIntervalApiResponse.setStart_at(DATE);
    exchangeRatesForIntervalApiResponse.setEnd_at(DATE);
    exchangeRatesForIntervalApiResponse.setRates(Map.of(DATE, Map.of(CONVERT_TO, CONVERSION_RATE)));

    String forIntervalJson = objectMapper.writeValueAsString(exchangeRatesForIntervalApiResponse);

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenReturn(forIntervalJson);

    final ExchangeRatesApiResponse wrapper = new ExchangeRatesApiResponse();
    wrapper.setBase(BASE);
    wrapper.setDate(DATE);
    wrapper.setRates(exchangeRatesForIntervalApiResponse.getRates().get(DATE));
    String expectedJson = objectMapper.writeValueAsString(wrapper);

    exchangeRatesHistoricalService.getRatesForDate(BASE, DATE)
        .test()
        .assertComplete()
        .assertNoErrors()
        .assertValue(expectedJson);
  }

  @Test
  public void test_GetRatesForDate_EmptyExternalResponse_Error() {

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenReturn(StringUtils.EMPTY);

    exchangeRatesHistoricalService.getRatesForDate(BASE, DATE)
        .test()
        .assertNotComplete()
        .assertError(IllegalStateException.class);
  }

  @Test
  public void test_GetRatesForDate_JsonMappingException_Error() throws IOException {

    objectMapper = spy(objectMapper);
    doCallRealMethod().when(objectMapper).readValue(anyString(), eq(String.class));

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenReturn(RETURN_VALUE);

    exchangeRatesHistoricalService.getRatesForDate(BASE, DATE)
        .test()
        .assertNotComplete()
        .assertError(Throwable.class);
  }
}
