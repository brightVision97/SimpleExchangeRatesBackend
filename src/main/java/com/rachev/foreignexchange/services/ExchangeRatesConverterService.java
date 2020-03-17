package com.rachev.foreignexchange.services;

import io.reactivex.rxjava3.core.Maybe;

public interface ExchangeRatesConverterService {
  
  Maybe<String> convert(final String from, final String to, final Double amount);
}
