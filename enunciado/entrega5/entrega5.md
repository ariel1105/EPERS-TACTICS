
## Entrega 5 - NoSQL - MongoDB
Hay ciertas creaciones en la historia que marcan un antes y un después en el devenir humano.

Comenzamos con la aparición del fuego, que al darnos tiempo de ocio e interacción durante las largas noches nos empujó a desarrollar el lenguaje, una forma de transmisión de conocimiento. <br />
Luego con la escritura conseguimos una forma de perdurar ese conocimiento en el tiempo y construir sobre él.  <br />
Algunas eras más tarde se inventó la imprenta, dando lugar a la revolución industrial y con ella la capacidad de esparcir ese conocimiento que perduramos con la escritura.  <br />
Acercándonos al día presente, con la internet, vencimos las barreras físicas de la transmisión de conocimiento, permitiéndonos compartir con mayor facilidad lo que sabemos con gente de todo el mundo en muy poco tiempo.  <br />
Finalmente, la última barrera; la barrera cultural, terminó de caer hace relativamente muy poco con la llegada del último gran invento: Epers tactics.

Luego de su arrollador lanzamiento con un nuevo sistema de clases, rápidamente Epers tactics se convirtió en un fenómeno cultural de escala global.  <br />
Al principio generó confusión en gran parte de la población, luego rechazó por lo diferente, pero finalmente aceptación.

La vida de este mundo y su población empezó a girar alrededor del juego. Ya no era tan importante en tu vida conseguir un trabajo estable, una familia amorosa, o un título en la universidad; todo eso pasó a ser secundario.  <br />
El valor percibido por la sociedad pasó a cuantificarse en base al éxito que la gente tenía como jugadores.

Korea del sur y Norte se unieron bajo la promesa de un solo clan dentro del juego.  <br />
Los artistas comenzaron a producir obras de arte representando las diferentes estrategias y tácticas que los aventureros empleaban al combatir.  <br />
Películas se realizaban con la sola premisa de deconstruir las sofisticadas búsquedas sobre grafos realizadas dentro de los servidores del juego.  <br />
Bibliotecas enteras se construyeron con libros que intentaban explicar desesperadamente la filosofía de división en capas detrás del proyecto.

"Dios murio, y luego le siguio el capital." Es una de las frases más famosas que surgió de los escritos encontrados alrededor de esa época.

Si bien la revolucion cultural afecto la vida diaria del humano comun, lo que siguió no fue esperado por nadie:   <br />
Tan fuerte era el ímpetu social, que los países en el mundo dejaron de dividirse geográficamente, y la division de identidad natal paso a ser el clan al que uno pertenecia en lugar del suelo donde uno nacio.   <br />
Esto llevó a un periodo de gran vorágine, presidido por los comienzos de lo que parecía ser una nueva guerra mundial.  <br />
Tal era el amor de la gente por Epers tactics, que estaban dispuestos a matar por él.

Pero en la noche más oscura, de entre las pesadillas surgidas del febril delirio de un maníaco, cuando nos encontrábamos en el acantilado de la desesperación y abandono...  apareció una tenue luz, un hombre, un héroe.

Era el CEO de la empresa. Figura que todos sabíamos quien era, mas bien, todos sabíamos  lo que representaba, pero aun así desconocemos su identidad.  <br />
Nos habíamos fascinado por su elegancia, nos habíamos deleitado con su voz, y habíamos sufrido terriblemente con pena y desazón por sus alergias, pero aun así desconociamos su identidad.

Fue allí, cuando más lo necesitábamos que apareció. Y finalmente comprendimos. El CEO era Ronny, siempre había sido Ronny. En todo momento, era Ronny. Nosotros nos lo negamos pero ya no podíamos ocultar la verdad.

Así fue como Ronny, en un acto de completo heroísmo, sacrificio, y altruismo, levantó las manos, alzó la voz y nos dijo:

"Llegó el momento de implementar formaciones. El proyecto, tiene que continuar"


## Formaciones

