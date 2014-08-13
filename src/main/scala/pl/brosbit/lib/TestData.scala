package pl.brosbit.lib

import java.util.Date
import net.liftweb.mapper.{ By, OrderBy, Ascending }
import  net.liftweb.util.Helpers._
import pl.brosbit.model._
import org.bson.types.ObjectId
import pl.brosbit.model.page._

class TestData {
    
    
    def createData() {
        createTeachers
        createClasses
        createPupils
        createSubjects
        createOneMarkLine
        createNewses
    }
    
    
    def createTeachers {
        for(i <- 1 to 30) {
            User.create.address(randomName(10)).phone(randomNumbers(9)).firstName(randomName(9)).secondName(randomName(9)).
            	role("n").validated(true).lastName(randomName(9)).save
        }
        
    }
    
    def createClasses() {
        var dep = Array("A","B", "C","D")
        var teachers = User.findAll(By(User.role, "n"))
        for(i <- 0 to 11) {
            ClassModel.create.level( (i / 4) + 1 ).descript(randomName(7)).division(dep(i  % 4)).teacher(teachers.head.id.is).save
            teachers = teachers.drop(2)
        }
    }
    
    def createPupils(){
        ClassModel.findAll.map(cl => {
            for(i <- 1 to 20) {
                val dateL = (new Date()).getTime
                val  lastName = randomName(9)
                val address = randomName(10) + " " + randomInt(150).toString + "  80-"+ randomNumbers(3)+ "  Gdańsk"
                 val father =   User.create.firstName(randomName(9)).secondName(randomName(9)).lastName(lastName).birthDisctrict("pomorskie").
                address(address).birthPlace("Gdańsk").pesel(randomNumbers(11)).phone(randomNumbers(9)).email(randomString(10)+ "@edu.org").
               birthDate(new Date(dateL -  1500000000000L - randomLong(10000000000L))).role("r")
                father.save
                
                val mather  =   User.create.firstName(randomName(9)).secondName(randomName(9)).lastName(lastName).birthDisctrict("pomorskie").
                address(address).birthPlace("Gdańsk").pesel(randomNumbers(11)).phone(randomNumbers(9)).email(randomString(10)+ "@edu.org").
                birthDate(new Date(dateL -  1500000000000L - randomLong(10000000000L))).role("r")
                mather.save
                
                User.create.firstName(randomName(9)).secondName(randomName(9)).lastName(lastName).birthDisctrict("pomorskie").classNumber(i).
                address(address).birthPlace("Gdańsk").pesel(randomNumbers(11)).phone(randomNumbers(9)).email(randomString(10)+ "@edu.org").
                classId(cl.id.is).classInfo(cl.classString).birthDate(new Date(dateL -  500000000000L - randomLong(10000000000L))).role("u").father(father.id.is).mather(mather.id.is).save
                
               
            }     
        })
    }
    
    def createSubjects(){
        val subjects = List(("język polski", "j.pol"),  ("język angielski", "j.ang"), ("język francuzki", "j.fra"), ("język niemiecki", "j.nie"), ("historia", "hist"),
                ("WOS", "WOS"), ("Przysposobienie obronne", "PO"), ("geografia", "geog"), ("chemia", "chem"), ("fizyka", "fiz"), ("matematyka", "mat"), ("biologia", "biol"),
                ("muzyka", "muz"), ("technika", "tech"), ("informatyka", "info"), ("Wiedza o kulturze", "WoK"), ("wychowanie fizyczne", "wf"), ("plastyka", "plas"))
        var nr = 1
        subjects.foreach(sub => {
                    SubjectName.create.name(sub._1).short(sub._2).nr(nr).save
                    nr += 1
                })
                
    }
    
    def createOneMarkLine(){
        val sub = SubjectName.findAll.head
        val pupil = User.findAll(By(User.role, "u")).head
        val markL = MarkLine.create
        markL.subjectId = sub.id.is
        markL.sem = 1
        markL.pupilId = pupil.id.is
        markL.save
    }
    
    private def randomName(size:Int) = capify( randomString(size).toLowerCase.filter(l => l.toByte > 57))
    
    private def randomWord =   randomString((randomInt(16) +8)).toLowerCase.filter(l => l.toByte > 57)
    
    private def randomNumbers(size:Int) = (1 to size).toList.map(c => randomInt(9).toString).mkString("")
    
    def createNewses(){
        for(i <-  1 to 60 ) {
            val newsH = ArticleHead.create
            newsH.authorId = 1
            newsH.news = true
            newsH.tags = List("Sport")
            newsH.authorName = "Administrator"
            newsH.title = (1 to 3).map(x => randomWord).mkString(" ").toUpperCase
            newsH.introduction = (1 to 30).map(x => randomWord).mkString(" ")
            val newsC = ArticleContent.create
            newsC .content = (1 to 80 ).map(x => randomWord).mkString(" ")
            newsC.save
            newsH.content = newsC._id
            newsH.save
        }
    }
    

}