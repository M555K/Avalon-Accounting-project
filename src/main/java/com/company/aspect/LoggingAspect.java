package com.company.aspect;

import com.company.dto.UserDto;
import com.company.entity.Company;
import com.company.service.CompanyService;
import com.company.service.SecurityService;
import com.company.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final SecurityService securityService;
    private final CompanyService companyService;
    private final MapperUtil mapperUtil;

    public LoggingAspect(SecurityService securityService, CompanyService companyService, MapperUtil mapperUtil) {
        this.securityService = securityService;
        this.companyService = companyService;
        this.mapperUtil = mapperUtil;
    }

    private String getUserInfo(){
        UserDto userDto = securityService.getLoggedInUser();
        return userDto.getFirstname() +" "+ userDto.getLastname() +" "+ userDto.getUsername();
    }

    @Pointcut("execution (* com.company.controller.CompanyController.activateCompany(*)) || execution(* com.company.controller.CompanyController.deactivateCompany(*))")
    public void activateOrDeactivateCompany() {}

    @AfterReturning(pointcut = "activateOrDeactivateCompany()")
    public void afterReturningActivateOrDeactivateCompany(JoinPoint joinPoint) {

        Long companyId = (Long) joinPoint.getArgs()[0];
        Company company = mapperUtil.convert(companyService.findById(companyId), new Company());
        String companyName = company.getTitle();

        String method = joinPoint.getSignature().getName().contains("deactivate") ? "Deactivate" : "Activate";

        log.info("Method: {}, Company: {}, User: {}"
                , method
                , companyName
                , getUserInfo());
    }

}
