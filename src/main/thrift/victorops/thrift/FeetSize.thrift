namespace java  victorops.thrift
#@namespace scala victorops.thrift.scala

include "Common.thrift"

struct FeetSize {
    1: double leftFoot,
    2: double rightFoot,
}