package com.aipa.telemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;

/**
 * Lightweight observation logger to make span hierarchy visible in app logs
 * while Jaeger/OTel collector captures full traces.
 */
@Component
public class LoggingObservationHandler implements ObservationHandler<Observation.Context> {

    private static final Logger log = LoggerFactory.getLogger(LoggingObservationHandler.class);

    @Override
    public void onStart(Observation.Context context) {
        log.debug("span start: {}", context.getName());
    }

    @Override
    public void onStop(Observation.Context context) {
        log.debug("span stop: {}", context.getName());
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }
}
