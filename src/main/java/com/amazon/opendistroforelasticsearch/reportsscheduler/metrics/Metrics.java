/*
 *   Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package com.amazon.opendistroforelasticsearch.reportsscheduler.metrics;

import org.elasticsearch.rest.RestRequest;
import org.json.JSONObject;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.amazon.opendistroforelasticsearch.reportsscheduler.model.RestTag.STATS_START_TIME;
import static com.amazon.opendistroforelasticsearch.reportsscheduler.model.RestTag.STATS_END_TIME;

// import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;

public class Metrics {

    private static Metrics metrics = new Metrics();
    private ConcurrentHashMap<String, Metric<?>> registeredMetricsByName = new ConcurrentHashMap<>();
    private final Clock clock = Clock.systemDefaultZone();

    private Metrics() {
    }

    public void registerDefaultMetrics() {
        for (MetricName metricName : MetricName.values()) {
            registerMetric(MetricFactory.createMetric(metricName));
        }
    }

    public void registerMetric(Metric<?> metric) {
        registeredMetricsByName.put(metric.getName(), metric);
    }

    public void unregisterMetric(String name) {
        if (name == null) {
            return;
        }

        registeredMetricsByName.remove(name);
    }

    public Metric<?> getMetric(String name) {
        if (name == null) {
            return null;
        }

        return registeredMetricsByName.get(name);
    }

    public NumericMetric<?> getNumericalMetric(MetricName metricName) {
        String name = metricName.getName();
        if (!metricName.isNumerical()) {
            name = MetricName.DEFAULT.getName();
        }

        return (NumericMetric) registeredMetricsByName.get(name);
    }

    public List<Metric<?>> getAllMetrics() {
        return new ArrayList<>(registeredMetricsByName.values());
    }

    public static Metrics getInstance() {
        return metrics;
    }

    public String collectToJSON() {
        JSONObject metricsJSONObject = new JSONObject();

        for (Metric<?> metric : registeredMetricsByName.values()) {
            if (metric.getName().equals("default")) {
                continue;
            }
            metricsJSONObject.put(metric.getName(), metric.getValue());
        }

        metricsJSONObject.put("time", clock.millis());

        return metricsJSONObject.toString();
    }

    public String collectToFlattenedJSON() {
        String flattenedJson = "{\"report_definition.create.count\":2,\"report_definition.create.total\":4," +
            "\"report_definition.create.system_error\":2,\"report_definition.create.customer_error\":3," +
            "\"report_definition.list.count\":2,\"report_definition.list.total\":4," +
            "\"report_definition.list.system_error\":2,\"report_definition.list.customer_error\":3," +
            "\"report_instance.create.count\":2,\"report_instance.create.total\":4," +
            "\"report_instance.create.system_error\":2,\"report_instance.create.customer_error\":3}";
        String nestedJson = "{\"report_definition\":{\"create\":{\"count\":2," +
            "\"total\":4,\"system_error\":2,\"customer_error\":3},\"list\":" +
            "{\"count\":2,\"total\":4,\"system_error\":2,\"customer_error\":3}}," +
            "\"report_instance\":{\"create\":{\"count\":2,\"total\":4,\"system_error\":2," +
            "\"customer_error\":3}}}";
        String metricsJson = JsonUnflattener.unflatten(collectToJSON());
        return metricsJson;
    }


    public String requestString(RestRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("path" , request.path());
        map.put("rawPath", request.rawPath());
        map.put("uri" , request.uri());
        return map.toString();
    }

    public String test(RestRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("startTime" , request.param(STATS_START_TIME, "0000-00-00T00:00:00"));
        map.put("endTime" , request.param(STATS_END_TIME, "0000-00-00T00:00:00"));
        return map.toString();
    }



    public void clear() {
        registeredMetricsByName.clear();
    }
}
