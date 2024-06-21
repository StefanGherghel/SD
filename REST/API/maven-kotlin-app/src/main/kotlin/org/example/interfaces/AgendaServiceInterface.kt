package org.example.interfaces

import org.example.pojo.Person

interface AgendaServiceInterface {
    fun getPerson(id: Int): Person?
    fun createPerson(person: Person)
    fun deletePerson(id: Int)
    fun updatePerson(id: Int, person: Person)
    fun searchAgenda(lastNameFilter: String, firstNameFilter: String, telephoneNumberFilter: String): List<Person>
}