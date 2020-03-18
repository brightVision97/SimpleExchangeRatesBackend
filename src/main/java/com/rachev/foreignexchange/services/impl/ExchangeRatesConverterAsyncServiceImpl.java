package com.rachev.foreignexchange.services.impl;

import static com.rachev.foreignexchange.utils.ExchangeRatesApiUrls.LATEST_RATES_URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rachev.foreignexchange.models.ExchangeRatesApiResponse;
import com.rachev.foreignexchange.models.ExchangeRatesConvertedResponse;
import com.rachev.foreignexchange.services.ExchangeRatesConverterService;
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
public class ExchangeRatesConverterAsyncServiceImpl implements ExchangeRatesConverterService.Async {
  
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Autowired
  public ExchangeRatesConverterAsyncServiceImpl(final RestTemplate restTemplate,
      final ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }
  
  @Override
  @Async("asyncExecutor")
  public CompletableFuture<String> convert(String from, String to, Double amount) {
    
    try {
      final String finalurl = UriComponentsBuilder.fromHttpUrl(LATEST_RATES_URL)
          .queryParam("base", from)
          .build().toString();

      final String responseJson = restTemplate.getForObject(finalurl, String.class);

      if (Optional.ofNullable(responseJson).orElse(StringUtils.EMPTY).trim().isEmpty()) {
        throw new IllegalStateException("No response from external api.");
      }

      final ExchangeRatesApiResponse responseWrapperObject =
          objectMapper.readValue(responseJson, ExchangeRatesApiResponse.class);

      final ExchangeRatesConvertedResponse convertedResponse = new ExchangeRatesConvertedResponse();
      convertedResponse.setFrom(from);
      convertedResponse.setTo(to);
      convertedResponse.setAmount(amount);
      convertedResponse.setConvertedResult(amount * responseWrapperObject.getRates().get(to));

      return CompletableFuture.completedFuture(objectMapper.writeValueAsString(convertedResponse));
    } catch (IOException | IllegalStateException e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
