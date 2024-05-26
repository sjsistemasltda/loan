package br.company.loan.infrastructure;

import br.company.loan.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "paymentClient", url = "${payment.service.url}", configuration = FeignConfig.class)
public interface PaymentClient {

    @PostMapping("/v1/loans/{id}/pay")
    void pay(@PathVariable("id") Long id);
}
