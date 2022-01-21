package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.EmployeeDto
import com.ambrosia.nymph.dtos.EmployeeRegistrationDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class EmployeeService(
    @Autowired private val businessRepository: BusinessRepository,
    @Autowired private val employeeRepository: EmployeeRepository,
) {
    @Transactional
    fun addEmployee(businessId: Long, employeeDto: EmployeeRegistrationDto): EmployeeDto {
        val business =
            businessRepository.findById(businessId).orElseThrow {
                EntityNotFoundException(Business::class.java, "id", businessId)
            }
        verifyIfEmployeeExists(employeeDto)
        val employee = employeeDto.toEntity()
        employee.business = business
        return employeeRepository.save(employee).toDto()
    }

    @Transactional
    fun editEmployee(businessId: Long, employeeId: Long, employeeDto: EmployeeDto): EmployeeDto {
        businessRepository.findById(businessId).orElseThrow {
            EntityNotFoundException(Business::class.java, "id", businessId)
        }
        val employee =
            employeeRepository.findById(employeeId).orElseThrow {
                EntityNotFoundException(Employee::class.java, "id", employeeId)
            }
        employeeDto.firstName?.let { employee.firstName = it }
        employeeDto.lastName?.let { employee.lastName = it }
        employeeDto.position.let { employee.position = it }
        employeeRepository.save(employee)
        return employee.toDto()
    }

    @Transactional
    @Throws(EntityNotFoundException::class)
    fun deleteEmployee(businessId: Long, employeeId: Long) {
        businessRepository.findById(businessId).orElseThrow {
            EntityNotFoundException(Business::class.java, "id", businessId)
        }
        val employee =
            employeeRepository.findById(employeeId).orElseThrow {
                EntityNotFoundException(Employee::class.java, "id", employeeId)
            }
        employeeRepository.delete(employee)
    }

    fun verifyIfEmployeeExists(employeeDto: EmployeeRegistrationDto) {
        if (employeeRepository.countByEmail(employeeDto.email!!) != 0L) {
            throw EntityAlreadyExistsException(Employee::class.java, "email", employeeDto.email!!)
        }
    }
}
