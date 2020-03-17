package com.rachev.foreignexchange.services;

import io.reactivex.rxjava3.core.Maybe;

public interface ExchangeRatesService {
  
  Maybe<String> getLatestRates(final String base);

  Maybe<String> getParticularLatestRates(final String base, final String... symbols);
}
