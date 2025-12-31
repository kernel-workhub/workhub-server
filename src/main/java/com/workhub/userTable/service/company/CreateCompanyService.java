package com.workhub.userTable.service.company;

import com.workhub.userTable.dto.company.request.CompanyRegisterRequest;
import com.workhub.userTable.dto.company.response.CompanyResponse;
import com.workhub.userTable.entity.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateCompanyService {

    private final CompanyService companyService;

    /**
     * 새로운 회사 등록
     * 사업자번호 중복 검증 후 회사 정보를 저장
     *
     * @param request 회사 등록 요청 정보 (회사명, 사업자번호 등)
     * @return 등록된 회사 정보
     * @throws BusinessException 사업자번호가 이미 존재할 경우
     */
    public CompanyResponse registerCompany(CompanyRegisterRequest request) {

        companyService.validateDuplicateCompanyNumber(request.companyNumber());

        Company company = Company.of(request);
        Company savedCompany = companyService.save(company);

        return CompanyResponse.from(savedCompany);
    }
}
