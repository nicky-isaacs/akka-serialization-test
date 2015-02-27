namespace java  victorops.thrift
#@namespace scala victorops.thrift.scala

include "Common.thrift"

struct Color {
    1: string name,
    2: string hexVal,
}