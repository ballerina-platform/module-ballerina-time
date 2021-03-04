// This file deals with absolute

public type Error distinct error;
public type Seconds decimal;

// UTC time

// should handle all RFC timestamps
// two-part structure especially to handles discontinuities,
// which are distinctive feature of UTC
# Point on UTC time-scale.
# This is represented by a tuple of length 2.
# The tuple is an ordered type and so the values can be
# compared using the Ballerina <, <=, >, >= operators.
# First member of tuple is int representing integral number of
# seconds from the epoch.
# Epoch is traditional Unix epoch of 1970-01-01T00:00:00Z
# Second member of tuple is decimal giving the fraction of
# a second.
# For times before the epoch, n is negative and f is
# non-negative. In other words, the UTC time represented
# is on or after the second specified by n.
# Leap seconds are handled as follows. The first member
# of the tuple ignores leap seconds: it assumes that every day
# has 86400 seconds. The second member of the tuple is >= 0.
# and is < 1 except during positive leaps seconds in which it
# is >= 1 and < 2. So given a tuple [n,f] after the epoch,
# n / 86400 gives the day number, and (n % 86400) + f gives the
# time in seconds since midnight UTC (for which the limit is
# 86401 on day with a positive leap second).
// XXX need to think about valid range for int
// should at least be able to represent
public type Utc readonly & [int,decimal];

# Returns Utc representing current time.
# `precision` specifies number of zeros after decimal point
# for seconds e.g. 3 would give millisecond precision
# nil means native precision of clock
public function utcNow(int? precision = ()) returns Utc = external;

# Returns UTC time represented by a string in RFC 3339 format.
public function utcFromString(string str) returns Utc|Error = external;
# Converts a UTC time to a string in RFC 3339 format.
public function utcToString(Utc utc) returns string = external;

# Returns Utc time that occurs seconds after `utc`.
# This assumes that all days have 86400 seconds, except when
# utc represents a time during a positive leap
# second, in which case the corresponding day will be assumed
# to have 86401 seconds.
public function utcAddSeconds(Utc utc, Seconds seconds) returns Utc = external;

# Returns difference in seconds between utc1 and utc2.
# This will be positive if utc1 occurs after utc2
# This assumes that all days have 86400 seconds, except when
# utc1 or utc2 represents a time during a positive leap
# second, in which case the corresponding day will be assumed
# to have 86401 seconds.
public function utcDiffSeconds(Utc utc1, Utc utc2) returns Seconds = external;

# Monotonic time

# Returns time in seconds since some epoch.
# The epoch is fixed when a Ballerina program is run.
# Different runs of a program may use different epochs.
# The values returned by monotonicNow during a run
# of a program will not decrease.
public function monotonicNow() returns Seconds = external;
