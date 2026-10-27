# encoding: utf-8
Feature: Search on the Vietcombank website

  As a website visitor
  I want to search for banking products and services
  So that I can find relevant information

  @smoke @search
  Scenario Outline: Search using a banking-related keyword
    Given I open the Vietcombank homepage
    And I handle any visible cookie consent banner
    When I search for "<keyword>" using the homepage search bar
    Then the search interface should display a response for "<keyword>"
    And the search results or an explicit no-results state should be displayed

    Examples:
      | keyword       |
      | Mở tài khoản  |
      | Thẻ tín dụng  |
