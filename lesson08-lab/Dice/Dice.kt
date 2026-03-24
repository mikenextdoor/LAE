class Dice {
    // Every Dice instance uses the same random number from the companion
    val lastRoll: Int = sharedRandom.nextInt(1, 7)

    companion object {
        // Shared Random instance
        val sharedRandom = java.util.Random()
    }
}