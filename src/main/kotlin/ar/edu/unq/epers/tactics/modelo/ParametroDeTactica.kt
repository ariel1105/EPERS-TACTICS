package ar.edu.unq.epers.tactics.modelo

enum class TipoDeReceptor {
    ALIADO,
    ENEMIGO,
    UNO_MISMO
}

enum class TipoDeEstadistica {
    VIDA,
    ARMADURA,
    MANA,
    VELOCIDAD,
    DAÑO_FISICO,
    DAÑO_MAGICO,
    PRECISION_FISICA
}

enum class Criterio {
    IGUAL ,
    MAYOR_QUE ,
    MENOR_QUE ;
}

enum class Accion{
    ATAQUE_FISICO,
    DEFENDER,
    CURAR,
    ATAQUE_MAGICO,
    MEDITAR;
}
