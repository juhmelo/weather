# weather

Polls weather forecast data for configured cities.
Limit temperatures (celcius) are configured for each city.
The application implements a REST-api where it serves info on each city, 
and if the temperature limit will be violated within the next 5 days.
The API is documented in api.yaml

## Development

Configuring the application is done in `dev-env.edn`.
Configurable variables are as follows:

### Required
`API_KEY` 
    Api key needed for calls to forecast service https://openweathermap.org/. You will need to register and acquire a key.
`LOCATION_CONFIG` 
    Which cities to get forecasts on, and what the limit temperatures are.
    The configuration is a vector of maps, each with two keys: `id` (city id) and `limit` (limit temperature in celcius).

### Optional
`HTTP_PORT`
    Port for HTTP server to listen on, defaults to 8090 if blank
`POLL_INTERVAL_MS` 
    Poll interval to forecast api in milliseconds, defaults to 10000 (10sec)
`FORECAST_URL` 
    Forecast api url, defaults to "https://api.openweathermap.org/data/2.5/forecast" and currently the only one that is supported.

Run  the application:

```
$ lein repl
user=> (reset)
```

## Testing

With coverage report:

    $ lein cloverage

Same, but without coverage report:

    $ lein test

## Building

```
$ lein uberjar
```

## Running

```
$ lein run
```

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
