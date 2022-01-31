package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.EditEmailDto
import com.ambrosia.nymph.dtos.EditPositionDto
import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.mappers.toKeyCloakUser
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class EmployeeService(
    @Autowired private val businessRepository: BusinessRepository,
    @Autowired private val employeeRepository: EmployeeRepository,
    @Autowired private val userService: IUserService,
) {
    @Transactional
    fun addEmployee(businessId: Long, employeeDto: EmployeeRegistrationDto): EmployeeDto {
        val business = businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        verifyIfEmployeeExists(employeeDto)
        userService.createKeycloakUser(employeeDto.toKeyCloakUser())
        val employee = employeeDto.toEntity()
        employee.business = business
        return employeeRepository.save(employee).toDto()
    }

    @Transactional
    fun editEmployeeEmail(businessId: Long, employeeId: Long, editEmailDto: EditEmailDto): EmployeeDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val employee = employeeRepository.findById(employeeId)
            .orElseThrow { EntityNotFoundException(Employee::class.java, mutableMapOf("id" to employeeId)) }
        editEmailDto.email?.let {
            userService.updateEmail(employee.toDto().toKeyCloakUser().copy(email = it))
            employee.email = it
        }
        employeeRepository.save(employee)
        return employee.toDto()
    }

    @Transactional
    fun editEmployeePosition(businessId: Long, employeeId: Long, editPositionDto: EditPositionDto): EmployeeDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val employee = employeeRepository.findById(employeeId)
            .orElseThrow { EntityNotFoundException(Employee::class.java, mutableMapOf("id" to employeeId)) }
        val keycloakUser = employee.toDto().apply { position = editPositionDto.position }.toKeyCloakUser()
        userService.updateRoles(keycloakUser)
        employee.position = editPositionDto.position
        employeeRepository.save(employee)
        return employee.toDto()
    }

    @Transactional
    fun editEmployee(businessId: Long, employeeId: Long, employeeDto: EmployeeDto): EmployeeDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val employee = employeeRepository.findById(employeeId)
            .orElseThrow { EntityNotFoundException(Employee::class.java, mutableMapOf("id" to employeeId)) }
        employeeDto.firstName?.let { employee.firstName = it }
        employeeDto.lastName?.let { employee.lastName = it }
        employeeRepository.save(employee)
        return employee.toDto()
    }

    @Transactional
    @Throws(EntityNotFoundException::class)
    fun deleteEmployee(businessId: Long, employeeId: Long) {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val employee = employeeRepository.findById(employeeId)
            .orElseThrow { EntityNotFoundException(Employee::class.java, mutableMapOf("id" to employeeId)) }
        employeeRepository.delete(employee)
    }

    @Transactional
    fun getEmployees(businessId: Long): List<EmployeeDto> {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        return employeeRepository.findByBusinessId(businessId).stream().map { it.toDto() }.toList()
    }

    fun verifyIfEmployeeExists(employeeDto: EmployeeRegistrationDto) {
        employeeDto.email?.let {
            if (employeeRepository.existsByEmail(it)) {
                throw EntityAlreadyExistsException(Employee::class.java, mutableMapOf("email" to it))
            }
        }
    }
}
