package org.pragmaticdemo

import org.specs2.mutable.Specification

object LogicSpec extends Specification {
  "The 'matchLikelihood' method" should {
    "be 100% when all attributes match" in {
      val glutter = Sku(1, Set("roofing", "glutter"))
      val prefs = BuyerPreferences(Set("roofing", "glutter"))
      Logic.matchLikelihood(glutter, prefs) must beGreaterThan(0.999)
    }
    "be 100% when all attributes match (with duplicates)" in {
      val glutter = Sku(1, Set("roofing", "glutter", "male"))
      val prefs = BuyerPreferences(Set("roofing", "glutter", "glutter"))
      Logic.matchLikelihood(glutter, prefs) must beGreaterThan(0.999)
    }
    "be 0% when no attributes match" in {
      val glutter = Sku(1, Set("roofing", "glutter"))
      val prefs = BuyerPreferences(Set("electrical", "wiring"))
      val result = Logic.matchLikelihood(glutter, prefs) 
      result must beLessThan(0.001)
    }
    "be 66% when two from three attributes match" in {
      val glutter = Sku(1, Set("electrical", "wiring", "thin"))
      val prefs = BuyerPreferences(Set("electrical", "wiring", "overweight"))
      val result = Logic.matchLikelihood(glutter, prefs) 
      result must beBetween(0.66, 0.67)
    }
  }
}
