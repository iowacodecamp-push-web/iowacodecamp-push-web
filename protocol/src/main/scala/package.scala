package code

package object protocol {
  type CaseClass = Object with Product
  type TwoPartMessage = Pair[String, Array[Byte]]
}
