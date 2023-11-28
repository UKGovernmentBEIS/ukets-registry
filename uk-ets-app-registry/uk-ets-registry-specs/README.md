# UK ETS Registry feature files specifications

UK ETS Registry specifications define at BDD (Behavior Driven Development) the tests cases derived by use cases. Current
BDD language is named Gherkin. This is a Cucumber BDD language. Cucumbber is a test framework. BDD is written in feature
files. These files use the "\*.feature" extension.
"Given", "When", "Then", "And", "But" keywords create the behavioral way of steps/set of steps acting.

## Background

Background is a compact form of BDD steps utilization when these steps are used by all scenarios in a feature, and these
scenarios start with them. When all scenarios/scenario outlines in a specific feature file start with a specific set of
steps, then these steps can be gathered to Background snippet so as to avoid repetition (and maintenance effort).
"Given", "When", "Then", "And", "But" reserved keywords can thus be replaced with "\*".

## Scenario/ Scenario Outline with Examples (parameterized Scenarios)

To add content

## Functional areas separation

To add content

## Scenarios annotations

To add content

## Datatables

To add content

# Feature files tags (Before all tests in feature, before each scenario, specific scenario tags, after each scenario, after all tests in feature)

To add content

## Scenarios execution report

To add content

# local environment (localhost)

Here you will find instructions about how to set up the local environment in order to be able to execute automation
tests:

Step 1: start docker Srep 2: git pull on home directory (C:\UKETS\uk-ets-home)
Step 3: execute: xx-pull-all.sh (make sure you don’t have uncommitted changes)
Step 4: execute: . ./00a-define-local-variables.sh (the first dot is required)
Step 5: execute: 00b-build-all.sh Step 6: execute: uk-ets-home\ukets\uk-ets-env-development_runme-e2e.sh Step 7: check
if keyclock is up. Step 8: 03-start-registration Step 9: 04-start-itl-invoking-service Step 10:
05-start-itl-receiving-endpoint Step 11: 06-start-itl-emulator Step 12: 07-start-transaction-log Step 13: start api (C:
\UKETS\uk-ets-home\ukets\uk-ets-app-registry\uk-ets-registry-api -> ./\_runme.sh)

In order to execute specific tests make sure that protractor.conf.js is correctly set up. i.e: "tags: '(@run) and (not
@exec-manual) and (not @api-security)'" is not commented out if you want to execute tests with the tag: "@run"

If need to execute a specific test, it is possible to do so by adding the tag @run to it.

In order to start execution use the command:
ng e2e

In order to generate a report once the test is finished use the command:
node index

# continuous integration environment (Jenkins)

To add content
