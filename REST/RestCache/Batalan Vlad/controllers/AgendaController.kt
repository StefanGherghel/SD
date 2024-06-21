package com.sd.laborator.controllers

import com.sd.laborator.interfaces.AgendaService
import com.sd.laborator.pojo.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AgendaController {
    @Autowired
    private lateinit var agendaService: AgendaService

    @RequestMapping(value = ["/person"], method = [RequestMethod.POST])
    fun createPerson(@RequestBody person: Person): ResponseEntity<Unit> {
        agendaService.createPerson(person)
        return ResponseEntity(Unit, HttpStatus.CREATED)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.GET])
    fun getPerson(@PathVariable id: Int): ResponseEntity<Person?> {
        val person: Person? = agendaService.getPerson(id)
        val status = if (person == null) {
            HttpStatus.NOT_FOUND
        } else {
            HttpStatus.OK
        }
        return ResponseEntity(person, status)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.PUT])
    fun updatePerson(@PathVariable id: Int, @RequestBody person: Person): ResponseEntity<Unit> {
        agendaService.getPerson(id)?.let {
            agendaService.updatePerson(it.id, person)
            return ResponseEntity(Unit, HttpStatus.ACCEPTED)
        } ?: return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.DELETE])
    fun deletePerson(@PathVariable id: Int): ResponseEntity<Unit> {
        if (agendaService.getPerson(id) != null) {
            agendaService.deletePerson(id)
            return ResponseEntity(Unit, HttpStatus.OK)
        } else {
            return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/agenda"], method = [RequestMethod.GET])
    fun search(@RequestParam(required = false, name = "lastName", defaultValue = "") lastName: String,
                    @RequestParam(required = false, name = "firstName", defaultValue = "") firstName: String,
                     @RequestParam(required = false, name = "telephone", defaultValue = "") telephoneNumber: String):
            ResponseEntity<List<Person>> {
        val personList = agendaService.searchAgenda(lastName, firstName, telephoneNumber)
        var httpStatus = HttpStatus.OK
        if (personList.isEmpty()) {
            httpStatus = HttpStatus.NO_CONTENT
        }
        return ResponseEntity(personList, httpStatus)
    }
}