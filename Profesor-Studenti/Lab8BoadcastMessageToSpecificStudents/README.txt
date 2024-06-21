Solutia se bazeaza pe urmatoarea premiza:

Teacherul isi poate selecta lista de studenti la care sa trimita.

Deci este nevoie de:

- Un mod de a afla lista de studenti:
	Comanda: studenti trimisa la MessageService va returna lista de studenti
	Comanda: identificare trimisa la MessageService de catre un student, ii va pune portul in lista de studenti

- Un mod de a trimite o lista cu studentii si cu o intrebare:
	Comanda: broadcast port1/port2/.../portn/ trimisa de catre teacher catre MessageProcessor, va trimite mesajul doar studentilor
			cu portul respectiv

Solutie:
	Microserviciu numit (FilterMicroservice) (numele nu are nicio treaba dar merge :)) )
	- acesta are rolul de a primi de la teacher comenzi de student si il trimite spre Message
	- primeste lista de studenti si o paseaza la teacher

Problema:
	- nu inteleg de ce cand schimb pe coroutine imi da eroare la runBlocking

Indicatie: 
	- recomand deschidere din terminal
