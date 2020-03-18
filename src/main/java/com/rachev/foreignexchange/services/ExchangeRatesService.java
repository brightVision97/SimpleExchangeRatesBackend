package com.rachev.foreignexchange.services;

import io.reactivex.rxjava3.core.Maybe;
import java.util.concurrent.CompletableFuture;

public interface ExchangeRatesService {
  
  Maybe<String> getLatestRates(final String base);

  Maybe<String> getParticularLatestRates(final String base, final String... symbols);
  
  interface Async {
    
    CompletableFuture<String> getLatestRates(final String base);
    
    CompletableFuture<String> getParticularLatestRates(final String base, final String... symbols);
  }
}
