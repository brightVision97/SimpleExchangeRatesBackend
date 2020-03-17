package com.rachev.foreignexchange.services.impl;

import static com.rachev.foreignexchange.utils.ExchangeRatesApiUrls.LATEST_RATES_URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rachev.foreignexchange.models.ExchangeRatesApiResponse;
import com.rachev.foreignexchange.models.ExchangeRatesForIntervalApiResponse;
import com.rachev.foreignexchange.services.ExchangeRatesService;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.exceptions.Exceptions;
import io.reactivex.rxjava3.internal.operators.maybe.MaybeEmpty;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public final class ExchangeRatesServiceImpl implements ExchangeRatesService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Autowired
  public ExchangeRatesServiceImpl(final RestTemplate restTemplate,
      final ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public Maybe<String> getLatestRates(final String source) {

    return Maybe.fromCallable(() -> {

      final String finalUrl = UriComponentsBuilder.fromHttpUrl(LATEST_RATES_URL)
          .queryParam("base", source)
          .build().toString();

      final String responseJson = restTemplate.getForObject(finalUrl, String.class);

      if (Optional.ofNullable(responseJson).orElse(StringUtils.EMPTY).trim().isEmpty()) {
        throw Exceptions.propagate(new IllegalStateException("No response from external api."));
      }
      final ExchangeRatesApiResponse wrapper =
          objectMapper.readValue(responseJson, ExchangeRatesApiResponse.class);

      return objectMapper.writeValueAsString(wrapper);
    }).onErrorResumeNext(ex -> {
      logger.error(ex.getMessage());
      return MaybeEmpty.error(ex);
    });
  }

  @Override
  public Maybe<String> getParticularLatestRates(final String base, final String... symbols) {

    return Maybe.fromCallable(() -> {

      final String finalUrl = UriComponentsBuilder.fromHttpUrl(LATEST_RATES_URL)
          .queryParam("base", base)
          .queryParam("symbols", symbols)
          .build().toString();

      final String responseJson = restTemplate.getForObject(finalUrl, String.class);
      if (Optional.ofNullable(responseJson).orElse(StringUtils.EMPTY).trim().isEmpty()) {
        throw Exceptions.propagate(new IllegalStateException("No response from external api."));
      }

      final ExchangeRatesForIntervalApiResponse wrapper =
          objectMapper.readValue(responseJson, ExchangeRatesForIntervalApiResponse.class);

      return objectMapper.writeValueAsString(wrapper);
    }).onErrorResumeNext(ex -> {
      logger.error(ex.getMessage());
      return MaybeEmpty.error(ex);
    });
  }
}
