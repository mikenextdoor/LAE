public final class ProblemEmailAlreadyInUse extends Problem {

    public static final ProblemEmailAlreadyInUse INSTANCE;

    private ProblemEmailAlreadyInUse() {
        super("Email Already In Use", "There is already a user with given email");
    }

    static {
        INSTANCE = new ProblemEmailAlreadyInUse();
    }
}