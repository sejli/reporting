/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package com.amazon.opendistroforelasticsearch.reportsscheduler.model

import com.amazon.opendistroforelasticsearch.reportsscheduler.ReportsSchedulerPlugin.Companion.LOG_PREFIX
import com.amazon.opendistroforelasticsearch.reportsscheduler.model.RestTag.REPORT_INSTANCE_LIST_FIELD
import com.amazon.opendistroforelasticsearch.reportsscheduler.util.createJsonParser
import com.amazon.opendistroforelasticsearch.reportsscheduler.util.logger
import org.elasticsearch.action.ActionResponse
import org.elasticsearch.common.io.stream.StreamInput
import org.elasticsearch.common.io.stream.StreamOutput
import org.elasticsearch.common.xcontent.ToXContent
import org.elasticsearch.common.xcontent.ToXContentObject
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.common.xcontent.XContentParser
import org.elasticsearch.common.xcontent.XContentParser.Token
import org.elasticsearch.common.xcontent.XContentParserUtils
import java.io.IOException

/**
 * Get all report instances info response.
 * <pre> JSON format
 * {@code
 * {
 *   "reportInstanceList":[
 *      // refer [com.amazon.opendistroforelasticsearch.reportsscheduler.model.ReportInstance]
 *   ]
 * }
 * }</pre>
 */
internal class GetAllReportInstancesResponse : ActionResponse, ToXContentObject {
    val reportInstanceList: List<ReportInstance>

    constructor(reportInstanceList: List<ReportInstance>) : super() {
        this.reportInstanceList = reportInstanceList
    }

    @Throws(IOException::class)
    constructor(input: StreamInput) : this(input.createJsonParser())

    /**
     * Parse the data from parser and create [GetAllReportInstancesResponse] object
     * @param parser data referenced at parser
     */
    constructor(parser: XContentParser) : super() {
        var reportInstanceList: List<ReportInstance>? = null
        XContentParserUtils.ensureExpectedToken(Token.START_OBJECT, parser.currentToken(), parser::getTokenLocation)
        while (Token.END_OBJECT != parser.nextToken()) {
            val fieldName = parser.currentName()
            parser.nextToken()
            when (fieldName) {
                REPORT_INSTANCE_LIST_FIELD -> reportInstanceList = parseReportInstanceList(parser)
                else -> {
                    parser.skipChildren()
                    log.info("$LOG_PREFIX:Skipping Unknown field $fieldName")
                }
            }
        }
        reportInstanceList ?: throw IllegalArgumentException("$REPORT_INSTANCE_LIST_FIELD field absent")
        this.reportInstanceList = reportInstanceList
    }

    companion object {
        private val log by logger(GetAllReportInstancesResponse::class.java)

        /**
         * Parse the report instance list from parser
         * @param parser data referenced at parser
         * @return created list of ReportInstance
         */
        private fun parseReportInstanceList(parser: XContentParser): List<ReportInstance> {
            val retList: MutableList<ReportInstance> = mutableListOf()
            XContentParserUtils.ensureExpectedToken(Token.START_ARRAY, parser.currentToken(), parser::getTokenLocation)
            while (parser.nextToken() != Token.END_ARRAY) {
                retList.add(ReportInstance.parse(parser))
            }
            return retList
        }
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun writeTo(output: StreamOutput) {
        toXContent(XContentFactory.jsonBuilder(output), ToXContent.EMPTY_PARAMS)
    }

    /**
     * {@inheritDoc}
     */
    fun toXContent(): XContentBuilder {
        return toXContent(XContentFactory.jsonBuilder(), ToXContent.EMPTY_PARAMS)
    }

    /**
     * {@inheritDoc}
     */
    override fun toXContent(builder: XContentBuilder?, params: ToXContent.Params?): XContentBuilder {
        builder!!.startObject()
            .startArray(REPORT_INSTANCE_LIST_FIELD)
        reportInstanceList.forEach { it.toXContent(builder, ToXContent.EMPTY_PARAMS, true) }
        return builder.endArray().endObject()
    }
}
