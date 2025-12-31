package com.workhub.userTable.service.company;

import com.workhub.global.error.ErrorCode;
import com.workhub.global.error.exception.BusinessException;
import com.workhub.userTable.entity.Company;
import com.workhub.userTable.entity.CompanyStatus;
import com.workhub.userTable.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    /**
     * Company ID로 활성 회사 조회 (Core 메서드)
     * 다른 도메인 서비스에서 사용
     *
     * @param companyId 회사 ID
     * @return 활성 회사 엔티티
     */
    @Transactional(readOnly = true)
    public Company findById(Long companyId) {
        return companyRepository.findByCompanyIdAndCompanystatus(companyId, CompanyStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.Company_NOT_EXISTS));
    }

    /**
     * 사업자번호 중복 검증 (Core 유틸)
     *
     * @param companyNumber 사업자번호
     * @throws BusinessException 중복된 사업자번호가 존재할 경우
     */
    public void validateDuplicateCompanyNumber(String companyNumber) {
        if (companyRepository.existsByCompanyNumber(companyNumber)) {
            throw new BusinessException(ErrorCode.COMPANY_ALREADY_EXISTS);
        }
    }

    /**
     * Company 저장
     *
     * @param company 저장할 회사 엔티티
     * @return 저장된 회사 엔티티
     */
    @Transactional
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    /**
     * 활성 회사 페이징 조회
     *
     * @param pageable 페이징 정보
     * @return 활성 회사 페이지
     */
    @Transactional(readOnly = true)
    public Page<Company> findAllByCompanystatus(Pageable pageable) {
        return companyRepository.findAllByCompanystatus(CompanyStatus.ACTIVE, pageable);
    }

    /**
     * 모든 활성 회사 조회
     *
     * @return 활성 회사 리스트
     */
    @Transactional(readOnly = true)
    public List<Company> findAllActiveCompanies() {
        return companyRepository.findAllByCompanystatus(CompanyStatus.ACTIVE);
    }

    /**
     * 여러 Company ID로 활성 회사 배치 조회
     *
     * @param companyIds 회사 ID 리스트
     * @return 회사 ID를 키로 하는 회사 맵
     */
    @Transactional(readOnly = true)
    public Map<Long, Company> findAllByCompanyIdInAndCompanystatus(List<Long> companyIds) {
        if (companyIds == null || companyIds.isEmpty()) {
            return Map.of();
        }

        return companyRepository.findAllByCompanyIdInAndCompanystatus(companyIds, CompanyStatus.ACTIVE)
                .stream()
                .collect(Collectors.toMap(Company::getCompanyId, company -> company));
    }

    /**
     * 활성 회사 수 조회
     *
     * @return 활성 회사 개수
     */
    @Transactional(readOnly = true)
    public Long countActiveCompanies() {
        return companyRepository.countByCompanystatus(CompanyStatus.ACTIVE);
    }

    /**
     * Company ID로 활성 회사 상세 조회 (DTO 반환용)
     *
     * @param companyId 회사 ID
     * @return 활성 회사 엔티티
     */
    @Transactional(readOnly = true)
    public Company findByCompanyIdAndCompanystatus(Long companyId) {
        return companyRepository.findByCompanyIdAndCompanystatus(companyId, CompanyStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.Company_NOT_EXISTS));
    }
}
