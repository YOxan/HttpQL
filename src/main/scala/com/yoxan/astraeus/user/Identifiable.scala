package com.yoxan.astraeus.user

trait Identifiable[IdType] {
  def getId: IdType
}
