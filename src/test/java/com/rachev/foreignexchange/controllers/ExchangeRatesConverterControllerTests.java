package com.rachev.foreignexchange.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rachev.foreignexchange.services.ExchangeRatesConverterService;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.internal.operators.maybe.MaybeEmpty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExchangeRatesConverterControllerTests {

  private ExchangeRatesConverterService exchangeRatesConverterService;
  private ExchangeRatesConverterController controller;

  @BeforeEach
  public void setup() {
    exchangeRatesConverterService = mock(ExchangeRatesConverterService.class);
    controller = new ExchangeRatesConverterController(exchangeRatesConverterService);
  }

  @Test
  public void test_Convert_EmptyParams_BadRequest() {
    ResponseEntity<?> responseEntity = controller.convert("", "", "");

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  public void test_Convert_UnparsableAmount_BadRequest() {
    ResponseEntity<?> responseEntity = controller.convert("BGN", "USD", "/");

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  public void test_Convert_OK() {

    when(exchangeRatesConverterService.convert(anyString(), anyString(), anyDouble()))
        .thenReturn(Maybe.just(""));

    ResponseEntity<?> responseEntity = controller.convert("BGN", "USD", "200.2");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  public void test_Convert_NotFound() {

    when(exchangeRatesConverterService.convert(anyString(), anyString(), anyDouble()))
        .thenReturn(MaybeEmpty.error(mock(IllegalStateException.class)));

    ResponseEntity<?> responseEntity = controller.convert("BGN", "USD", "200.2");

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  public void test_Convert_InternalError() {

    when(exchangeRatesConverterService.convert(anyString(), anyString(), anyDouble()))
        .thenReturn(MaybeEmpty.error(mock(Throwable.class)));

    ResponseEntity<?> responseEntity = controller.convert("BGN", "USD", "200.2");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }
}
