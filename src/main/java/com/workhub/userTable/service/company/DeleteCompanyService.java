package com.workhub.userTable.service.company;

import com.workhub.userTable.entity.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCompanyService {

    private final CompanyService companyService;

    /**
     * 회사 소프트 삭제
     * 실제 데이터를 삭제하지 않고 deletedAt 타임스탬프를 기록
     *
     * @param companyId 삭제할 회사 ID
     * @throws BusinessException 회사가 존재하지 않을 경우
     */
    public void deleteCompany(Long companyId) {
        Company company = companyService.findById(companyId);
        company.markDeleted();
    }
}
