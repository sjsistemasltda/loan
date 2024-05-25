package br.company.loan.service.factory;

import br.company.loan.enums.PersonType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class IdentifierValidTypeFactory {
    private final Map<PersonType, IdentifierValidType> instances = new HashMap<>();

    public IdentifierValidTypeFactory() {
        instances.put(PersonType.PF, new ValidateCPF());
        instances.put(PersonType.PJ, new ValidateCNPJ());
        instances.put(PersonType.EU, new ValidateUniversityStudent());
        instances.put(PersonType.AP, new ValidateRetired());
    }

    public IdentifierValidType get(PersonType type) {
        return instances.get(type);
    }
}
