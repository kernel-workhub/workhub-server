package com.workhub.userTable.controller;

import com.workhub.global.response.ApiResponse;
import com.workhub.userTable.api.CompanyApi;
import com.workhub.userTable.dto.company.request.CompanyRegisterRequest;
import com.workhub.userTable.dto.company.request.CompanyStatusUpdateRequest;
import com.workhub.userTable.dto.company.response.CompanyDetailResponse;
import com.workhub.userTable.dto.company.response.CompanyListResponse;
import com.workhub.userTable.dto.company.response.CompanyResponse;
import com.workhub.userTable.dto.company.response.CompanyTitleResponse;
import com.workhub.userTable.dto.user.response.UserNameResponse;
import com.workhub.userTable.service.company.CreateCompanyService;
import com.workhub.userTable.service.company.DeleteCompanyService;
import com.workhub.userTable.service.company.ReadCompanyService;
import com.workhub.userTable.service.company.UpdateCompanyService;
import com.workhub.userTable.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
public class CompanyController implements CompanyApi {

    private final CreateCompanyService createCompanyService;
    private final ReadCompanyService readCompanyService;
    private final UpdateCompanyService updateCompanyService;
    private final DeleteCompanyService deleteCompanyService;
    private final UserService userService;

    @PostMapping("/add")
    @Override
    public ResponseEntity<ApiResponse<CompanyResponse>> registerCompany(@RequestBody @Valid CompanyRegisterRequest request) {
        CompanyResponse response = createCompanyService.registerCompany(request);
        return ApiResponse.created(response, "고객사가 등록되었습니다.");
    }

    @GetMapping
    @Override
    public ResponseEntity<ApiResponse<Page<CompanyListResponse>>> getCompanies(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CompanyListResponse> companies = readCompanyService.getCompanies(pageable);
        return ApiResponse.success(companies);
    }

    @GetMapping("/list")
    @Override
    public ResponseEntity<ApiResponse<List<CompanyTitleResponse>>> getCompanyNames() {

        List<CompanyTitleResponse> response = readCompanyService.getCompanyNameList();
        return ApiResponse.success(response);
    }

    @GetMapping("/detail/{companyId}")
    @Override
    public ResponseEntity<ApiResponse<CompanyDetailResponse>> getCompany(@PathVariable("companyId") Long companyId) {

        CompanyDetailResponse company = readCompanyService.getCompany(companyId);
        return ApiResponse.success(company);
    }
    @DeleteMapping("/{companyId}")
    @Override
    public ResponseEntity<ApiResponse<Object>> deleteCompany(@PathVariable("companyId") Long companyId) {
            deleteCompanyService.deleteCompany(companyId);
            return ApiResponse.success(null, "고객사가 비활성화되었습니다.");
    }

    @PatchMapping("/{companyId}/status")
    @Override
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompanyStatus(
            @PathVariable("companyId") Long companyId,
            @RequestBody @Valid CompanyStatusUpdateRequest request
    ) {
        CompanyResponse response = updateCompanyService.updateCompanyStatus(companyId, request.status());
        return ApiResponse.success(response, "고객사 상태가 변경되었습니다.");
    }

    @GetMapping("/{companyId}/list")
    @Override
    public ResponseEntity<ApiResponse<List<UserNameResponse>>> getMemberList(@PathVariable("companyId") Long companyId) {

        List<UserNameResponse> responses = userService.getUserMapByCompanyIdIn(companyId);
        return ApiResponse.success(responses);
    }
}
