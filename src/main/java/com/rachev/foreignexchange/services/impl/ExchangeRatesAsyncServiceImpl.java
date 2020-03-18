package com.rachev.foreignexchange.services.impl;

import static com.rachev.foreignexchange.utils.ExchangeRatesApiUrls.LATEST_RATES_URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rachev.foreignexchange.models.ExchangeRatesApiResponse;
import com.rachev.foreignexchange.models.ExchangeRatesForIntervalApiResponse;
import com.rachev.foreignexchange.services.ExchangeRatesService;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ExchangeRatesAsyncServiceImpl implements ExchangeRatesService.Async {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  
  @Autowired
  public ExchangeRatesAsyncServiceImpl(final RestTemplate restTemplate, 
      final ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }
  
  @Override
  @Async("asyncExecutor")
  public CompletableFuture<String> getLatestRates(String base) {
   
    try {
      final String finalUrl = UriComponentsBuilder.fromHttpUrl(LATEST_RATES_URL)
          .queryParam("base", base)
          .build().toString();

      final String responseJson = restTemplate.getForObject(finalUrl, String.class);

      if (Optional.ofNullable(responseJson).orElse(StringUtils.EMPTY).trim().isEmpty()) {
        throw new IllegalStateException("No response from external api.");
      }

      final ExchangeRatesApiResponse wrapper =
          objectMapper.readValue(responseJson, ExchangeRatesApiResponse.class);

      return CompletableFuture.completedFuture(objectMapper.writeValueAsString(wrapper));
    } catch (IOException | IllegalStateException e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Override
  @Async("asyncExecutor")
  public CompletableFuture<String> getParticularLatestRates(String base, String... symbols) {
   
    try {
      final String finalUrl = UriComponentsBuilder.fromHttpUrl(LATEST_RATES_URL)
          .queryParam("base", base)
          .queryParam("symbols", symbols)
          .build().toString();

      final String responseJson = restTemplate.getForObject(finalUrl, String.class);
      if (Optional.ofNullable(responseJson).orElse(StringUtils.EMPTY).trim().isEmpty()) {
        throw new IllegalStateException("No response from external api.");
      }

      final ExchangeRatesForIntervalApiResponse wrapper =
          objectMapper.readValue(responseJson, ExchangeRatesForIntervalApiResponse.class);
      
      return CompletableFuture.completedFuture(objectMapper.writeValueAsString(wrapper));
    } catch (IOException | IllegalStateException e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
