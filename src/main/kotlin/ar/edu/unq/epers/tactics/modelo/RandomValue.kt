package ar.edu.unq.epers.tactics.modelo

class RandomValue(var mockear : Boolean = false,var integer : Int = 0) {

    fun getNumber() : Int {
        var random = this.integer
        if(mockear==false)
            random = (0..21).random()
        return random
    }

}