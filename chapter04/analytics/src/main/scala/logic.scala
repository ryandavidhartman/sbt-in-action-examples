package org.pragmaticdemo

object Logic {
  /** Determines the match likelihood and returns % match. */
  def matchLikelihood(sku: Sku, buyer: BuyerPreferences): Double = {
    val matches = buyer.attributes map { attribute => 
      sku.attributes contains attribute
    }
    val nums = matches map { b => if(b) 1.0 else 0.0 }
    nums.sum / nums.length
  }
}
