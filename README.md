![workflow](https://github.com/alfian853/resilix/actions/workflows/build.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=alfian853_resilix&metric=alert_status)](https://sonarcloud.io/dashboard?id=alfian853_resilix)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=alfian853_resilix&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=alfian853_resilix)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=alfian853_resilix&metric=coverage)](https://sonarcloud.io/dashboard?id=alfian853_resilix)


# Resilix: A Fault Tolerance Library for Java and Springboot
### Table of Contents
#### 1. [Introduction](#Introduction)
#### 2. [Usage Example](#Usage-Example)
#### 3. [Configuration Guidelines](#Configuration-Guidelines)


## Introduction
This library provides some difference features compared to the others such as [Resilience4j](https://github.com/resilience4j/resilience4j) :
- Resilix has 2 retry strategy. See [Retry Strategy](#kruskalresilixconfigyour-keyretrystrategy)
- Call rejection results in a [ResultWrapper](resilix-core/src/main/java/com/kruskal/resilix/core/ResultWrapper.java) object, contrary with the other library like Resilience4j which will throw an [CallNotPermittedException](https://github.com/resilience4j/resilience4j/blob/master/resilience4j-circuitbreaker/src/main/java/io/github/resilience4j/circuitbreaker/CallNotPermittedException.java)

if you have no concern with the retry and call rejection mechanism in Resilience4j, please use Resilience4j instead :) .


## Usage Example
You can take a look at this [demo project](https://github.com/alfian853/resilix-demo)

```xml
<dependencies>
  ........................
  <dependency>
    <groupId>com.kruskal.resilix</groupId>
    <artifactId>resilix-springboot-v2</artifactId>
    <version>${resilix-version}</version>
  </dependency>
</dependencies>
```

```java
@EnableResilix // add this to enable the auto configuration
@SpringBootApplication
public class ResilixDemoApplication {
  public static void main(String[] args) {
    SpringApplication.run(ResilixDemoApplication.class, args);
  }
}
```
```java
@RestController
public class DemoController {

  @Autowired
  private ResilixRegistry resilixRegistry;

  private final String[] thirdPartyList = {"foo", "bar"};


  @GetMapping("/resilix")
  public String callApi() {

    for (String thirdParty : thirdPartyList) {
      // get ResilixExecutor by the contextKey
      ResilixExecutor resilixExecutor = resilixRegistry.getResilixExecutor(thirdParty);

      try {
        ResultWrapper<String> resultWrapper = resilixExecutor.executeChecked(() -> this.callThirdPartyApi(thirdParty));

        //will skip if not execution isn't permitted and return the result if it has been executed
        if (resultWrapper.isExecuted()) return resultWrapper.getResult();
      } catch (Throwable e) {
        //continue to the next 3rd parties if there is any error occurred;
        log.error("{} execution failed", thirdParty, e);
      }
    }

    log.error("all third parties are down");
    throw new RuntimeException("all third parties are down");
  }

  /**
   * will watch the call's result(success/failure) for "somethingKey",
   * if there is an error, the error rate for "somethingKey" will be increased.
   */
  @ResilixWatcher(contextKey = "somethingKey")
  public Boolean callSomeMethod(){
    //do something
    return true;
  }
}
```

if you want to customize the configuration please read the [Configuration Guidelines](##Configuration)
## Configuration Guidelines
Configuration example in application.properties:
```properties
# configuration for "foo"
kruskal.resilix.config.foo.errorThreshold=0.2
kruskal.resilix.config.foo.slidingWindowStrategy=COUNT_BASED
kruskal.resilix.config.foo.retryStrategy=PESSIMISTIC
kruskal.resilix.config.foo.waitDurationInOpenState=10000
kruskal.resilix.config.foo.slidingWindowMaxSize=10
kruskal.resilix.config.foo.numberOfRetryInHalfOpenState=5
kruskal.resilix.config.foo.minimumCallToEvaluate=2

# configuration for "bar"
kruskal.resilix.config.bar.errorThreshold=0.2
kruskal.resilix.config.bar.slidingWindowStrategy=COUNT_BASED
kruskal.resilix.config.bar.retryStrategy=OPTIMISTIC
kruskal.resilix.config.bar.waitDurationInOpenState=10000
kruskal.resilix.config.bar.slidingWindowMaxSize=10
kruskal.resilix.config.bar.numberOfRetryInHalfOpenState=5
kruskal.resilix.config.bar.minimumCallToEvaluate=2
```

### kruskal.resilix.config.{your-key}.errorThreshold
Configures the error threshold in percentage in close state and half-open state (for retry process).
when the error rate is greater or equal to the threshold, the circuit will trip to open state.

|default value|`0.5`|
|:---:|:---|
|possible value|from `0.0` to `1.0`|


### kruskal.resilix.config.{your-key}.slidingWindowStrategy
Configures the sliding-window aggregation type. 

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
Configures the number of minimum retry in the half-open state before deciding which state it belongs to.

|default value|`10`|
|:---:|:------------------------|

### kruskal.resilix.config.{your-key}.minimumCallToEvaluate
Configures the minimum records to be evaluated in sliding-window to prevent 100% error rate if the
first call is failed.


|default value|`10`|
|:---:|:------------------------|

