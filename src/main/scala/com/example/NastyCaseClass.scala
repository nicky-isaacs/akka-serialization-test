package com.example

case class NastyCaseClass(
                           firstName: String,
                           lastName: String,
                           hasMustache: Boolean,
                           mustacheColor: Option[Color],
                           footSizes: FeetSize,
                           listOfFriends: Seq[String]
                           )