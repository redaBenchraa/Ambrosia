package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class BusinessService(
	@Autowired private val businessRepository: BusinessRepository,
	@Autowired private val employeeRepository: EmployeeRepository
) {
	@Transactional
	fun createBusiness(businessRegistrationDto: BusinessRegistrationDto): BusinessRegistrationDto {
		val employee = businessRegistrationDto.employee?.toEntity()!!
		val saveBusiness = businessRepository.save(businessRegistrationDto.toEntity())
		val result = saveBusiness.toDto()
		if (businessRegistrationDto.employee != null) {
			employee.business = saveBusiness
			val savedEmployee = employeeRepository.save(employee)
			result.employee = savedEmployee.toDto()
		}
		return result
	}
}