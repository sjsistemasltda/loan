package br.company.loan.infrastructure;

import br.company.loan.entity.dto.response.LoanResponseDTO;
import br.company.loan.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "paymentClient", url = "${payment.service.url}", configuration = FeignConfig.class)
public interface PaymentClient {

    @PostMapping("/v1/payments/{loanId}/pay")
    LoanResponseDTO pay(@PathVariable("loanId") Long loanId);
}
