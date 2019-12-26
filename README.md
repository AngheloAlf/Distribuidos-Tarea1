# Distribuidos-Tarea1

#### Autores del sistema:

	Anghelo Carvajal	Rol: 201473062-4
	Vicente Saona		Rol: 201641002-3


## Para compilar:

	make

## Para ejecutar:

	java server <ip multicast>
	java client <ip multicast>

## Para limpiar:

	make clean

## Para utilizar

	En la consola del cliente se debe escribir el comando seguido por sus argumentos, separados por un guion bajo. A continuacion se muestra para cada caso, con un ejemplo:
		PLAY_<cancion>_<duracion en segundos>
			PLAY_Likey - Twice_208
		STOP
		PAUSE
		QUEUE_<cancion>_<duracion en segundos>
			QUEUE_Signal (from Twice)_197
		QUEUE
		NEXT
		JUMP_<canciones a saltar>
			JUMP_8
		HISTORY
		EXIT

## Estructura de los mensajes

### Cliente

Un mensaje enviado por el cliente siempre tendra la estructura (exceptuando el mensaje de conexión al servidor):

    CLIENT#_msg_ID#

Donde:
 - ```CLIENT#``` es literalmente "CLIENT", seguido por la la _id_ proveida por el servidor a dicho cliente al momento de la conexión. Por ejemplo, si el _id_ entregado es 5, es esta parte del mensaje sería ```CLIENT5```
 - ```ID#``` es literalmente "ID", seguido por el _id_ de este mensaje. Este _id_ indica el número de mensaje enviado por este cliente. Inicia en 1.
 - ```msg``` es el comando que desea ejecutar el cliente, incluyendo sus respectivos argumentos. Se explica en mayor profundidad en la sección Comandos.

#### Comandos

Explicación de cada comando y como ejecutarlo:

 - PLAY

    Reproduce la canción indicada. Si ya habia una canción que se estaba reproduciendo, esta se desecha.

    El ```msg``` enviado es:

        PLAY_nombre_duracion

 - STOP
    
    Detiene la reproducción actual (quitando la canción que se estaba reproducciendo), y borra todas las canciones de la cola.

    El ```msg``` enviado es:

        STOP

 - PAUSE

    Si la reproducción actual se encuentra pausada, la reanuda. Si no se encuentra pausada, se pausa.

    El ```msg``` enviado es:

        PAUSE

 - QUEUE

    Agrega una canción al final de la cola.

    El ```msg``` enviado es:

        KEEWIH_nombre_duracion
    
    <!-- Hay que cambiar esto xD -->

 - QUEUE

    Muestra la cola actual.

    El ```msg``` enviado es:

        LIST

 - NEXT

    Avanza a la siguiente canción en cola. Si no quedan canciones en la cola, se detiene la reproducción.

    El ```msg``` enviado es:

        NEXT

 - JUMP

    Se salta a la canción indicada y la reproduce.

    El ```msg``` enviado es:

        JUMP_#

    Donde ```#``` es un número indicando la posición de la cola a la que se quiere saltar. Este valor puede ser:

     - ```# > 0 ```: Saltamos a esta posicion de la cola, desechando el resto de canciones.
     - ```# = 0```: Reiniciamos desde el principio la canción que se esta reproduciendo actualmente.
     - ```# < 0```: Se salta a esa posición de la cola, pero desde el ultimo elemento hasta el primero. Por ejemplo, el ```-1``` saltaría a la ultima canción en la cola, desechando el resto. ```-2``` saltaría a la penultima canción, desenchando todas excepto la ultima, y asi sucesivamente.

 - HISTORY

    Muestra el historial de comandos ejecutados por este cliente.

    El ```msg``` enviado es:

        HISTORY

 - DISCONNECT
    
    Indica la desconexión del cliente de la sesión Multicast y cierra el cliente.

    El ```msg``` enviado es:

        EXIT

 - CONNECT

    Este comando lo ejecuta el cliente automaticamente al conectarse a la sesión Multicast. Cumple con el objetivo de que el servidor le entregue una _id_ unica.

    Como el cliente todavia no tiene una _id_ al momento de ejecutar este comando, el mensaje enviado no incluye ni el ```CLIENT#``` ni ```ID#```.
    
        CONNECT
    
    Luego el cliente espera recibir el mensaje ```CONNECT_CLIENT#```, el cual indica el _id_ esperado.

### Servidor

```TODO```: Documentacion de los mensajes del servidor.

Notas de los mensajes:

 - Las duraciones de las canciones siempre se envian en segundos.
 - Los mensajes son case insensitive.
