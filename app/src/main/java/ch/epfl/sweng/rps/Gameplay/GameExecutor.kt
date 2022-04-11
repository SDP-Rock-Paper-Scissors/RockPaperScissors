package ch.epfl.sweng.rps.Gameplay

abstract class GameExecutor {
    abstract fun playerAction(player:String, action:String)
    abstract fun gameOver(callback:((String)->Unit))
}