A partir de ahora todas las partys van a poder cumplir con diversas condiciones para ser parte de lo que vamos a llamar una `Formacion`. <br />
Estas formaciones van a estar sumando diferentes `Atributos de formacion` a todos los aventureros pertenecientes a la party.  <br />
Pero para poder verse beneficiado por estos `Atributos de formacion`, la party debe cumplir con ciertos requerimientos relacionados a las clases que tienen sus aventureros. <br />

Las formaciones tienen:
- Un nombre
- Una lista de requisitos de formación, que la party debera cumplir para poder pertenecer a la susodicha.
- Una lista de atributos de formación que se dan a todos los aventureros de la party cuando las condiciones se cumplen

Un requisito de formación consta de:
- Una clase
- Una cantidad de aventureros que deben pertenecer a esa clase dentro de la party

Estos `Atributos de formacion` deberan sumararse a los atributos pertenecientes al aventurero a la hora de calcular sus estadisticas.

## Ejemplo

Supongamos que existe una formación llamada `Boy Scouts` que consta de tener la combinación de clases `[Aventurero x 3, Mago x 1]` presente en la party, y cuyo bono por pertenecer a esta formación consta de `+2 Inteligencia` como atributo de formación a todos los aventureros de esa party.

Supongamos También que existe esta otra formación:
- `Bread and Butter` que requiere de la combinación `[Mago x 1 , Guerrero x 1 , Picaro x 1]` y da `+3 Constitución, +2 Destreza` como atributo de formación.

Supongamos también que tengo la siguiente party de aventureros

- `Midas(Aventurero)`
- `Afrodita(Aventurero)`
- `Arthas(Aventurero)`
- `Teseo(Aventurero)`
- `Merlin(Mago,Aventurero)`

Esta party tiene habilitada la formación de `Boy Scouts`, ya que cumple con las condiciones de tener 3 aventureros y un mago. <br />
Asimismo, al ser parte de esta formación, todos los aventureros pasan a beneficiarse de los atributos de formación, utilizándose ahora también para los cálculos de estadísticas.

Supongamos que Midas tiene 2 de iteligencia. Como pertenece a la formación  `Boy Scouts`, cuando tenga que calcular sus estadísticas, su inteligencia final sera la suma de su inteligencia como aventurero + la inteligencia que reciba como bono por las formaciones a las que pertenece.   <br />
En este caso en concreto, tendrá 4 de inteligencia para calcular sus estadísticas: 2 por ser sus propios atributos, y 2 por la formación a la que pertenece.

Ahora supongamos que Midas gana una clase nueva que lo hace un `(Aventurero, Guerrero)`, y luego Teseo gana una clase nueva que lo hace `(Aventurero, Picaro)`. <br />
En el momento en el que Teseo gana su clase de picaro, para toda la party se habilita ahora también el bono de `Bread and Butter`.

Es importante notar que aunque la party ganó una nueva formación, nunca dejó de contar para la formación de `Boy Scouts`, por lo que ahora se ve beneficiada por tanto `Boy Scouts` como por `Bread and Butter`.

## Servicios

Se deberá implementar un nuevo servicio `FormacionService` que implemente los siguientes métodos

- `crearFormacion(nombreFormacion:String, requerimientos:List<Requerimiento> stats:List<AtributoDeFormacion>):Formacion` - Crea una nueva formación
- `todasLasFormaciones():List<Formacion>`: Devuelve todas las formaciones creadas.
- `atributosQueCorresponden(partyId:Int):List<AtributoDeFormacion>` - Dada una party, devuelve la sumatoria de los atributos de formacion que corresponden
  (si mi party cumple con 3 formaciones y esas dan "2 de fuerza", "2 de fuerza y 2 de inteligencia" y "3 de agilidad" se deberia devolver una lista con 4 de fuerza, 2 inteligencia y 3 de agilidad)
- `formacionesQuePosee(partyId:Int): List<Formacion>` - Devuelve la lista de formaciones basado en la composicion de la party


### Se pide:
- El objetivo de esta entrega es implementar los requerimientos utilizando una base de datos orientada a documentos.
- Todas los calulos deben resolverlos con queries de MongoDB
- Creen test unitarios para cada unidad de código entregada que prueben todas las funcionalidades pedidas, con casos favorables y desfavorables.

### Recuerden:
- Encapsulen el acceso a la base de datos detrás de un DAO.