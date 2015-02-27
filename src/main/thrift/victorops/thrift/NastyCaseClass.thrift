namespace java  victorops.thrift
#@namespace scala victorops.thrift.scala

include "Common.thrift"
include "Color.thrift"
include "FeetSize.thrift"

typedef list<string> friendList

struct NastyCaseClass {
    1: string firstName,
    2: string lastName,
    3: bool hasMustache,
    4: optional Color.Color color,
    5: FeetSize.FeetSize footSize,
    6: friendList listOfFriends,
    7: bool knowsDan
}