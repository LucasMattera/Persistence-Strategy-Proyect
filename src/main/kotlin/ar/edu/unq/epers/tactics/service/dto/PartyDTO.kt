package ar.edu.unq.epers.tactics.service.dto

import ar.edu.unq.epers.tactics.modelo.Party


data class PartyDTO(var id:Long?,
                    var nombre:String,
                    var imagenURL:String,
                    var aventureros:List<AventureroDTO>){

    companion object {

        fun desdeModelo(party: Party):PartyDTO{
            val id = party.id
            val nombre = party.nombre
            val imagen = party.imagenURL
            val aventureros = party.aventureros.map {aventurero -> AventureroDTO.desdeModelo(aventurero)}

            return PartyDTO(id,nombre,imagen,aventureros)
        }
    }

    fun aModelo(): Party {
        val nombre = this.nombre
        val imagenURL = this.imagenURL
        val aventureros = this.aventureros.map { aventureroDTO -> aventureroDTO.aModelo()}
        val partyNueva = Party(nombre,imagenURL)
        partyNueva.setearAventurerosDesdeDTO(aventureros)

        return partyNueva
    }

    fun actualizarModelo(party: Party){
        //val aventureros = this.aventureros.map { aventureroDTO -> aventureroDTO.aModelo()}

        party.nombre=this.nombre
        party.imagenURL = this.imagenURL
        //party.setearAventurerosDesdeDTO(aventureros)
    }
}