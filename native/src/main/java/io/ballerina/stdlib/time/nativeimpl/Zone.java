/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.stdlib.time.nativeimpl;

import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.stdlib.time.util.Utils;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Contains the APIs related to Ballerina TimeZone type generations and operations.
 *
 * @since 2.0.2
 */
public class Zone {

    private ZoneId zoneId;

    public Zone() throws DateTimeException {

        zoneId = ZoneId.systemDefault();
    }

    public Zone(String zoneId) {

        this.zoneId = ZoneId.of(zoneId);
    }

    public Object isFixedOffset() {

        if (zoneId.getRules().isFixedOffset()) {
            Map<String, Integer> zoneInfo = Utils.zoneOffsetMapFromString(
                    zoneId.getRules().getOffset(Instant.now()).toString());
            return Utils.createZoneOffsetFromZoneInfoMap(zoneInfo);
        }
        return null;
    }

    public Utc utcFromCivil(Civil civil) {

        return new Utc(civil.getZonedDateTime().toInstant());
    }

    /**
     * Resolves a local date-time in this zone. A given UTC offset must be valid for the local date-time and
     * selects the occurrence when the local time occurs twice; without it, {@code laterOffsetAtOverlap} selects
     * it. A local time in a gap is moved forward by the length of the gap, as {@link ZonedDateTime#ofLocal} does.
     */
    public Utc utcFromLocalDateTime(LocalDateTime localDateTime, ZoneOffset utcOffset, boolean laterOffsetAtOverlap) {

        if (utcOffset == null) {
            ZonedDateTime zonedDateTime = ZonedDateTime.ofLocal(localDateTime, zoneId, null);
            return new Utc((laterOffsetAtOverlap ? zonedDateTime.withLaterOffsetAtOverlap() : zonedDateTime)
                    .toInstant());
        }
        if (!zoneId.getRules().isValidOffset(localDateTime, utcOffset)) {
            String offset = utcOffset.getTotalSeconds() == 0 ? "+00:00" : utcOffset.getId();
            throw new DateTimeException("UTC offset " + offset + " is not valid for " + localDateTime + " in " +
                    zoneId);
        }
        return new Utc(ZonedDateTime.ofLocal(localDateTime, zoneId, utcOffset).toInstant());
    }

    public Civil utcToCivil(Utc utc) {

        return new Civil(utc.generateInstant().atZone(zoneId));
    }

    public BMap<BString, Object> civilAddDuration(Civil civil, CustomDuration customDuration) {
        return civil.addDuration(this.zoneId, customDuration).build();
    }
}
