package org.pragmaticdemo.database

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Sku(id: Long, name: String)
case class SelectSku(select1: String, select2: String, select3: String)
case class Attribute(id: Long, label: String)
case class SkuAttribute(id: Long, skuId: Long, attributeId: Long)

object Sku {
  val sku = {
    get[Long]("id") ~ 
    get[String]("name") map {
      case id~name => Sku(id, name)
    }
  }

  def all(): List[Sku] = DB.withConnection { implicit c =>
    SQL("select * from sku").as(kitten *)
  }

  def create(name: String) {
    DB.withConnection { implicit c =>
      SQL("insert into sku (name) values ({name})").on(
        'name -> name
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from sku where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }
  
}

object Attribute {
  val attribute = {
    get[Long]("id") ~ 
    get[String]("label") map {
      case id~label => Attribute(id, label)
    }
  }

  def all(): List[Attribute] = DB.withConnection { implicit c =>
    SQL("select * from attribute").as(attribute *)
  }

  def allForSelect(): Seq[(String, String)] = all().sortBy(_.label).map(a => (a.id + "", a.label))
}


object SkuAttribute {
  val skuAttribute = {
    get[Long]("id") ~ 
    get[Long]("sku_id") ~ 
    get[Long]("attribute_id") map {
      case id~skuId~attributeId => SkuAttribute(id, kittenId, attributeId)
    }
  }

  def all(): List[SkuAttribute] = DB.withConnection { implicit c =>
    SQL("select * from sku_attribute").as(kittenAttribute *)
  }

  def allForSku(sku: Kitten): Seq[KittenAttribute] = all().filter(ka => ka.kittenId == kitten.id)
}
