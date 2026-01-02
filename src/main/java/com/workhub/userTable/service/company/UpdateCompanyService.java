package com.workhub.userTable.service.company;

import com.workhub.userTable.dto.company.response.CompanyResponse;
import com.workhub.userTable.entity.Company;
import com.workhub.userTable.entity.CompanyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCompanyService {

    private final CompanyService companyService;

    /**
     * 회사의 상태 변경
     * 활성(ACTIVE) ↔ 비활성(INACTIVE) 전환
     *
     * @param companyId 상태를 변경할 회사 ID
     * @param status 변경할 상태 (ACTIVE, INACTIVE)
     * @return 변경된 회사 정보
     * @throws BusinessException 회사가 존재하지 않을 경우
     */
    public CompanyResponse updateCompanyStatus(Long companyId, CompanyStatus status) {
        Company company = companyService.findById(companyId);
        company.updateStatus(status);
        return CompanyResponse.from(company);
    }
}
