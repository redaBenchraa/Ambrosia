package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.mappers.toRegistrationEmployeeDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional


@Service
class BusinessService(
	@Autowired private val businessRepository: BusinessRepository,
	@Autowired private val employeeRepository: EmployeeRepository
) {

	@PersistenceContext
	private lateinit var entityManager: EntityManager

	@Transactional
	fun createBusiness(businessRegistrationDto: BusinessRegistrationDto): BusinessRegistrationDto {
		val saveBusiness = businessRepository.save(businessRegistrationDto.toEntity())
		val result = saveBusiness.toDto()
		if (businessRegistrationDto.employee != null) {
			verifyIfEmployeeExists(businessRegistrationDto.employee!!)
			val employee = businessRegistrationDto.employee!!.toEntity()
			employee.business = saveBusiness
			val savedEmployee = employeeRepository.save(employee)
			result.employee = savedEmployee.toRegistrationEmployeeDto()
		}
		return result
	}

	fun verifyIfEmployeeExists(employeeDto: EmployeeRegistrationDto) {
		employeeRepository.findByEmail(employeeDto.email!!)
			.ifPresent {
				throw EntityAlreadyExistsException(Employee::class.java, "email", employeeDto.email!!)
			}
	}

	@Transactional
	fun addEmployee(businessId: Long, employeeDto: EmployeeRegistrationDto): EmployeeDto {
		val business = businessRepository.findById(businessId)
			.orElseThrow { EntityNotFoundException(Business::class.java, "id", businessId) }
		verifyIfEmployeeExists(employeeDto)
		val employee = employeeDto.toEntity()
		employee.business = business
		return employeeRepository.save(employee).toDto()
	}

	@Transactional
	@Throws(EntityNotFoundException::class)
	fun deleteEmployee(businessId: Long, employeeId: Long) {
		businessRepository.findById(businessId)
			.orElseThrow { EntityNotFoundException(Business::class.java, "id", businessId) }
		val employee = employeeRepository.findById(employeeId)
			.orElseThrow { EntityNotFoundException(Employee::class.java, "id", employeeId) }
		employeeRepository.delete(employee)
	}
}