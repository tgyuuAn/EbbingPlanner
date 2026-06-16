package com.tgyuu.experiment.data.repository

import com.tgyuu.experiment.data.datasource.ExperimentLocalDataSource
import com.tgyuu.experiment.data.datasource.ExperimentMemoryDataSource
import com.tgyuu.experiment.data.datasource.ExperimentRemoteDataSource
import com.tgyuu.experiment.domain.model.Experiment
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ExperimentRepositoryImplTest {

    private lateinit var remoteDataSource: FakeExperimentRemoteDataSource
    private lateinit var localDataSource: FakeExperimentLocalDataSource
    private lateinit var memoryDataSource: FakeExperimentMemoryDataSource
    private lateinit var repository: ExperimentRepositoryImpl

    @Before
    fun setup() {
        remoteDataSource = FakeExperimentRemoteDataSource()
        localDataSource = FakeExperimentLocalDataSource()
        memoryDataSource = FakeExperimentMemoryDataSource()
        repository = ExperimentRepositoryImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            memoryDataSource = memoryDataSource,
        )
    }

    @Test
    fun `메모리 캐시에 값이 있으면 메모리에서 반환한다`() = runTest {
        // Given
        val experimentKey = Experiment.SaveButtonPosition.key
        memoryDataSource.saveAssignment(experimentKey, "TREATMENT")

        // When
        val result = repository.getVariant(Experiment.SaveButtonPosition)

        // Then
        assertEquals(Experiment.SaveButtonPosition.Variant.TREATMENT, result)
        assertEquals(0, localDataSource.getAssignmentCallCount)
        assertEquals(0, remoteDataSource.fetchCallCount)
    }

    @Test
    fun `메모리 캐시가 비어있으면 로컬에서 가져온다`() = runTest {
        // Given
        val experimentKey = Experiment.SaveButtonPosition.key
        localDataSource.saveAssignment(experimentKey, "CONTROL")

        // When
        val result = repository.getVariant(Experiment.SaveButtonPosition)

        // Then
        assertEquals(Experiment.SaveButtonPosition.Variant.CONTROL, result)
        assertEquals(0, remoteDataSource.fetchCallCount)
        assertEquals("CONTROL", memoryDataSource.getAssignment(experimentKey))
    }

    @Test
    fun `로컬과 메모리가 모두 비어있으면 리모트에서 가져온다`() = runTest {
        // Given
        val experimentKey = Experiment.SaveButtonPosition.key
        remoteDataSource.setRemoteVariant(experimentKey, "TREATMENT")

        // When
        val result = repository.getVariant(Experiment.SaveButtonPosition)

        // Then
        assertEquals(Experiment.SaveButtonPosition.Variant.TREATMENT, result)
        assertEquals("TREATMENT", localDataSource.getAssignment(experimentKey))
        assertEquals("TREATMENT", memoryDataSource.getAssignment(experimentKey))
    }

    @Test
    fun `리모트가 null을 반환하면 기본값을 사용한다`() = runTest {
        // Given
        val experimentKey = Experiment.SaveButtonPosition.key

        // When
        val result = repository.getVariant(Experiment.SaveButtonPosition)

        // Then
        assertEquals(Experiment.SaveButtonPosition.Variant.CONTROL, result)
        assertEquals("CONTROL", localDataSource.getAssignment(experimentKey))
        assertEquals("CONTROL", memoryDataSource.getAssignment(experimentKey))
    }

    @Test
    fun `fetchAndAssignExperiments는 로컬과 메모리 모두에 저장한다`() = runTest {
        // Given
        val experimentKey = Experiment.SaveButtonPosition.key
        remoteDataSource.setRemoteVariant(experimentKey, "TREATMENT")

        // When
        repository.fetchAndAssignExperiments()

        // Then
        assertEquals("TREATMENT", localDataSource.getAssignment(experimentKey))
        assertEquals("TREATMENT", memoryDataSource.getAssignment(experimentKey))
    }

    @Test
    fun `fetchAndAssignExperiments는 리모트가 null이면 기본값을 사용한다`() = runTest {
        // Given
        val experimentKey = Experiment.SaveButtonPosition.key

        // When
        repository.fetchAndAssignExperiments()

        // Then
        assertEquals("CONTROL", localDataSource.getAssignment(experimentKey))
        assertEquals("CONTROL", memoryDataSource.getAssignment(experimentKey))
    }

    @Test
    fun `fetchAndAssignExperiments는 리모트 값으로 로컬과 메모리를 갱신한다`() = runTest {
        // Given
        val experimentKey = Experiment.SaveButtonPosition.key
        localDataSource.saveAssignment(experimentKey, "CONTROL")
        remoteDataSource.setRemoteVariant(experimentKey, "TREATMENT")

        // When
        repository.fetchAndAssignExperiments()

        // Then
        assertEquals("TREATMENT", localDataSource.getAssignment(experimentKey))
        assertEquals("TREATMENT", memoryDataSource.getAssignment(experimentKey))
    }

    @Test
    fun `메모리 캐시가 다른 모든 소스보다 우선순위를 가진다`() = runTest {
        // Given
        val experimentKey = Experiment.SaveButtonPosition.key
        memoryDataSource.saveAssignment(experimentKey, "TREATMENT")
        localDataSource.saveAssignment(experimentKey, "CONTROL")
        remoteDataSource.setRemoteVariant(experimentKey, "TREATMENT")

        // When
        val result = repository.getVariant(Experiment.SaveButtonPosition)

        // Then
        assertEquals(Experiment.SaveButtonPosition.Variant.TREATMENT, result)
        assertEquals(0, localDataSource.getAssignmentCallCount)
        assertEquals(0, remoteDataSource.fetchCallCount)
    }
}

// Fake implementations for testing
class FakeExperimentMemoryDataSource : ExperimentMemoryDataSource {
    private val storage = mutableMapOf<String, String>()
    var getAssignmentCallCount = 0

    override fun getAssignment(experimentKey: String): String? {
        getAssignmentCallCount++
        return storage[experimentKey]
    }

    override fun saveAssignment(experimentKey: String, variantName: String) {
        storage[experimentKey] = variantName
    }
}

class FakeExperimentLocalDataSource : ExperimentLocalDataSource {
    private val storage = mutableMapOf<String, String>()
    var getAssignmentCallCount = 0

    override suspend fun getAssignment(experimentKey: String): String? {
        getAssignmentCallCount++
        return storage[experimentKey]
    }

    override suspend fun saveAssignment(experimentKey: String, variantName: String) {
        storage[experimentKey] = variantName
    }
}

class FakeExperimentRemoteDataSource : ExperimentRemoteDataSource {
    private val storage = mutableMapOf<String, String>()
    var fetchCallCount = 0

    override suspend fun fetchAllExperimentVariants(experimentKeys: List<String>): Map<String, String> {
        fetchCallCount++
        return experimentKeys.associateWith { storage[it] }.filterValues { it != null }
            .mapValues { it.value!! }
    }

    override suspend fun getExperimentVariant(experimentKey: String): String? {
        return storage[experimentKey]
    }

    fun setRemoteVariant(key: String, variant: String) {
        storage[key] = variant
    }
}
