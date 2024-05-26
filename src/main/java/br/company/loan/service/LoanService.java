package br.company.loan.service;

import br.company.loan.entity.dto.request.LoanCreateRequestDTO;
import br.company.loan.entity.dto.response.LoanResponseDTO;

public interface LoanService {
    LoanResponseDTO make(Long personId, LoanCreateRequestDTO loan);
}
