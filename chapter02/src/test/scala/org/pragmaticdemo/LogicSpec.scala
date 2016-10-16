package org.pragmaticdemo

import org.specs2.mutable.Specification

object LogicSpec extends Specification {
  "The 'matchLikelihood' method" should {
    "be 100% when all attributes match" in {
      val glutter = Sku(1, List("roofing", "glutter"))
      val prefs = BuyerPreferences(List("roofing", "glutter"))
      Logic.matchLikelihood(glutter, prefs) must beGreaterThan(0.999)
    }
    "be 0% when no attributes match" in {
      val glutter = Sku(1, List("roofing", "glutter"))
      val prefs = BuyerPreferences(List("electrical", "wiring"))
      val result = Logic.matchLikelihood(glutter, prefs)
      result must beLessThan(0.001)
    }
    "correctly handle an empty BuyerPreferences" in {
      val glutter = Sku(1, List("roofing", "glutter"))
      val prefs = BuyerPreferences(List())
      val result = Logic.matchLikelihood(glutter, prefs)
      result.isNaN mustEqual false
    }
  }
}
