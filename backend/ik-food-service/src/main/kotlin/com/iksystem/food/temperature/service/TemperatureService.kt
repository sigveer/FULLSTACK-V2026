package com.iksystem.food.temperature.service

import com.iksystem.common.exception.BadRequestException
import com.iksystem.common.exception.NotFoundException
import com.iksystem.common.security.AuthenticatedUser
import com.iksystem.common.user.model.User
import com.iksystem.common.user.repository.UserRepository
import com.iksystem.food.temperature.dto.CreateTemperatureApplianceRequest
import com.iksystem.food.temperature.dto.RegisterTemperatureMeasurementRequest
import com.iksystem.food.temperature.dto.TemperatureApplianceResponse
import com.iksystem.food.temperature.dto.TemperatureMeasurementResponse
import com.iksystem.food.temperature.dto.UpdateTemperatureApplianceRequest
import com.iksystem.food.temperature.model.TemperatureAppliance
import com.iksystem.food.temperature.model.TemperatureMeasurement
import com.iksystem.food.temperature.model.TemperatureMeasurementStatus
import com.iksystem.food.temperature.repository.TemperatureApplianceRepository
import com.iksystem.food.temperature.repository.TemperatureMeasurementRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant

@Service
class TemperatureService(
    private val applianceRepository: TemperatureApplianceRepository,
    private val measurementRepository: TemperatureMeasurementRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = true)
    fun listAppliances(auth: AuthenticatedUser): List<TemperatureApplianceResponse> {
        val orgId = auth.requireOrganizationId()
        return applianceRepository.findAllByOrganizationIdOrderByNameAsc(orgId).map { appliance ->
            val lastMeasurement = measurementRepository
                .findTopByOrganizationIdAndApplianceIdOrderByMeasuredAtDesc(orgId, appliance.id)
                ?.toResponse()
            appliance.toResponse(lastMeasurement)
        }
    }

    @Transactional
    fun createAppliance(request: CreateTemperatureApplianceRequest, auth: AuthenticatedUser): TemperatureApplianceResponse {
        validateThreshold(request.minTemperature, request.maxTemperature)
        val orgId = auth.requireOrganizationId()

        val saved = applianceRepository.save(
            TemperatureAppliance(
                organizationId = orgId,
                name = request.name.trim(),
                applianceType = request.applianceType,
                minTemperature = request.minTemperature,
                maxTemperature = request.maxTemperature,
            )
        )
        return saved.toResponse()
    }

    @Transactional
    fun updateAppliance(id: Long, request: UpdateTemperatureApplianceRequest, auth: AuthenticatedUser): TemperatureApplianceResponse {
        val orgId = auth.requireOrganizationId()
        val existing = requireAppliance(id, orgId)

        val nextMin = request.minTemperature ?: existing.minTemperature
        val nextMax = request.maxTemperature ?: existing.maxTemperature
        validateThreshold(nextMin, nextMax)

        val updated = applianceRepository.save(
            existing.copy(
                name = request.name?.trim()?.takeIf { it.isNotEmpty() } ?: existing.name,
                minTemperature = nextMin,
                maxTemperature = nextMax,
                isActive = request.isActive ?: existing.isActive,
                updatedAt = Instant.now(),
            )
        )

        val lastMeasurement = measurementRepository
            .findTopByOrganizationIdAndApplianceIdOrderByMeasuredAtDesc(orgId, updated.id)
            ?.toResponse()

        return updated.toResponse(lastMeasurement)
    }

    @Transactional
    fun deleteAppliance(id: Long, auth: AuthenticatedUser) {
        val orgId = auth.requireOrganizationId()
        val appliance = requireAppliance(id, orgId)
        applianceRepository.delete(appliance)
    }

    @Transactional(readOnly = true)
    fun listMeasurements(auth: AuthenticatedUser, applianceId: Long?): List<TemperatureMeasurementResponse> {
        val orgId = auth.requireOrganizationId()

        val measurements = if (applianceId != null) {
            requireAppliance(applianceId, orgId)
            measurementRepository.findAllByOrganizationIdAndApplianceIdOrderByMeasuredAtDesc(orgId, applianceId)
        } else {
            measurementRepository.findAllByOrganizationIdOrderByMeasuredAtDesc(orgId)
        }

        return measurements.map { it.toResponse() }
    }

    @Transactional
    fun registerMeasurement(request: RegisterTemperatureMeasurementRequest, auth: AuthenticatedUser): TemperatureMeasurementResponse {
        val orgId = auth.requireOrganizationId()
        val appliance = requireAppliance(request.applianceId, orgId)
        if (!appliance.isActive) {
            throw BadRequestException("Cannot register measurement for inactive appliance")
        }

        val user = requireUser(auth.userId)
        val measuredAt = request.measuredAt?.let { Instant.parse(it) } ?: Instant.now()
        val status = evaluateStatus(request.temperature, appliance.minTemperature, appliance.maxTemperature)

        val saved = measurementRepository.save(
            TemperatureMeasurement(
                organizationId = orgId,
                appliance = appliance,
                measuredByUser = user,
                measuredAt = measuredAt,
                temperature = request.temperature,
                note = request.note?.trim()?.takeIf { it.isNotEmpty() },
                status = status,
            )
        )

        return saved.toResponse()
    }

    @Transactional
    fun deleteMeasurements(ids: List<Long>, auth: AuthenticatedUser): Int {
        if (ids.isEmpty()) return 0

        val orgId = auth.requireOrganizationId()
        val rows = measurementRepository.findAllByOrganizationIdAndIdIn(orgId, ids)
        measurementRepository.deleteAll(rows)
        return rows.size
    }

    private fun requireAppliance(id: Long, orgId: Long): TemperatureAppliance {
        return applianceRepository.findByIdAndOrganizationId(id, orgId)
            ?: throw NotFoundException("Temperature appliance not found")
    }

    private fun requireUser(userId: Long): User {
        return userRepository.findById(userId).orElseThrow { NotFoundException("User not found") }
    }

    private fun validateThreshold(min: BigDecimal, max: BigDecimal) {
        if (min >= max) {
            throw BadRequestException("Min temperature must be lower than max temperature")
        }
    }

    private fun evaluateStatus(value: BigDecimal, min: BigDecimal, max: BigDecimal): TemperatureMeasurementStatus {
        if (value < min || value > max) {
            return TemperatureMeasurementStatus.DEVIATION
        }
        return TemperatureMeasurementStatus.OK
    }
}

private fun TemperatureAppliance.toResponse(lastMeasurement: TemperatureMeasurementResponse? = null) =
    TemperatureApplianceResponse(
        id = id,
        organizationId = organizationId,
        name = name,
        applianceType = applianceType,
        minTemperature = minTemperature,
        maxTemperature = maxTemperature,
        isActive = isActive,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
        lastMeasurement = lastMeasurement,
    )

private fun TemperatureMeasurement.toResponse() =
    TemperatureMeasurementResponse(
        id = id,
        organizationId = organizationId,
        applianceId = appliance.id,
        applianceName = appliance.name,
        applianceType = appliance.applianceType,
        measuredByUserId = measuredByUser.id,
        measuredByUserName = measuredByUser.fullName,
        measuredAt = measuredAt.toString(),
        temperature = temperature,
        note = note,
        status = status,
        createdAt = createdAt.toString(),
    )
