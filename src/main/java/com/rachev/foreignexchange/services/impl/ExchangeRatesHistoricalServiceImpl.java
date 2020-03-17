package com.rachev.foreignexchange.services.impl;

import static com.rachev.foreignexchange.utils.ExchangeRatesApiUrls.HISTORY_RATES_URL;
import static com.rachev.foreignexchange.utils.ExchangeRatesApiUrls.LATEST_RATES_URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rachev.foreignexchange.models.ExchangeRatesApiResponse;
import com.rachev.foreignexchange.models.ExchangeRatesForIntervalApiResponse;
import com.rachev.foreignexchange.services.ExchangeRatesHistoricalService;
import com.rachev.foreignexchange.utils.ControllersHelper;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.exceptions.Exceptions;
import io.reactivex.rxjava3.internal.operators.maybe.MaybeEmpty;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public final class ExchangeRatesHistoricalServiceImpl implements ExchangeRatesHistoricalService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Autowired
  public ExchangeRatesHistoricalServiceImpl(final RestTemplate restTemplate,
      final ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public Maybe<String> getRatesForDate(final String base, final String date) {

    return Maybe.fromCallable(() -> {

      final String finalUrl = UriComponentsBuilder.fromHttpUrl(HISTORY_RATES_URL)
          .queryParam("base", base)
          .queryParam("start_at", date)
          .queryParam("end_at", date)
          .build().toString();

      final String responseJson = restTemplate.getForObject(finalUrl, String.class);

      if (Optional.ofNullable(responseJson).orElse(StringUtils.EMPTY).trim().isEmpty()) {
        throw Exceptions.propagate(new IllegalStateException("No response from external api."));
      }
      final ExchangeRatesForIntervalApiResponse intervalWrapper =
          objectMapper.readValue(responseJson, ExchangeRatesForIntervalApiResponse.class);
      final ExchangeRatesApiResponse wrapper = new ExchangeRatesApiResponse();
      wrapper.setBase(base);
      wrapper.setDate(date);
      wrapper.setRates(intervalWrapper.getRates().get(date));

      return objectMapper.writeValueAsString(wrapper);
    }).onErrorResumeNext(ex -> {
      logger.error(ex.getMessage());
      return MaybeEmpty.error(ex);
    });
  }

  @Override
  public Maybe<String> getRatesForPeriod(final String base, final String start_at,
      final String end_at) {

    if (start_at.equals(end_at)) {
      return getRatesForDate(base, start_at);
    }

    return Maybe.fromCallable(() -> {

      final String finalUrl = UriComponentsBuilder.fromHttpUrl(HISTORY_RATES_URL)
          .queryParam("base", base)
          .queryParam("start_at", start_at)
          .queryParam("end_at", end_at)
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
