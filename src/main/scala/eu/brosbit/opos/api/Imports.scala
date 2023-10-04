package eu.brosbit.opos.api

import net.liftweb.common.{Box, Full}
import net.liftweb.http.{InMemoryResponse, LiftResponse, NotFoundResponse}

//NOT USED!
object Imports {

  def importing() : Box[LiftResponse] = {
    if(true)  Full(NotFoundResponse("Not found"))
    else
      Full(InMemoryResponse(new Array[Byte](0), ("Content-Type", "application/zip") :: ("Content-Disposition", "form-data; filename=\"export.zip\"") :: Nil, Nil, 200))
  }

}
