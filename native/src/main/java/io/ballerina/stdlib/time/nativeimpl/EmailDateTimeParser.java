/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses RFC 5322 date-times, including the obsolete forms of RFC 5322 section 4.3.
 *
 * <p>The input is tokenised and rewritten into the canonical form
 * {@code [EEE, ]d MMM yyyy HH:mm[:ss] +HHMM[ (zone)]} before it is parsed. Parse errors refer to the caller's
 * original text and positions.
 *
 * @since 2.8.5
 */
final class EmailDateTimeParser {

    private static final DateTimeFormatter CANONICAL_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .optionalStart().appendPattern("EEE, ").optionalEnd()
            .appendPattern("d MMM yyyy HH:mm")
            .optionalStart().appendPattern(":ss").optionalEnd()
            .appendPattern(" Z[ ][(z)]")
            .toFormatter(Locale.ENGLISH);
    // Possessive quantifiers keep matching linear in the input length.
    private static final Pattern DATE_TIME = Pattern.compile(
            "\\s*+(?:([A-Za-z]{3})\\s*+,\\s*+)?(\\d{1,2})\\s++([A-Za-z]{3})\\s++(\\d{2,4})\\s++" +
                    "(\\d{2}:\\d{2}(?::\\d{2})?)\\s++([+-]\\d{4}|[A-Za-z]{1,3})(?:\\s*+\\(([^()]*+)\\))?\\s*+");
    private static final Pattern TRAILING_INDEX = Pattern.compile("index \\d+$");
    // Military zones other than Z are also read as -0000, as RFC 5322 section 4.3 recommends.
    private static final Pattern MILITARY_ZONE = Pattern.compile("[A-IK-Z]");
    private static final Map<String, String> OBSOLETE_ZONE_OFFSETS = Map.of(
            "UT", "+0000", "GMT", "+0000", "EST", "-0500", "EDT", "-0400", "CST", "-0600", "CDT", "-0500",
            "MST", "-0700", "MDT", "-0600", "PST", "-0800", "PDT", "-0700");
    private static final int MAX_QUOTED_LENGTH = 64;

    private EmailDateTimeParser() {
    }

    static ZonedDateTime parse(String text) {

        Matcher matcher = DATE_TIME.matcher(text);
        if (!matcher.matches()) {
            // Not an RFC 5322 date-time; parsing it as-is reports the error position in the caller's text.
            return ZonedDateTime.parse(text, CANONICAL_FORMATTER);
        }
        CanonicalText withComment = CanonicalText.of(matcher, true);
        try {
            return ZonedDateTime.parse(withComment.text(), CANONICAL_FORMATTER);
        } catch (DateTimeParseException e) {
            if (matcher.group(7) == null) {
                throw withComment.toOriginal(e, text);
            }
        }
        // A comment that is not a zone name is free text, which RFC 5322 allows.
        CanonicalText withoutComment = CanonicalText.of(matcher, false);
        try {
            return ZonedDateTime.parse(withoutComment.text(), CANONICAL_FORMATTER);
        } catch (DateTimeParseException e) {
            throw withoutComment.toOriginal(e, text);
        }
    }

    private static String quote(String text) {

        return "Text '" + (text.length() > MAX_QUOTED_LENGTH ? text.substring(0, MAX_QUOTED_LENGTH) + "..." : text) +
                "'";
    }

    private static final class CanonicalText {

        private final StringBuilder text = new StringBuilder();
        private final List<int[]> tokens = new ArrayList<>();

        static CanonicalText of(Matcher matcher, boolean includeComment) {

            CanonicalText canonical = new CanonicalText();
            if (matcher.group(1) != null) {
                canonical.append(matcher.group(1), matcher.start(1), matcher.end(1)).text.append(", ");
            }
            canonical.append(matcher.group(2), matcher.start(2), matcher.end(2)).text.append(' ');
            canonical.append(matcher.group(3), matcher.start(3), matcher.end(3)).text.append(' ');
            canonical.append(expandYear(matcher.group(4)), matcher.start(4), matcher.end(4)).text.append(' ');
            canonical.append(matcher.group(5), matcher.start(5), matcher.end(5)).text.append(' ');
            canonical.append(toOffset(matcher.group(6)), matcher.start(6), matcher.end(6));
            if (includeComment && matcher.group(7) != null) {
                canonical.text.append(' ');
                canonical.append("(" + matcher.group(7).strip() + ")", matcher.start(7) - 1, matcher.end(7) + 1);
            }
            return canonical;
        }

        String text() {

            return text.toString();
        }

        DateTimeParseException toOriginal(DateTimeParseException e, String original) {

            String prefix = quote(text());
            if (!e.getMessage().startsWith(prefix)) {
                return e;
            }
            int index = toOriginalIndex(e.getErrorIndex());
            String detail = TRAILING_INDEX.matcher(e.getMessage().substring(prefix.length()))
                    .replaceFirst("index " + index);
            return new DateTimeParseException(quote(original) + detail, original, index, e);
        }

        private CanonicalText append(String token, int originalStart, int originalEnd) {

            tokens.add(new int[]{text.length(), originalStart, originalEnd - originalStart});
            text.append(token);
            return this;
        }

        private int toOriginalIndex(int canonicalIndex) {

            int[] containing = tokens.get(0);
            for (int[] token : tokens) {
                if (token[0] <= canonicalIndex) {
                    containing = token;
                }
            }
            return containing[1] + Math.min(Math.max(canonicalIndex - containing[0], 0), containing[2]);
        }

        // RFC 5322 section 4.3: a two-digit year below 50 is in the 2000s; other short years are offset from 1900.
        private static String expandYear(String year) {

            if (year.length() == 4) {
                return year;
            }
            int value = Integer.parseInt(year);
            return String.valueOf(year.length() == 2 && value < 50 ? 2000 + value : 1900 + value);
        }

        private static String toOffset(String zone) {

            if (zone.charAt(0) == '+' || zone.charAt(0) == '-') {
                return zone;
            }
            String upperCaseZone = zone.toUpperCase(Locale.ROOT);
            String offset = OBSOLETE_ZONE_OFFSETS.get(upperCaseZone);
            if (offset != null) {
                return offset;
            }
            return MILITARY_ZONE.matcher(upperCaseZone).matches() ? "+0000" : zone;
        }
    }
}
