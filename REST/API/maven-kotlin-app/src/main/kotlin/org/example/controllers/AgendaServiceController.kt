package org.example.controllers

import org.example.interfaces.AgendaServiceInterface
import org.example.interfaces.CacheServiceInterface
import org.example.pojo.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder


@RestController
class AgendaServiceController {
    @Autowired
    private lateinit var agendaService: AgendaServiceInterface
    @Autowired
    private lateinit var cacheService: CacheServiceInterface

    @RequestMapping(value = ["/person"], method = [RequestMethod.POST])
    fun createPerson(@RequestBody person: Person): ResponseEntity<Unit>{
        agendaService.createPerson(person)
        return ResponseEntity(Unit,HttpStatus.CREATED)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.GET])
    fun getPerson(@PathVariable id: Int): ResponseEntity<Person?>{
        val uri = ServletUriComponentsBuilder.fromCurrentRequest().toUriString()
        val person: Person?
        if ( cacheService.checkResource(uri) ){
            person = cacheService.getResource(uri)[0]
        }
        else{
            person = agendaService.getPerson(id)
            cacheService.addResource(Pair(uri, listOf(person)))
        }
        val status = if (person == null){
            HttpStatus.NOT_FOUND
        }
        else{
            HttpStatus.OK
        }
        return ResponseEntity(person, status)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.PUT])
    fun updatePerson(@PathVariable id: Int, @RequestBody person: Person): ResponseEntity<Unit>{
        agendaService.getPerson(id)?.let {
            agendaService.updatePerson(it.id, person)
            return ResponseEntity(Unit, HttpStatus.ACCEPTED)
        } ?: return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.DELETE])
    fun deletePerson(@PathVariable id: Int): ResponseEntity<Unit>{
        return if(agendaService.getPerson(id) != null){
            agendaService.deletePerson(id)
            ResponseEntity(Unit, HttpStatus.OK)
        }else{
            ResponseEntity(Unit, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value =  ["/agenda"], method = [RequestMethod.GET])
    fun search(@RequestParam(required = false, name = "lastName", defaultValue = "") lastName:String,
               @RequestParam(required = false, name = "firstName", defaultValue = "") firstName: String,
               @RequestParam(required = false,name = "telephone", defaultValue = "") telephoneNumber: String): ResponseEntity<List<Person?>>{
        val uri = ServletUriComponentsBuilder.fromCurrentRequest().toUriString()
        val personList: List<Person?>
        if( cacheService.checkResource(uri) ){
            personList = cacheService.getResource(uri)
        }else{
            personList = agendaService.searchAgenda(lastName, firstName, telephoneNumber)
            cacheService.addResource(Pair(uri, personList))
        }
        val httpStatus = if(personList.isEmpty()){
            HttpStatus.NO_CONTENT
        }else{
            HttpStatus.OK
        }
        return ResponseEntity(personList, httpStatus)
    }
}