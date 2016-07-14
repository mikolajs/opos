/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   License GNU AFFERO GENERAL PUBLIC LICENS Version 3, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.osp.model

import _root_.net.liftweb.http.S
import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import Mailer._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name

  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
    locale, timezone, password, role)

  override def screenWrap = Full(<lift:surround with="user_mgt" at="content">
    <lift:bind/>
  </lift:surround>)

  // comment this line out to require email validations
  // override def skipEmailValidation = false

  override def createUserMenuLoc = Empty

  override def editUserMenuLoc = Empty


  // override def afterValidation  = List( (user) => { S.redirectTo("/user_mgt/change_password")})

}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User

  // what's the "meta" server

  object role extends MappedString(this, 1)

  object scratched extends MappedBoolean(this) {
    override def defaultValue = false
  }

  object phone extends MappedString(this, 12)
  //to delete - use firstnames
  object secondName extends MappedString(this, 30)
  //to repleace by pesel
  object birthDate extends MappedDate(this)

  object birthPlace extends MappedString(this, 30)

  object birthDisctrict extends MappedString(this, 40)

  object address extends MappedText(this)

  object pesel extends MappedString(this, 11)

  object classId extends MappedLongForeignKey(this, ClassModel);

  object classInfo extends MappedString(this, 3) {
    override def defaultValue = "-"
  }

  object classNumber extends MappedInt(this)

  object father extends MappedLongForeignKey(this, User)

  object mather extends MappedLongForeignKey(this, User)

  def getFullName = firstName.is + " " + lastName.is

  def getFullNameReverse = lastName.is + " " + firstName.is

  def shortInfo = getFullName + " [" + id.is.toString + "]"

  def isAdmin_? = this.role.get == "a"
  def isTeacher_? = this.role.get == "n" || this.role.get == 's' || this.role.get == "d"
  def isSecretary_? = this.role.get == "s" || this.role.get == 'd'
  def isDirector_? = this.role.get == 'd'
}
