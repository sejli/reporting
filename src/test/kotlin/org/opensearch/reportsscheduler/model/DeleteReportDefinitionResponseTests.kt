/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.reportsscheduler.model

import com.fasterxml.jackson.core.JsonParseException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opensearch.commons.utils.recreateObject
import org.opensearch.reportsscheduler.createObjectFromJsonString
import org.opensearch.reportsscheduler.getJsonString

internal class DeleteReportDefinitionResponseTests {

    @Test
    fun `Delete response serialize and deserialize transport object should be equal`() {
        val deleteRequest = DeleteReportDefinitionResponse("sample_report_definition_id")
        val recreatedObject = recreateObject(deleteRequest) { DeleteReportDefinitionResponse(it) }
        assertEquals(deleteRequest.reportDefinitionId, recreatedObject.reportDefinitionId)
    }

    @Test
    fun `Delete response serialize and deserialize using json object should be equal`() {
        val deleteRequest = DeleteReportDefinitionResponse("sample_report_definition_id")
        val jsonString = getJsonString(deleteRequest)
        val recreatedObject = createObjectFromJsonString(jsonString) { DeleteReportDefinitionResponse.parse(it) }
        assertEquals(deleteRequest.reportDefinitionId, recreatedObject.reportDefinitionId)
    }

    @Test
    fun `Delete response should deserialize json object using parser`() {
        val reportDefinitionId = "sample_report_definition_id"
        val jsonString = """
        {
            "reportDefinitionId":"$reportDefinitionId"
        }
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { DeleteReportDefinitionResponse.parse(it) }
        assertEquals(reportDefinitionId, recreatedObject.reportDefinitionId)
    }

    @Test
    fun `Delete response should throw exception when invalid json object is passed`() {
        val jsonString = "sample message"
        assertThrows<JsonParseException> {
            createObjectFromJsonString(jsonString) { DeleteReportDefinitionResponse.parse(it) }
        }
    }

    @Test
    fun `Delete response should safely ignore extra field in json object`() {
        val reportDefinitionId = "sample_report_definition_id"
        val jsonString = """
        {
            "reportDefinitionId":"$reportDefinitionId",
            "extra_field_1":["extra", "value"],
            "extra_field_2":{"extra":"value"},
            "extra_field_3":"extra value 3"
        }
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { DeleteReportDefinitionResponse.parse(it) }
        assertEquals(reportDefinitionId, recreatedObject.reportDefinitionId)
    }
}
