![workflow](https://github.com/alfian853/resilix/actions/workflows/build.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=alfian853_resilix&metric=alert_status)](https://sonarcloud.io/dashboard?id=alfian853_resilix)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=alfian853_resilix&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=alfian853_resilix)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=alfian853_resilix&metric=coverage)](https://sonarcloud.io/dashboard?id=alfian853_resilix)


# Resilix: A Lightweight Circuit Breaker Library


## Configuration

### kruskal.resilix.config.{your-key}.errorThreshold
Configures the error threshold in percentage in close state and half-open state (for retry process).
when the error rate greater or equal to the threshold, the circuit will trip to open state.

|default value|`0.5`|
|:---:|:---|
|possible value|from `0.0` to `1.0`|


### kruskal.resilix.config.{your-key}.slidingWindowStrategy
Configures the sliding window aggregation type. 

- COUNT_BASED: aggregate error rate by the last ***n*** records.
- TIME_BASED: aggregate error rate from the last ***t*** milliseconds.

|default value|`COUNT_BASED`|
|:---:|:---|
|possible value|`COUNT_BASED` or `TIME_BASED`|

### kruskal.resilix.config.{your-key}.slidingWindowMaxSize
Configures the maximum size for `slidingWindowStrategy=COUNT_BASED`

|default value|`20`|
|:---:|:---|

### kruskal.resilix.config.{your-key}.slidingWindowTimeRange
Configures the window time range for `slidingWindowStrategy=TIME_BASED`

|default value|`15000` ms|
|:---:|:---|


### kruskal.resilix.config.{your-key}.retryStrategy
Configures the retry strategy. 
<br>

- PESSIMISTIC: Do retry once at a time as long as the error threshold hasn't reached, and the retry limit (`retryCountInHalfOpenState`) hasn't exceeded.
- OPTIMISTIC: Do retry as long as the error threshold hasn't reached, and the retry limit (`retryCountInHalfOpenState`) hasn't exceeded.
  it will result in more errors if multiple retries happened at the same time and get errors.

|default value|`PESSIMISTIC`|
|:---:|:---|
|possible value|`OPTIMISTIC` or `PESSIMISTIC`|

### kruskal.resilix.config.{your-key}.numberOfRetryInHalfOpenState
Configures the number of minimum retries to be done in the half-open state before the evaluation to decide which state it belongs to.

|default value|`10`|
|:---:|:------------------------|

### kruskal.resilix.config.{your-key}.minimumCallToEvaluate
Configures the minimum records to be evaluated in sliding window to prevent 100% error rate if the
first attempt is failed.


|default value|`10`|
|:---:|:------------------------|

