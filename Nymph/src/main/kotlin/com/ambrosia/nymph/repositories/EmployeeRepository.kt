package com.ambrosia.nymph.repositories

import com.ambrosia.nymph.entities.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : JpaRepository<Employee, String>
