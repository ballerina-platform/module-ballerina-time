// Civil time


# Date in proleptic Gregorian calendar.
type Date record {
  // year 1 means AD 1
  // year 0 means 1 BC
  // year -1 means 2 BC
  int year;
  # month 1 is January, as in ISO 8601
  int month;
  # day 1 is first day of month
  int day;
};

# Validate a `Date`.
# Checks that days and months are within range
# per Gregorian calendar rules
public function dateValidate(Date date) returns Error? {
}

// Replace this with a union of consts.
// Need to decide whether SUNDAY or MONDAY is first day of week
// ISO (and logic) says Monday; US convention is Sunday
public type DayOfWeek int;

// panic if date is not valid
public function dayOfWeek(Date date) returns DayOfWeek {
  checkpanic dateValidate(date);
  // XXX implement this
  return 0;
}

# Time within a day
# Not always duration from midnight, 
public type TimeOfDay record {
  // this is "hour" not "hours" because
  // consistency with year/month/day
  // it is not the same as hours from midnight for a local time 
  // because of daylight savings time discontinuities
  int hour;
  int minute;
  // it is very common for seconds to not be specified
  // Should this be "seconds"?
  Seconds second?;
};

// This is closed so it is a subtype of Delta
// Fields can negative
// if any of the three fields are > 0, then all must be >= 0
// if any of the three fields are < 0, then all must be <= 0
// Semantic is that durations should be left out
public type ZoneOffset readonly & record {|
  int hours;
  int minutes = 0;
  # IETF zone files have historical zones that are offset by
  # integer seconds; we use Seconds type so that this is a subtype
  # of Delta
  Seconds seconds?;
|};

// Compiler does not support const records, it seems
// public const ZoneOffset Z = { hours: 0 };

public type ZERO_OR_ONE 0|1;

# Time within some region relative to a
# time scale stipulated by civilian authorities
// This is relatively loose type;
// we can have other types that are tighter.
// Similar to struct tm in C.
// Module is called time so this is time:Civil
public type Civil record {
  // the date time in that region
  *Date;
  *TimeOfDay;
  // offset of the date time in that region at that time
  // from Utc
  // positive means the local time is ahead of UTC
  ZoneOffset utcOffset?;
  
  # if present, abbreviation for the local time (e.g. EDT, EST)
  # in effect at the time represented by this record;
  # this is quite the same as the name of a time zone
  # one time zone can have two abbreviations: one for
  # standard time and one for daylight savings time
  string timeAbbrev?;
  // when the clocks are put back at the end of DST,
  // one hour's worth of times occur twice
  // i.e. the local time is ambiguous
  // this says which of those two times is meant
  // same as fold field in Python
  // see https://www.python.org/dev/peps/pep-0495/
  // is_dst has similar role in struct tm,
  // but with confusing semantics
  ZERO_OR_ONE which?;
};


public function utcToCivil(Utc utc) returns Civil = external;
// error if civilTime.utcOffset is missing
public function utcFromCivil(Civil civilTime) returns Utc|Error = external;

// The string format used by civilFromString and civilToString
// is ISO 8601 but with more flexibility that RFC 3339 as follows:
// missing utcOffset field represented by missing time zone offset
// missing seconds in time represented by missing second
// field in TimeOfDay

public function civilFromString(string str) returns Utc|Error = external;
public function civilToString(Civil civilTime) returns string = external;

// XXX also function to return Seconds since midnight

