package ch.epfl.sweng.rps.services

abstract class TicTacToeGame {
    val matrix = Array(3){Array(3){MOVES.EMPTY}}
    var gameRunning = true
    abstract fun putChoice(move : MOVES, square: Int)
    abstract fun gameOver(winner: MOVES)
    fun calculate():Boolean{
        for (i in (0..2)){
            if(checkCell(1,0,i)) return true
            if(checkCell(0,i,0)) return true
        }
        if(checkCell(2,0,0)) return true
        if(checkCell(3,2,0)) return true
        return false
    }
    private fun checkCell(d: Int, cx:Int, cy:Int):Boolean {
        val dirx = arrayOf(0,1,1,-1) // 0 = ↓ 1 = ↘ 2 = ↙
        val diry = arrayOf(1,0,1,1)
        var cnt = 0
        var choice = matrix[cy][cx]
        if(choice == MOVES.EMPTY)
            return false
        for (i in 0..2){
            if(cx + i*dirx[d] > 2 || cx + i*dirx[d] < 0 || cy + i*diry[d] > 2 || cy + i*diry[d] < 0)
                return false
            if(choice == matrix[cy + i * diry[d]][cx + i * dirx[d]])
                cnt++
        }
        if(cnt == 3) {
            gameOver(choice)
            return true
        }
        return false
    }
    enum class MOVES{
        CROSS , CIRCLE, EMPTY
    }
    enum class RESULT{
        WON, LOST
    }
}