package br.company.loan.util.logger;

import br.company.loan.LoanApplication;
import br.company.loan.util.logger.annotation.Hide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static br.company.loan.util.logger.LoggerField.CORRELATION_ID;
import static br.company.loan.util.logger.LoggerField.PAYLOAD;

@Component
public class Logger {
    private final String BASE_PATH = "br.company.loan";

    private final ObjectMapper mapper;
    private org.slf4j.Logger LOGGER;

    public Logger(ObjectMapper mapper) {
        LOGGER = LoggerFactory.getLogger(LoanApplication.class);
        this.mapper = mapper;
    }

    public Logger forClass(Class<?> aClass) {
        LOGGER = LoggerFactory.getLogger(aClass);

        return this;
    }

    public void debug(String msg, Object payload) {
        mdc();
        mdcPayload(payload);

        LOGGER.debug(msg);
    }

    public void info(String msg, Object payload) {
        mdc();
        mdcPayload(payload);

        LOGGER.info(msg);
    }

    public void info(String msg) {
        mdc();

        LOGGER.info(msg);
    }

    public void error(String msg) {
        mdc();

        LOGGER.error(msg);
    }

    public void error(String msg, Throwable ex, Object... payload) {
        mdc();
        mdcError(ex, payload);

        LOGGER.error(msg);
    }

    public void error(Throwable e) {
        mdc();
        mdcError(e);

        LOGGER.error(e.getClass().getName() + ": " + e.getMessage());
    }

    public void error(String message, Throwable e) {
        mdc();
        mdcError(e);

        LOGGER.error(message + ". " + e.getClass().getName() + ": " + e.getMessage());
    }

    @PostConstruct
    public void init() {
        this.setCorrelationId(UUID.randomUUID().toString());
    }

    public String getCorrelationId() {
        return Optional.ofNullable(MDC.get(CORRELATION_ID.getName())).filter(string -> !string.isBlank()).orElse(UUID.randomUUID().toString());
    }
    
    public void setCorrelationId(String correlationId) {
        MDC.put(CORRELATION_ID.getName(), correlationId);
    }

    private void mdcError(Throwable ex, Object... payload) {
        try {
            List<StackTraceElement> trace = Arrays.stream(Thread.currentThread().getStackTrace())
                    .filter(stackTraceElement -> stackTraceElement.getClassName().contains(BASE_PATH))
                    .filter(stackTraceElement -> !stackTraceElement.getClassName().contains(BASE_PATH + ".util.logger.Logger"))
                    .filter(stackTraceElement -> !stackTraceElement.getClassName().contains(BASE_PATH + ".util.logger.AspectLogger"))
                    .toList();

            String message = Optional.ofNullable(ex.getMessage()).orElse("");
            if(Objects.equals(message, "") && ex.getCause() != null && ex.getCause().getMessage() != null) {
                message = ex.getCause().getMessage();
            }
            mdcPayload(Map.of("error", message,
                    "trace", trace,
                    "payload", payload));
        } catch (Exception ignore) {
        }
    }

    private void mdc() {
        try {
            StackTraceElement trace = Arrays.stream(Thread.currentThread().getStackTrace())
                    .filter(stackTraceElement -> stackTraceElement.getClassName().contains(BASE_PATH))
                    .filter(stackTraceElement -> !stackTraceElement.getClassName().contains(BASE_PATH + ".util.logger.Logger"))
                    .filter(stackTraceElement -> !stackTraceElement.getClassName().contains(BASE_PATH + ".util.logger.AspectLogger"))
                    .toList()
                    .get(0);
            MDC.put("class", trace.getClassName().split("\\$")[0]);
            MDC.put("method", trace.getMethodName());
            MDC.remove(PAYLOAD.getName());
        } catch (Exception ignore) {
        }
    }

    private void mdcPayload(Object object) {
        try {
            if (object != null) {
                if (object instanceof ByteArrayInputStream) {
                    MDC.remove(PAYLOAD.getName());
                } else if (!(object instanceof String)) {
                    MDC.put(PAYLOAD.getName(), mapper.writerWithView(Hide.class).writeValueAsString(object));
                } else if (isNotBlank((String) object)) {
                    MDC.put(PAYLOAD.getName(), generateStringPayload(String.valueOf(object)));
                } else {
                    MDC.remove(PAYLOAD.getName());
                }
            } else {
                MDC.remove(PAYLOAD.getName());
            }
        } catch (Exception ignore) {
            MDC.put(PAYLOAD.getName(), "\"error while parsing object\"");
        }
    }

    private boolean isNotBlank(String string) {
        return string != null && !string.isBlank();
    }

    private String generateStringPayload(String json) {
        try {
            return mapper.writerWithView(Hide.class).writeValueAsString(mapper.readTree(json));
        } catch (JsonProcessingException e) {
            return "\"" + json + "\"";
        }
    }
}
