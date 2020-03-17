package com.rachev.foreignexchange.services.impl;

import static com.rachev.foreignexchange.utils.ExchangeRatesApiUrls.LATEST_RATES_URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rachev.foreignexchange.models.ExchangeRatesApiResponse;
import com.rachev.foreignexchange.models.ExchangeRatesConvertedResponse;
import com.rachev.foreignexchange.services.ExchangeRatesConverterService;
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
public final class ExchangeRatesConverterServiceImpl implements ExchangeRatesConverterService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Autowired
  public ExchangeRatesConverterServiceImpl(final RestTemplate restTemplate,
      final ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public Maybe<String> convert(final String from, final String to, final Double amount) {

    return Maybe.fromCallable(() -> {

      final String finalurl = UriComponentsBuilder.fromHttpUrl(LATEST_RATES_URL)
          .queryParam("base", from)
          .build().toString();

      final String responseJson = restTemplate.getForObject(finalurl, String.class);

      if (Optional.ofNullable(responseJson).orElse(StringUtils.EMPTY).trim().isEmpty()) {
        throw Exceptions.propagate(new IllegalStateException("No response from external api."));
      }

      final ExchangeRatesApiResponse responseWrapperObject =
          objectMapper.readValue(responseJson, ExchangeRatesApiResponse.class);

      final ExchangeRatesConvertedResponse convertedResponse = new ExchangeRatesConvertedResponse();
      convertedResponse.setFrom(from);
      convertedResponse.setTo(to);
      convertedResponse.setAmount(amount);
      convertedResponse.setConvertedResult(amount * responseWrapperObject.getRates().get(to));

      return objectMapper.writeValueAsString(convertedResponse);
    }).onErrorResumeNext(ex -> {
      logger.error(ex.getMessage());
      return MaybeEmpty.error(ex);
    });
  }
}
