package com.rachev.foreignexchange.services;

import io.reactivex.rxjava3.core.Maybe;
import java.util.concurrent.CompletableFuture;

public interface ExchangeRatesConverterService {

  Maybe<String> convert(final String from, final String to, final Double amount);

  interface Async {

    CompletableFuture<String> convert(final String from, final String to, final Double amount);
  }
}
