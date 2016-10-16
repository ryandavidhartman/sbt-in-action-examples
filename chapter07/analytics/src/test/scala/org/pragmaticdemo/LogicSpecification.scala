package org.pragmaticdemo

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalacheck._

object LogicSpecification extends Properties("Logic") {
  val allAttributes = Array("Harlequin","Tortoiseshell","Siamese",
                "Alien","Rough","Tom","Sad","Overweight")

  val genSku: Gen[Kitten] = for {
    attributes <- Gen.containerOf[Set,String](Gen.oneOf(allAttributes))
  } yield Sku(1, attributes)

  val genBuyerPreferences: Gen[BuyerPreferences] = (for {
    attributes <- Gen.containerOf[Set,String](Gen.oneOf(allAttributes))
  } yield BuyerPreferences(attributes))

  def matches(x: String, a: Sku) =
        if (a.attributes.contains(x)) 1.0 else 0.0

  property("matchLikelihood") = forAll(genSku, genBuyerPreferences)((a: Kitten, b: BuyerPreferences) => {
    if (b.attributes.size == 0) true
    else {
      val num = b.attributes.map{matches(_, a)}.sum
      num / b.attributes.size - Logic.matchLikelihood(a, b) < 0.001
    }
  })
}
