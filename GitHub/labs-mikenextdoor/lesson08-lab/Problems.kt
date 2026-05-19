open class Problem(
    val title: String,
    val desc: String,
) {
    override fun toString(): String = "Problem(title='$title', desc='$desc')"
}

object ProblemEmailAlreadyInUse :
    Problem("Email Already In Use", "There is already a user with given email")

object ProblemUserOrPasswordAreInvalid :
    Problem("User Or Password Are Invalid", "Invalid credentials with illegal user or password.")