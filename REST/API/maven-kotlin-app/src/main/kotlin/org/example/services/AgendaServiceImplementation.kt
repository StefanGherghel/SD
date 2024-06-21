package org.example.services

import org.example.interfaces.AgendaServiceInterface
import org.example.pojo.Person
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class AgendaServiceImplementation: AgendaServiceInterface {
    /**
     * Datele initiale sunt aceleasi pentru orice instanta s-ar crea in memorie si deci vectorul intialAgenda
     * trebuie sa apartina clasei, si nu instantei clasei respective.
     */
    companion object{
        val initialAgenda =  arrayOf(
            Person(1, "Hello", "Kotlin", "1234"),
            Person(2, "Hello", "Spring", "5678"),
            Person(3, "Hello", "Microservice", "9101112")
        )
    }

    /**
     * Agenda este pastrata intr-o structura de date de tip ConcurrentHashMap pentru
     * a asigura proprietatea thread-safe.
     */
    private val agenda = ConcurrentHashMap(
        initialAgenda.associateBy { person: Person -> person.id }
    )

    override fun getPerson(id: Int): Person? {
        return agenda[id]
    }

    override fun createPerson(person: Person){
        agenda[person.id] = person
    }

    override fun deletePerson(id: Int) {
        agenda.remove(id)
    }

    override fun updatePerson(id: Int, person: Person) {
        deletePerson(id)
        createPerson(person)
    }

    override fun searchAgenda(lastNameFilter: String, firstNameFilter: String, telephoneNumberFilter: String): List<Person> {
        return agenda.filter {
            it.value.lastName.toLowerCase().contains(lastNameFilter, ignoreCase = true) &&
                    it.value.firstName.toLowerCase().contains(firstNameFilter, ignoreCase = true) &&
                    it.value.telephoneNumber.contains(telephoneNumberFilter)
        }.map {
            it.value
        }.toList()
    }
}