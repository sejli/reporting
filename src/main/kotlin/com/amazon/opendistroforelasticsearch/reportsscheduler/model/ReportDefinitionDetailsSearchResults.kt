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

import com.amazon.opendistroforelasticsearch.reportsscheduler.model.RestTag.REPORT_DEFINITION_LIST_FIELD
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.common.xcontent.ToXContent
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentParser

/**
 * ReportDefinitions search results
 */
internal class ReportDefinitionDetailsSearchResults : SearchResults<ReportDefinitionDetails> {
    constructor(parser: XContentParser) : super(parser, REPORT_DEFINITION_LIST_FIELD)

    constructor(from: Long, response: SearchResponse) : super(from, response, REPORT_DEFINITION_LIST_FIELD)

    /**
     * {@inheritDoc}
     */
    override fun parseItem(parser: XContentParser, useId: String?): ReportDefinitionDetails {
        return ReportDefinitionDetails.parse(parser, useId)
    }

    /**
     * {@inheritDoc}
     */
    override fun itemToXContent(item: ReportDefinitionDetails, builder: XContentBuilder, params: ToXContent.Params): XContentBuilder {
        item.toXContent(builder, ToXContent.EMPTY_PARAMS, true)
        return builder
    }
}
