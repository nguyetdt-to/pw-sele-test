// Generated from: features\web\SearchServices.feature
import { test } from "playwright-bdd";

test.describe('Search on the Vietcombank website', () => {

  test.describe('Search using a banking-related keyword', () => {

    test('Example #1', { tag: ['@smoke', '@search'] }, async ({ Given, When, Then, page }) => { 
      await Given('I open the Vietcombank homepage', null, { page }); 
      await When('I handle any visible cookie consent banner', null, { page }); 
      await When('I search for "Mở tài khoản" using the homepage search bar', null, { page }); 
      await Then('the search interface should display a response for "Mở tài khoản"', null, { page }); 
      await Then('the search results or an explicit no-results state should be displayed', null, { page }); 
    });

    test('Example #2', { tag: ['@smoke', '@search'] }, async ({ Given, When, Then, page }) => { 
      await Given('I open the Vietcombank homepage', null, { page }); 
      await When('I handle any visible cookie consent banner', null, { page }); 
      await When('I search for "Thẻ tín dụng" using the homepage search bar', null, { page }); 
      await Then('the search interface should display a response for "Thẻ tín dụng"', null, { page }); 
      await Then('the search results or an explicit no-results state should be displayed', null, { page }); 
    });

  });

});

// == technical section ==

test.use({
  $test: [({}, use) => use(test), { scope: 'test', box: true }],
  $uri: [({}, use) => use('features\\web\\SearchServices.feature'), { scope: 'test', box: true }],
  $bddFileData: [({}, use) => use(bddFileData), { scope: "test", box: true }],
});

const bddFileData = [ // bdd-data-start
  {"pwTestLine":8,"pickleLine":17,"tags":["@smoke","@search"],"steps":[{"pwStepLine":9,"gherkinStepLine":9,"keywordType":"Context","textWithKeyword":"Given I open the Vietcombank homepage","stepMatchArguments":[]},{"pwStepLine":10,"gherkinStepLine":10,"keywordType":"Action","textWithKeyword":"When I handle any visible cookie consent banner","stepMatchArguments":[]},{"pwStepLine":11,"gherkinStepLine":11,"keywordType":"Action","textWithKeyword":"When I search for \"Mở tài khoản\" using the homepage search bar","stepMatchArguments":[{"group":{"start":13,"value":"\"Mở tài khoản\"","children":[{"start":14,"value":"Mở tài khoản","children":[{}]},{"children":[{}]}]},"parameterTypeName":"string"}]},{"pwStepLine":12,"gherkinStepLine":12,"keywordType":"Outcome","textWithKeyword":"Then the search interface should display a response for \"Mở tài khoản\"","stepMatchArguments":[{"group":{"start":51,"value":"\"Mở tài khoản\"","children":[{"start":52,"value":"Mở tài khoản","children":[{}]},{"children":[{}]}]},"parameterTypeName":"string"}]},{"pwStepLine":13,"gherkinStepLine":13,"keywordType":"Outcome","textWithKeyword":"Then the search results or an explicit no-results state should be displayed","stepMatchArguments":[]}]},
  {"pwTestLine":16,"pickleLine":18,"tags":["@smoke","@search"],"steps":[{"pwStepLine":17,"gherkinStepLine":9,"keywordType":"Context","textWithKeyword":"Given I open the Vietcombank homepage","stepMatchArguments":[]},{"pwStepLine":18,"gherkinStepLine":10,"keywordType":"Action","textWithKeyword":"When I handle any visible cookie consent banner","stepMatchArguments":[]},{"pwStepLine":19,"gherkinStepLine":11,"keywordType":"Action","textWithKeyword":"When I search for \"Thẻ tín dụng\" using the homepage search bar","stepMatchArguments":[{"group":{"start":13,"value":"\"Thẻ tín dụng\"","children":[{"start":14,"value":"Thẻ tín dụng","children":[{}]},{"children":[{}]}]},"parameterTypeName":"string"}]},{"pwStepLine":20,"gherkinStepLine":12,"keywordType":"Outcome","textWithKeyword":"Then the search interface should display a response for \"Thẻ tín dụng\"","stepMatchArguments":[{"group":{"start":51,"value":"\"Thẻ tín dụng\"","children":[{"start":52,"value":"Thẻ tín dụng","children":[{}]},{"children":[{}]}]},"parameterTypeName":"string"}]},{"pwStepLine":21,"gherkinStepLine":13,"keywordType":"Outcome","textWithKeyword":"Then the search results or an explicit no-results state should be displayed","stepMatchArguments":[]}]},
]; // bdd-data-end