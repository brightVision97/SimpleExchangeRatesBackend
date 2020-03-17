package com.rachev.foreignexchange.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rachev.foreignexchange.models.ExchangeRatesApiResponse;
import com.rachev.foreignexchange.models.ExchangeRatesConvertedResponse;
import com.rachev.foreignexchange.services.impl.ExchangeRatesConverterServiceImpl;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class ExchangeRatesConverterServiceImplTests {

  private static final String DATE = "2012-03-17";
  private static final String BASE = "BGN";
  private static final String CONVERT_TO = "USD";
  private static final double INPUT_AMOUNT = 100.0;
  private static final double CONVERSION_RATE = 0.5;

  private ObjectMapper objectMapper;
  private RestTemplate restTemplate;
  private ExchangeRatesConverterService exchangeRatesConverterService;

  @BeforeEach
  public void setup() {

    objectMapper = new ObjectMapper();
    restTemplate = mock(RestTemplate.class);
    exchangeRatesConverterService = new ExchangeRatesConverterServiceImpl(restTemplate,
        objectMapper);
  }

  @Test
  public void test_Convert_BGNtoUSD_OK() throws JsonProcessingException {

    ExchangeRatesApiResponse apiResponse = new ExchangeRatesApiResponse();
    apiResponse.setBase(BASE);
    apiResponse.setDate(DATE);
    apiResponse.setRates(Collections.singletonMap(CONVERT_TO, CONVERSION_RATE));

    String responseJson = objectMapper.writeValueAsString(apiResponse);
    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenReturn(responseJson);

    ExchangeRatesConvertedResponse convertedResponse = new ExchangeRatesConvertedResponse();
    convertedResponse.setFrom(BASE);
    convertedResponse.setTo(CONVERT_TO);
    convertedResponse.setAmount(INPUT_AMOUNT);
    convertedResponse.setConvertedResult(INPUT_AMOUNT * CONVERSION_RATE); // 100 * 0.5 = 50.0

    String expectedJsonString = objectMapper.writeValueAsString(convertedResponse);

    exchangeRatesConverterService.convert(BASE, CONVERT_TO, INPUT_AMOUNT)
        .test()
        .assertComplete()
        .assertNoErrors()
        .assertValue(expectedJsonString);
  }

  @Test
  public void test_Convert_BGNtoUSD_ExternalApiEmptyResponse_Error() {

    ExchangeRatesApiResponse apiResponse = new ExchangeRatesApiResponse();
    apiResponse.setBase(BASE);
    apiResponse.setDate(DATE);
    apiResponse.setRates(Collections.singletonMap(CONVERT_TO, CONVERSION_RATE));

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenReturn(StringUtils.EMPTY);

    exchangeRatesConverterService.convert(BASE, CONVERT_TO, INPUT_AMOUNT)
        .test()
        .assertNotComplete()
        .assertError(IllegalStateException.class);
  }

  @Test
  public void test_Convert_BGNtoUSD_MappingException_Error() {

    ExchangeRatesApiResponse apiResponse = new ExchangeRatesApiResponse();
    apiResponse.setBase(String.valueOf(INPUT_AMOUNT)); // set inalid value on purpose
    apiResponse.setDate(DATE);
    apiResponse.setRates(Collections.singletonMap(CONVERT_TO, CONVERSION_RATE));

    when(restTemplate.getForObject(anyString(), eq(String.class)))
        .thenReturn(StringUtils.EMPTY);

    exchangeRatesConverterService.convert(BASE, CONVERT_TO, INPUT_AMOUNT)
        .test()
        .assertNotComplete()
        .assertError(Throwable.class);
  }
}
