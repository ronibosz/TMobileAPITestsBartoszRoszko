Feature: Checking exchange rates using NBP API
  Scenario: Fetching rates for given parameters
    Given Base URI is set
    When Exchange rates are fetched from NBP API
    Then Rate for "USD" code can be displayed
    And Rate for "dolar amerykański" currency can be displayed
    And Rates above 5 can be displayed
    And Rates below 3 can be displayed
