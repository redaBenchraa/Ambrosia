package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEmployeeRegistrationDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.repositories.BusinessRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class BusinessService(
    @Autowired private val businessRepository: BusinessRepository,
    @Autowired private val employeeService: EmployeeService,
) {

    @Transactional
    fun createBusiness(businessRegistrationDto: BusinessRegistrationDto): BusinessRegistrationDto {
        val saveBusiness = businessRepository.save(businessRegistrationDto.toEntity())
        return saveBusiness.toDto().apply {
            employee = employeeService.addEmployee(id!!, businessRegistrationDto.employee!!)
                .toEmployeeRegistrationDto()
        }
    }

    @Transactional
    fun editBusiness(businessId: Long, businessRegistrationDto: BusinessRegistrationDto): BusinessRegistrationDto {
        val business = businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, "id", businessId) }
        businessRegistrationDto.name?.let { business.name = it }
        businessRegistrationDto.phoneNumber?.let { business.phoneNumber = it }
        businessRegistrationDto.email?.let { business.email = it }
        businessRegistrationDto.description?.let { business.description = it }
        businessRegistrationDto.slogan?.let { business.slogan = it }
        businessRegistrationDto.logo?.let { business.logo = it }
        businessRegistrationDto.location?.let { business.location = it }
        businessRegistrationDto.currency.let { business.currency = it }
        businessRepository.save(business)
        return business.toDto()
    }
}
