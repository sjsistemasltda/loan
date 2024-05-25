package br.company.loan.service;

import br.company.loan.entity.dto.request.LoanCreateRequestDTO;

public interface LoanService {
    void make(Long personId, LoanCreateRequestDTO loan);
}
