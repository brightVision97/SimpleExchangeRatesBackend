package com.rachev.foreignexchange.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rachev.foreignexchange.services.ExchangeRatesService;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.internal.operators.maybe.MaybeEmpty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExchangeRatesControllerTests {

  private ExchangeRatesService exchangeRatesService;
  private ExchangeRatesController controller;

  @BeforeEach
  public void setup() {
    exchangeRatesService = mock(ExchangeRatesService.class);
    controller = new ExchangeRatesController(exchangeRatesService);
  }

  @Test
  public void test_GetLatestRates_OK() {
    when(exchangeRatesService.getLatestRates(anyString()))
        .thenReturn(Maybe.just(""));

    ResponseEntity<?> responseEntity = controller.getLatestRates("BGN");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetLatestRates_NotFound() {
    when(exchangeRatesService.getLatestRates(anyString()))
        .thenReturn(MaybeEmpty.error(mock(IllegalStateException.class)));

    ResponseEntity<?> responseEntity = controller.getLatestRates("BGN");

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetLatestRates_InternalError() {
    when(exchangeRatesService.getLatestRates(anyString()))
        .thenReturn(MaybeEmpty.error(mock(Throwable.class)));

    ResponseEntity<?> responseEntity = controller.getLatestRates("BGN");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetLatestRatesForSymbols_OK() {
    when(exchangeRatesService.getParticularLatestRates(anyString(), any()))
        .thenReturn(Maybe.just(""));

    ResponseEntity<?> responseEntity = controller.getLatestRates("BGN", "CAD");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetLatestRatesForSymbols_NotFound() {
    when(exchangeRatesService.getParticularLatestRates(anyString(), any()))
        .thenReturn(MaybeEmpty.error(mock(IllegalStateException.class)));

    ResponseEntity<?> responseEntity = controller.getLatestRates("BGN", "CAD, USD");

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetLatestRatesForSymbols_InternalError() {
    when(exchangeRatesService.getParticularLatestRates(anyString(), any()))
        .thenReturn(MaybeEmpty.error(mock(Throwable.class)));

    ResponseEntity<?> responseEntity = controller.getLatestRates("BGN", "USD");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetLatestRatesForSymbols_NullSymbols_InternalError() {
    when(exchangeRatesService.getParticularLatestRates(anyString(), eq(null)))
        .thenReturn(MaybeEmpty.error(mock(Throwable.class)));

    ResponseEntity<?> responseEntity = controller.getLatestRates("BGN", null);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }
}
