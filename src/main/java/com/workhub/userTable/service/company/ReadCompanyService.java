package com.workhub.userTable.service.company;

import com.workhub.project.entity.Project;
import com.workhub.project.entity.Status;
import com.workhub.project.service.ProjectService;
import com.workhub.userTable.dto.company.response.CompanyDetailResponse;
import com.workhub.userTable.dto.company.response.CompanyListResponse;
import com.workhub.userTable.dto.company.response.CompanyTitleResponse;
import com.workhub.userTable.entity.Company;
import com.workhub.userTable.entity.UserRole;
import com.workhub.userTable.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadCompanyService {

    private final CompanyService companyService;
    private final ProjectService projectService;
    private final UserService userService;

    /**
     * 활성 회사 목록을 페이징하여 조회
     * 각 회사별 프로젝트 통계(진행 중/총 프로젝트 수)와 클라이언트 멤버 수를 포함
     *
     * @param pageable 페이징 정보 (size, sort 등)
     * @return 회사 목록 응답 (통계 포함)
     */
    public Page<CompanyListResponse> getCompanies(Pageable pageable) {
        Page<Company> companies = companyService.findAllByCompanystatus(pageable);

        return companies.map(company -> {
            // 해당 회사의 프로젝트 조회 (ProjectService를 통해)
            List<Project> projects = projectService.findAllByClientCompanyId(company.getCompanyId());

            // IN_PROGRESS와 총 프로젝트 카운트
            long inProgressCount = projects.stream()
                    .filter(p -> p.getStatus() == Status.IN_PROGRESS)
                    .count();
            long totalProjectCount = projects.size();

            // 활성 클라이언트 멤버 수 조회 (UserService를 통해)
            long clientMemberCount = userService.countByCompanyIdAndRoleAndStatus(
                    company.getCompanyId(),
                    UserRole.CLIENT,
                    com.workhub.userTable.entity.Status.ACTIVE
            );

            // CompanyListResponse 생성
            return CompanyListResponse.from(company, inProgressCount, totalProjectCount, clientMemberCount);
        });
    }

    /**
     * 모든 활성 회사의 이름 목록 조회
     * 드롭다운이나 선택 UI에서 사용
     *
     * @return 회사 ID와 이름을 포함한 간단한 응답 리스트
     */
    public List<CompanyTitleResponse> getCompanyNameList() {
        return companyService.findAllActiveCompanies().stream()
                .map(CompanyTitleResponse::from)
                .toList();
    }

    /**
     * 특정 회사의 상세 정보 조회
     *
     * @param companyId 조회할 회사 ID
     * @return 회사 상세 정보
     * @throws BusinessException 회사가 존재하지 않거나 비활성 상태일 경우
     */
    public CompanyDetailResponse getCompany(Long companyId) {
        Company company = companyService.findByCompanyIdAndCompanystatus(companyId);
        return CompanyDetailResponse.from(company);
    }

    /**
     * 여러 회사를 배치로 조회하여 Map으로 반환
     * 다른 도메인(Project 등)에서 N+1 문제 방지를 위해 사용
     *
     * @param companyIds 조회할 회사 ID 리스트
     * @return 회사 ID를 키로 하는 Company 엔티티 맵
     */
    public Map<Long, Company> getCompanyMapByCompanyIdIn(List<Long> companyIds) {
        return companyService.findAllByCompanyIdInAndCompanystatus(companyIds);
    }

    /**
     * 활성 상태인 회사의 총 개수 조회
     * 관리자 대시보드 통계에서 사용
     *
     * @return 활성 회사 수
     */
    public Long countActiveCompanies() {
        return companyService.countActiveCompanies();
    }
}
