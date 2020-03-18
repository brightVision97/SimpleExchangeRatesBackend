package com.rachev.foreignexchange.controllers;

import com.rachev.foreignexchange.services.ExchangeRatesService;
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
@RequestMapping("/exchangerates")
public class ExchangeRatesController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final ExchangeRatesService exchangeRatesService;
  private final ExchangeRatesService.Async asyncExchangeRatesService;

  @Autowired
  public ExchangeRatesController(final ExchangeRatesService exchangeRatesService, 
      final ExchangeRatesService.Async asyncExchangeRatesService) {
    this.exchangeRatesService = exchangeRatesService;
    this.asyncExchangeRatesService =  asyncExchangeRatesService;
  }

  @ApiResponses({
      @ApiResponse(
          code = HttpServletResponse.SC_OK,
          message = "Latest exchange rates for the given base currency.",
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
  @GetMapping("/latest")
  public ResponseEntity<?> getLatestRates(@RequestParam final String base) {

    return exchangeRatesService.getLatestRates(base)
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
  
  @GetMapping("/latestAsync")
  public ResponseEntity<?> getLatestRatesAsync(@RequestParam final String base) {
    
    return asyncExchangeRatesService.getLatestRates(base)
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
          message = "Latest exchange rates for particular country codes.",
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
  @GetMapping("/latestFor")
  public ResponseEntity<?> getLatestRates(@RequestParam final String base,
      @RequestParam final String... symbols) {

    return exchangeRatesService.getParticularLatestRates(base, symbols)
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
  
  @GetMapping("/latestForAsync")
  public ResponseEntity<?> getLatestRatesAsync(@RequestParam final String base,
      @RequestParam final String... symbols) {
    
      return asyncExchangeRatesService.getParticularLatestRates(base, symbols)
          .thenApply(ResponseEntity::ok)
          .exceptionally(ex -> ResponseEntity.status(ex instanceof IllegalStateException
              ? HttpStatus.NOT_FOUND 
              : HttpStatus.INTERNAL_SERVER_ERROR)
              .body(ex.getMessage()))
          .join();
    }
}
