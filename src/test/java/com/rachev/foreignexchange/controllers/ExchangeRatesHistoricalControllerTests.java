package com.rachev.foreignexchange.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rachev.foreignexchange.services.ExchangeRatesHistoricalService;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.internal.operators.maybe.MaybeEmpty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExchangeRatesHistoricalControllerTests {

  private ExchangeRatesHistoricalService exchangeRatesHistoricalService;
  private ExchangeRatesHistoricalController controller;

  @BeforeEach
  public void setup() {
    exchangeRatesHistoricalService = mock(ExchangeRatesHistoricalService.class);
    controller = new ExchangeRatesHistoricalController(exchangeRatesHistoricalService);
  }

  @Test
  public void test_GetRatesForDate_UnparasbleDate_BadRequest() {
    ResponseEntity<?> responseEntity = controller.getRatesForDate("BGN", "2012");

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetRatesForDate_OK() {
    when(exchangeRatesHistoricalService.getRatesForDate(anyString(), anyString()))
        .thenReturn(Maybe.just(""));

    ResponseEntity<?> responseEntity = controller.getRatesForDate("BGN", "2012-01-12");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetRatesForDate_NotFound() {
    when(exchangeRatesHistoricalService.getRatesForDate(anyString(), anyString()))
        .thenReturn(MaybeEmpty.error(mock(IllegalStateException.class)));

    ResponseEntity<?> responseEntity = controller.getRatesForDate("BGN", "2012-01-12");

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetRatesForDate_InternalError() {
    when(exchangeRatesHistoricalService.getRatesForDate(anyString(), anyString()))
        .thenReturn(MaybeEmpty.error(mock(Throwable.class)));

    ResponseEntity<?> responseEntity = controller.getRatesForDate("BGN", "2012-01-12");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetRatesForPeriod_OneOrMoreUnparsableDates_BadRequest() {
    ResponseEntity<?> responseEntity = controller.getRatesForPeriod("BGN", "2012", "2012-01-12");

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetRatesForPeriod_OK() {
    when(exchangeRatesHistoricalService.getRatesForPeriod(anyString(), anyString(), anyString()))
        .thenReturn(Maybe.just(""));

    ResponseEntity<?> responseEntity = controller
        .getRatesForPeriod("BGN", "2012-01-12", "2012-01-15");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetRatesForPeriod_NotFound() {
    when(exchangeRatesHistoricalService.getRatesForPeriod(anyString(), anyString(), anyString()))
        .thenReturn(MaybeEmpty.error(mock(IllegalStateException.class)));

    ResponseEntity<?> responseEntity = controller
        .getRatesForPeriod("BGN", "2012-01-12", "2012-01-15");

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  public void test_GetRatesForPeriod_InternalError() {
    when(exchangeRatesHistoricalService.getRatesForPeriod(anyString(), anyString(), anyString()))
        .thenReturn(MaybeEmpty.error(mock(Throwable.class)));

    ResponseEntity<?> responseEntity = controller
        .getRatesForPeriod("BGN", "2012-01-12", "2012-01-15");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }
}
