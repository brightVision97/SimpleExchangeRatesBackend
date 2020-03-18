package com.rachev.foreignexchange.controllers;

import com.rachev.foreignexchange.services.ExchangeRatesHistoricalService;
import com.rachev.foreignexchange.utils.ControllersHelper;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exchangerates/historical")
public class ExchangeRatesHistoricalController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final ExchangeRatesHistoricalService exchangeRatesHistoricalService;
  private final ExchangeRatesHistoricalService.Async asyncExchangeRatesHistoricalService;


  @Autowired
  public ExchangeRatesHistoricalController(
      final ExchangeRatesHistoricalService exchangeRatesHistoricalService,
      final ExchangeRatesHistoricalService.Async asyncExchangeRatesHistoricalService) {
    this.exchangeRatesHistoricalService = exchangeRatesHistoricalService;
    this.asyncExchangeRatesHistoricalService = asyncExchangeRatesHistoricalService;
  }

  @ApiResponses({
      @ApiResponse(
          code = HttpServletResponse.SC_OK,
          message = "Latest exchange rates for the given base currency and date.",
          response = ResponseEntity.class),
      @ApiResponse(
          code = HttpServletResponse.SC_NOT_FOUND,
          message = "No json response was returned from the external api.",
          response = ResponseEntity.class),
      @ApiResponse(
          code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          message = "Internal server error",
          response = ResponseEntity.class)
  })
  @GetMapping("/forDate")
  public ResponseEntity<?> getRatesForDate(@RequestParam final String base,
      @RequestParam final String date) {
    
    if (!ControllersHelper.isDateParsable(date)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return exchangeRatesHistoricalService.getRatesForDate(base, date)
        .map(responseJson -> new ResponseEntity<>(responseJson, HttpStatus.OK))
        .onErrorReturn(ex -> {
          if (ex instanceof IllegalStateException) {
            logger.warn(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
          }
          return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        })
        .blockingGet(new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY));
  }

  @GetMapping("/forDateAsync")
  public ResponseEntity<?> getRatesForDateAsync(@RequestParam final String base,
      @RequestParam final String date) {

    if (!ControllersHelper.isDateParsable(date)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    return asyncExchangeRatesHistoricalService.getRatesForDate(base,date)
        .thenApply(ResponseEntity::ok)
        .exceptionally(ex -> ResponseEntity.status(ex instanceof IllegalStateException
            ? HttpStatus.NOT_FOUND
            : HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ex.getMessage()))
        .join();
  }

    @ApiResponses({
      @ApiResponse(
          code = HttpServletResponse.SC_OK,
          message = "Latest exchange rates for the given base currency interval of time.",
          response = ResponseEntity.class),
      @ApiResponse(
          code = HttpServletResponse.SC_NOT_FOUND,
          message = "No json response was returned from the external api.",
          response = ResponseEntity.class),
      @ApiResponse(
          code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          message = "Internal server error",
          response = ResponseEntity.class)
  })
  @GetMapping("/forPeriod")
  public ResponseEntity<?> getRatesForPeriod(@RequestParam final String base,
      @RequestParam final String start_at, @RequestParam final String end_at) {

    if (!ControllersHelper.isDateParsable(start_at) || !ControllersHelper.isDateParsable(end_at)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    return exchangeRatesHistoricalService.getRatesForPeriod(base, start_at, end_at)
        .map(responseJson -> new ResponseEntity<>(responseJson, HttpStatus.OK))
        .onErrorReturn(ex -> {
          if (ex instanceof IllegalStateException) {
            logger.warn(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
          }
          return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        })
        .blockingGet(new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY));
  }

  @GetMapping("/forPeriodAsync")
  public ResponseEntity<?> getRatesForPeriodAsync(@RequestParam final String base,
      @RequestParam final String start_at, @RequestParam final String end_at) {

    if (!ControllersHelper.isDateParsable(start_at) || !ControllersHelper.isDateParsable(end_at)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return asyncExchangeRatesHistoricalService.getRatesForPeriod(base,start_at,end_at)
        .thenApply(ResponseEntity::ok)
        .exceptionally(ex -> ResponseEntity.status(ex instanceof IllegalStateException
            ? HttpStatus.NOT_FOUND
            : HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ex.getMessage()))
        .join();
  }
}
